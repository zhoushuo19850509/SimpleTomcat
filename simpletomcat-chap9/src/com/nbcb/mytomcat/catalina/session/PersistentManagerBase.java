package com.nbcb.mytomcat.catalina.session;


import com.nbcb.mytomcat.catalina.util.Constants;
import org.apache.catalina.*;

import java.io.IOException;
import java.util.List;


/**
 * PersistentManagerBase是持久化Manager的基类
 * 和StandardManager一样，也会启动异步线程定期检测过期的session对象。
 * PersistentManagerBase新增的功能是会对session做持久化处理
 *
 */
public class PersistentManagerBase extends ManagerBase
    implements Runnable, Lifecycle

{


    /**
     * 某个session如果空闲时间超过了一定时间，
     * PersistentManagerBase会把这个session swap to persistence
     * 当然，要满足的条件：
     * 空闲时间超过minIdleSwap和maxIdleSwap
     */
    private int minIdleSwap = -1;
    private int maxIdleSwap = -1;

    /**
     * 这个Store实例，主要是用于当前PersistentManagerBase访问持久化设备
     * 保存session到持久化设备、从持久化设备获取session对象
     */
    private Store store;


    /**
     * 当前PersistentManagerBase代表的线程
     */
    private Thread thread = null;


    /**
     * @用于统计
     * 在本轮异步线程处理中
     * 有多少session是空闲时间过长，被直接回收了
     */
    private int maxIdleExpireCount = 0;

    /**
     * @用于统计
     * 在本轮异步线程处理中
     * 有多少session是因为空闲时间过长，被放到持久化层
     */
    private int maxIdleSwapCount = 0;

    /**
     * @用于统计
     * 在本轮异步线程处理中
     * 有多少session是因为内存中空闲session过多，被放到持久化层
     */
    private int maxActiveSwapCount = 0;

    /**
     * @用于统计
     * 在本轮异步线程处理中
     * 有多少session是因为空闲时间过长，备份到持久化层
     */
    private int maxIdleBackups = 0;

    /**
     * 1.定期检测失效的session
     * 2.定期将session持久化
     */
    @Override
    public void run() {
        while(true){
            try {
                Thread.sleep(Constants.CHECK_INTERVAL * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            printSessionStatusBefore();
            processExpires();
            processPersistenceChecks();
            printSessionStatusAfter();
        }
    }


    /**
     * 打印异步线程处理之前各个session的状态
     */
    public void printSessionStatusBefore(){

        /**
         * 在异步线程处理前，内存中的session有多少
         */
        System.out.println("PersistentManagerBase before run()");
        System.out.println("current session count(in memory) : " + this.findSessions().size());
        System.out.println("current session count(recycled) : " + super.getRecycled().size());

        /**
         * 本次异步线程处理前，先把上一次异步线程的数据清理一下
         */
        this.maxIdleExpireCount = 0;
        this.maxIdleSwapCount = 0;
        this.maxActiveSwapCount = 0;
        this.maxIdleBackups = 0;

    }

    /**
     * 打印当前Manager管理的各个session的状态(经过异步线程处理之后)
     * 包括仅仅在内存中的、仅仅保存在持久化层中的、
     * 备份到持久化层的(内存中和持久化层中都有)、处于回收队列的
     */
    public void printSessionStatusAfter(){

        /**
         * 在异步线程处理后，保留在内存中的session还剩多少
         */
        System.out.println("PersistentManagerBase after run()");
        System.out.println("current session count(in memory) : " + super.findSessions().size());
        System.out.println("current session count(recycled) : " + super.getRecycled().size());

        /**
         * 如果内存中有session信心，就打印一下sid
         */
        StringBuilder sb = new StringBuilder();
        if(super.findSessions().size() > 0){
            for(Session session : super.findSessions()){
                sb.append(session.getId() + ",");
                System.out.println("print session info " + session);
            }
            System.out.println("current session id(s)(in memory) : " + sb.toString());
        }

        /**
         * 本轮异步线程，有多少session是因为idle时间过长，直接被回收
         * idle时间参考： Constants.MAX_INACTIVE_INTERVAL
         * 目前是60s
         * 正常来说，这里不会有数据应该idle时间过长被Manager直接回收了
         * 因为idle时间过程的一般都被放到持久化层了
         */


        /**
         * 本轮异步线程，有多少线程是因为IDLE时间过长，被swap out to persistence
         * idle时间参考：
         * Constants.MIN_IDLE_SWAP = 10
         * MAX_IDLE_SWAP = 30
         * 目前是30s
         */
        System.out.println("PersistentManagerBase 将那些空闲时间过长的session放到持久层。" +
                "数据条数： " + this.maxIdleSwapCount);

        /**
         * 本轮异步线程，有多少session是因为内存中session过多，被swap out to persistence
         * 最多线程数参考： Constants.MAX_ACTIVE_SESSIONS
         *  目前是50个
         */
        System.out.println("PersistentManagerBase 将那些内存中过多的session放到持久层。" +
                "数据条数： " + this.maxActiveSwapCount);

        /**
         * 本轮异步线程，有多少线程是因为IDLE时间过长，被backup to persistence
         * idle时间参考：
         * Constants.MAX_IDLE_BACKUP
         * 目前是20s
         */

    }

    /**
     * 持久化处理，包括3个处理内容：
     * 1.将长期空闲的session放到持久化层；
     * 2.将过多的活跃session放到持久化层；
     * 3.将过多的空闲session备份到持久化层
     */
    public void processPersistenceChecks(){
        processMaxIdleSwaps();
        processMaxActiveSwaps();
        processMaxIdleBackups();
    }


    /**
     * 处理那些(内存中)过期的session对象
     * 处理逻辑和StandardManager差不多，就是把那些超时的session设置为expire并回收
     * 因为这个Manager虽然是PersistentManger，但是还是会有一部分session放在内存中
     * 这部分session需要当前PersistentMangerBase进行处理，定期把过期session清理掉
     */
    public void processExpires(){

        /**
         * 检查(内存中)各个session最近一次登录的时间
         * 如果最近一次登录的时间和当前时间相比，超过了一定的时长，就将这个session设置为超时
         */
        List<Session> sessionList = findSessions();
        long now = System.currentTimeMillis();
        for(Session session : sessionList){
            String sid = session.getId();

            /**
             * 最近一次访问时间
             */
            long lastAccessTime = session.getLastAccessedTime();

            /**
             * 现在距离最近一次访问已经过去了多少时间(ms)
             */
            long duration = (int) ((now - lastAccessTime) / 1000L);

//            System.out.println("session in memory(sid: " + sid + ") " +
//                    " 距离最近一次访问过去了: " +duration + " s" );
            int maxInactiveInterval = session.getMaxInactiveInterval();
            if(duration > maxInactiveInterval){
                session.expire();
                System.out.println("session in memory has expired! sid: " + sid);
                maxIdleExpireCount++;
            }
        }
    }

    /**
     * 下面三个方法，
     * 1.processMaxIdleSwaps；
     * 2.processMaxActiveSwaps；
     * 3.processMaxIdleBackups；
     *
     * 是PersistentManagerBase对session对象实现持久化管理。
     * 啥意思呢？就是内存中的session对象空闲了很久，
     * 或者活跃的session对象太多达到了MaxActive的配置数量。
     * 那么PersistentManagerBase会把这些session Swap out到持久层(文件系统或者数据库)。
     * 以保证内存中的session不会过多，影响tomcat JVM的性能。
     *
     * 后续要用到session的时候，再通过Swap in，从持久层加载到内存
     */

    /**
     * 方法1 processMaxIdleSwaps()
     * 实现逻辑是这样的：
     * 遍历当前Manager管理的session列表，一旦发现某个线程的空闲时间超过了阈值
     * 就swap out to persistence
     */
    public void processMaxIdleSwaps(){

        /**
         * 当前Manager管理的内存中的session列表
         */
        List<Session> sessionList = findSessions();

        /**
         * 当前时间
         */
        long now = System.currentTimeMillis();

        /**
         * session空闲相关的参数
         */
        minIdleSwap = Constants.MIN_IDLE_SWAP;
        maxIdleSwap = Constants.MAX_IDLE_SWAP;

        if(maxIdleSwap < 0){
            return ;
        }

        /**
         * 遍历session列表
         */
        for(Session session : sessionList){

            if(!session.isValid()){
                continue;
            }

            String sid = session.getId();

            /**
             * 最近一次访问时间
             */
            long lastAccessTime = session.getLastAccessedTime();

            /**
             * 现在距离最近一次访问已经过去了多少时间(ms)
             */
            long duration = (int) ((now - lastAccessTime) / 1000L);

//            System.out.println("sid: " + sid + " 距离最近一次访问过去了: " +duration + " s" );

            if(duration > minIdleSwap && duration > maxIdleSwap){

                System.out.println("session(sid:" + sid + ") keep idle too long! " +
                                "Idle time : " + duration + " seconds. " +
                                "So swap out to persistence! ");
                swapOut(session);

                this.maxIdleSwapCount++;
            }
        }

    }

    /**
     * Swap out the session from memory to persistence
     * Swap out包括3步：
     * 1.保存到持久化
     * 2.将这个session从当前manager session队列中删除；
     * 3.回收这个session
     * @param session
     */
    public void swapOut(Session session){

        /**
         * 把内存中的session放到持久化层
         */
        writeSession(session);

        /**
         * 将Manger管理的这个session从Manager中删除
         */
        super.remove(session);

        /**
         * 最后，将这个session回收
         */
        session.recycle();

    }

    /**
     * 重载方法
     * 在这里要重写一下findSession方法，因为对于PersistentManager来说，
     * session可能在内存，也有可能在持久化层
     *
     * @param id The session id for the session to be returned
     *
     * @return
     * @throws IOException
     */
    @Override
    public Session findSession(String id) throws IOException {

        /**
         * 先尝试从内存(Manager session队列)取
         */
        Session session = super.findSession(id);
        if(null != session){
            return session;
        }

        /**
         * 再尝试从持久化层取
         */
        session = swapIn(id);

        return session;
    }


    /**
     * Swap in the session object from persistence to memory
     */
    public Session swapIn(String id) throws IOException {
        if(null == store){
            return null;
        }

        Session session = null;

        try {
            session = store.load(id);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        /**
         * 没法从持久化层加载到想要的session，说明持久化层没有对应的session
         */
        if(null == session ){
            return null;
        }

        /**
         * 判断从持久层load进来的session是否失效了
         * 如果失效的话，那load进来的session也没啥用
         * 直接从持久层删除就行了
         */
        if(!session.isValid() || isSessionStale(session)){
            session.expire();
            store.remove(id);
        }

        session.setManager(this);

        /**
         * 把从持久化加载的session加入Manager的session队列
         */
        add(session);


        return null;
    }


    /**
     * 判断当前session是否已经超时
     * @param session
     * @return 如果session已经超时，就返回true
     */
    protected boolean isSessionStale(Session session){
        /**
         * 最近一次访问时间
         */
        long lastAccessTime = session.getLastAccessedTime();

        long now = System.currentTimeMillis();

        /**
         * 现在距离最近一次访问已经过去了多少时间(ms)
         */
        long duration = (int) ((now - lastAccessTime) / 1000L);

        int maxInactiveInterval = session.getMaxInactiveInterval();
        if(maxInactiveInterval > 0){
            if(duration > maxInactiveInterval){
                return true;
            }
        }

        return false;
    }



    /**
     * 如果当前PersistentManager管理的session数过多，就swap out to persistent
     *
     */
    public void processMaxActiveSwaps(){
//        System.out.println("start process processMaxActiveSwaps");

        /**
         * 当前内存中的session列表
         */
        List<Session> sessions = super.findSessions();


        /**
         * 找出那些过多的session
         */
        int maxActiveSessions = Constants.MAX_ACTIVE_SESSIONS;

        if(maxActiveSessions < 0){
            return ;
        }

        int toSwap = 0;
        if(sessions.size() > maxActiveSessions){
            toSwap = sessions.size() - maxActiveSessions;
        }

        long now = System.currentTimeMillis();

        /**
         * 遍历这些过多的session
         */
        for (int i = 0; i < sessions.size() && toSwap > 0; i++) {
             Session session = sessions.get(i);


            /**
             * 最近一次访问时间
             */
            long lastAccessTime = session.getLastAccessedTime();

            /**
             * 现在距离最近一次访问已经过去了多少时间(ms)
             */
            long duration = (int) ((now - lastAccessTime) / 1000L);

            /**
             * 如果session的空闲时间达到一定时间，就swap out to persistent
             */
            if(duration > Constants.MIN_IDLE_SWAP){
                 swapOut(session);
                 toSwap--;
                System.out.println("Session exceed the MAX_ACTIVE_SESSIONS swap out to persistence!" +
                        " sid: " + session.getId());

                this.maxActiveSwapCount++;
            }
        }



    }

    /**
     * 将空闲时间过长的session备份到持久化层
     * 这个方法和processMaxIdleSwaps的区别是
     * 这个方法只是把session"备份"到持久化层，并不会删除内存中的session信息
     * 而processMaxIdleSwaps是直接把session swap out to persistence
     * 同时将内存中的session信息删除
     */
    public void processMaxIdleBackups(){

        /**
         * 当前Manager管理的内存中的session列表
         */
        List<Session> sessionList = findSessions();

        /**
         * 当前时间
         */
        long now = System.currentTimeMillis();



        if(Constants.MAX_IDLE_BACKUP < 0){
            return ;
        }

        /**
         * 遍历session列表
         */
        for(Session session : sessionList){

            if(!session.isValid()){
                continue;
            }

            String sid = session.getId();

            /**
             * 最近一次访问时间
             */
            long lastAccessTime = session.getLastAccessedTime();

            /**
             * 现在距离最近一次访问已经过去了多少时间(ms)
             */
            long duration = (int) ((now - lastAccessTime) / 1000L);

//            System.out.println("sid: " + sid + " 距离最近一次访问过去了: " +duration + " s" );

            /**
             * 在备份之前，先判断一下，这个session之前是否有备份过
             * 如果之前已经备份过，并且备份之后没有再被访问过，就无需再重复备份了
             */
            if(null !=session.getNote(Constants.PERSISTED_LAST_ACCESSED_TIME)
               && session.getNote(Constants.PERSISTED_LAST_ACCESSED_TIME) == Long.valueOf(lastAccessTime)){
                continue;
            }

            if(duration > Constants.MAX_IDLE_BACKUP){

                System.out.println("session(sid:" + sid + ") keep idle too long! " +
                        "Idle time : " + duration + " seconds. " +
                        "Backup to persistence! ");
                writeSession(session);

                /**
                 * 备份完成之后，将当时的时间点保存到session的notes属性中
                 * 防止后续重复备份
                 */
                session.setNote(Constants.PERSISTED_LAST_ACCESSED_TIME,Long.valueOf(lastAccessTime));

                maxIdleBackups++;
            }
        }
    }

    /**
     * 调用Store接口，将session写入持久化层
     * @param session
     */
    protected void writeSession(Session session){
        if(null == store){
            return;
        }

        try {
            store.save(session);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断某个session id是否在内存中
     * @param id
     * @return
     */
    public boolean isLoaded(String id){
        try {
            /**
             * 通过ManagerBase来加载某个Session
             */
            Session session = super.findSession(id);
            if(null != session){
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    @Override
    public void addLifecycleListener(LifecycleListener listener) {

    }

    @Override
    public LifecycleListener[] findLifecycleListeners() {
        return new LifecycleListener[0];
    }

    @Override
    public void removeLifecycleListener(LifecycleListener listener) {

    }

    @Override
    public void start() throws LifecycleException {


        /**
         * 如果当前PersistentManagerBase没有设置store对象
         * 那这个PersistentManagerBase也就没啥用了
         */
        if(null == store){
            System.out.println("PersistentManagerBase has no Store implement!");
        }

        /**
         * 启动Store(主要是启动StoreBase)
         */
        if(store instanceof Lifecycle){
            ((Lifecycle) store).start();
        }

        threadStart();

    }

    public void threadStart(){

        thread = new Thread(this, "PersistentManagerBase");
        thread.setDaemon(true);
        thread.start();

    }

    @Override
    public void stop() throws LifecycleException {

    }


    /**
     * setter()/gett()
     */
    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
        store.setManager(this);
    }
}
