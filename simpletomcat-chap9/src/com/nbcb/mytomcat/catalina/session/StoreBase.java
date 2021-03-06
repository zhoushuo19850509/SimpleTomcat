package com.nbcb.mytomcat.catalina.session;

import com.nbcb.mytomcat.catalina.util.Constants;
import org.apache.catalina.*;

import java.beans.PropertyChangeListener;
import java.io.IOException;

public abstract class StoreBase implements Lifecycle ,Store ,Runnable{

    /**
     * 当前StoreBase代表的异步线程
     */
    protected Thread thread = null;

    /**
     * Store类对应的Manager
     */
    protected Manager manager = null;

    /**
     * @用于统计
     * 在本轮StoreBase异步线程处理中
     * 有多少session是空闲时间过长，被直接回收了
     */
    private int maxIdleExpireCount = 0;



    @Override
    public String getInfo() {
        return null;
    }

    @Override
    public Manager getManager() {
        return this.manager;
    }

    @Override
    public void setManager(Manager manager) {
        this.manager = manager;
    }

    @Override
    public int getSize() throws IOException {
        return 0;
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {

    }



    @Override
    public Session load(String id) throws ClassNotFoundException, IOException {
        return null;
    }

    @Override
    public void clear() throws IOException {

    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {

    }

    @Override
    public void save(Session session) throws IOException {

    }

    /**
     * StoreBase会启动一个单独的线程，检测过期的session
     */
    @Override
    public void run() {

        while(true){
            try {
                Thread.sleep(Constants.CHECK_INTERVAL_STOREBASE * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            printStoreSessionStatusBefore();
            processExpire();
            printStoreSessionStatusAfter();
        }

    }

    /**
     * 打印一下那些由StoreBase处理的session信息(异步线程执行后)
     */
    public void printStoreSessionStatusBefore(){
        /**
         * 打印StoreBase本轮异步线程处理前
         * 在持久化层，有多少session
         */
        try {
            System.out.println("StoreBase before run()");
            System.out.println("[StoreBase]current session count(in file system) : " + keys().length);
            System.out.println("[StoreBase]current session count(in memory) : " + this.manager.findSessions().size());
            System.out.println("[StoreBase]current session recycled : " + ((ManagerBase)this.manager).getRecycled().size());

            /**
             * 同时，在异步线程处理前，把上一轮异步线程的统计数据清空一下
             */
            maxIdleExpireCount = 0;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 打印一下那些由StoreBase处理的session信息(异步线程执行前)
     */
    private void printStoreSessionStatusAfter(){


        /**
         * 打印StoreBase本轮异步线程处理中，回收了多少持久化层中需要expire的session
         * 具体参数参考Constants.MAX_INACTIVE_INTERVAL
         * 目前是60s
         */

        /**
         * 打印StoreBase本轮异步线程处理之后
         * 在持久化层，还剩多少session
         */
        try {
            System.out.println("StoreBase after run()");
            System.out.println("current session count(in file system) : " + keys().length);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * 处理过期的session
     * StoreBase中的处理的超时session，主要是那些保存在持久化层的session
     *
     */
    public void processExpire(){
        try {
            /**
             * 先通过具体的Store实现类，获取持久化层中保存的session的session id列表
             */
            String[] keys = keys();
            System.out.println("持久化中现在有多少session文件： " + keys.length);

            long now = System.currentTimeMillis();
            for(String key: keys){

                /**
                 * 现将持久化层中的session文件加载进内存来
                 */
                Session session = load(key);
                /**
                 * 一旦发现session长久没有访问了，就将这个session expire掉
                 */
                /**
                 * 最近一次访问时间
                 */
                long lastAccessTime = session.getLastAccessedTime();

                /**
                 * 现在距离最近一次访问已经过去了多少时间(ms)
                 */
                long duration = (int) ((now - lastAccessTime) / 1000L);

                System.out.println("session in persistent(sid: " + key + ") " +
                        " 距离最近一次访问过去了: " +duration + " s" );
                int maxInactiveInterval = session.getMaxInactiveInterval();
                if(duration > maxInactiveInterval){

                    /**
                     * 如果当前已经超时的session已经加载到内存中了.
                     * 那只要把我们从持久层load进来的session对象只要标记为回收就行了，
                     * PersistentManagerBase管理的内存中的session对象,Manager自己就会处理
                     * StoreBase就别管了
                     *
                     */
                    if( ((PersistentManager)this.manager).isLoaded(key) ){
                        session.recycle();
                        System.out.println("persistent session(sid:" + session.getId() + ") " +
                                "keep idle too long! " +
                                "Idle time : " + duration + " seconds. " +
                                "So recycle from persistence! ");
                    }else{
                        /**
                         * 如果当前session没有加载到内存，还在持久化层，StoreBase就要完成expire工作
                         * 因为expire工作的内容相对多一点，当前session如果还在持久化层，
                         * 就不用再麻烦Load到Manger处理了
                         * StoreBase这边就直接处理掉了。
                         */
                        System.out.println("persistent session(sid:" + session.getId() + ") " +
                                "keep idle too long! " +
                                "Idle time : " + duration + " seconds. " +
                                "So expire from persistence! ");
                        session.expire();
                    }

                    /**
                     * 最后调用Store具体的实现类，将这个过期的session从持久化层删除
                     * 比如FileStore，把这个session从文件系统中删除
                     * 比如JDBCStore，把这个session从数据库删除
                     */
                    remove(key);

                    System.out.println("session in persistent has expired! sid: " + key);
                    maxIdleExpireCount++;
                }
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    /**
     * 以下方法实现了Lifecycle接口
     * @param listener The listener to add
     */
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


    /**
     * StoreBase启动的时候，需要启动一个异步线程，定期检测持久化层中过期的session
     * 那么StoreBase是在哪里启动的呢？一般是由对应的持久化Manager启动的时候拉起来的
     * 参考PersistentManagerBase.start()
     * @throws LifecycleException
     */
    @Override
    public void start() throws LifecycleException {
        System.out.println("start StoreBase !");
        threadStart();
    }



    protected void threadStart(){

        thread = new Thread(this, "StoreBase Thread");
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public void stop() throws LifecycleException {

    }
}
