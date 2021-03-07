package com.nbcb.mytomcat.catalina.session;

import com.nbcb.mytomcat.catalina.util.Constants;
import org.apache.catalina.Container;
import org.apache.catalina.DefaultContext;
import org.apache.catalina.Manager;
import org.apache.catalina.Session;

import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.*;


/**
 * ManagerBase实现了Manager接口，是所有Manger实现类的基类
 * ManagerBase实现了各种Manager都会用到的公共方法，主要是和session相关的，包括：
 * add()
 * remove()
 * findSession()
 * findSessions()
 *
 */
public class ManagerBase implements Manager {


    /**
     * Manager管理的session列表
     * 以Map形式组织
     * <key,value> 为<session id, session对象>
     */
    private Map<String ,Session> sessions = new HashMap<String,Session>();

    /**
     * Manager管理的被回收的Session
     *
     */
    protected List<Session> recycled = new ArrayList<Session>();

    @Override
    public Container getContainer() {
        return null;
    }

    @Override
    public void setContainer(Container container) {

    }

    @Override
    public DefaultContext getDefaultContext() {
        return null;
    }

    @Override
    public void setDefaultContext(DefaultContext defaultContext) {

    }

    @Override
    public boolean getDistributable() {
        return false;
    }

    @Override
    public void setDistributable(boolean distributable) {

    }

    @Override
    public String getInfo() {
        return null;
    }

    @Override
    public int getMaxInactiveInterval() {
        return 0;
    }

    @Override
    public void setMaxInactiveInterval(int interval) {


    }

    /**
     * 回收Manager管理的其中一个session
     * 实现方式就是把这个session放到recycle队列中
     * 后续createSession的时候，可以直接复用recycle队列中可回收的session对象
     * 这样就不用创建新的session对象了，可以提升session创建的效率
     * @param session
     */
    public void recycle(Session session){
        synchronized (recycled){
            recycled.add(session);
        }

    }

    /**
     * Manager添加要管理的session
     */
    @Override
    public void add(Session session) {
         synchronized (sessions){
             sessions.put(session.getId(),session);
         }
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {

    }


    /**
     * 创建一个空的session对象
     * 具体参考较新版本tomcat的代码，比如9.0.30 ManagerBase.createEmptySession()方法
     * 这和createSession()方法有何不同呢？
     * createSession()方法创建session实例后，会做很多事情：
     * 1.设置Session各个字段的初始值；
     * 2.将新创建的session对象纳入manager管理；
     * 3.尝试从Manager回收队列中获取闲置的session对象
     *
     * 那么，createEmptySession()一般用在哪些场景呢？
     * 一个典型的场景就StoreBase.processExpire()
     * 从持久化层获取session信息后，就会调用这个createEmptySession()方法
     * 为啥不调用createSession()呢？因为会产生pending null session的问题，具体参考：
     * "问题6 StoreBase.processExpire()的Pending null session问题"
     * @return
     */
    public Session createEmptySession(){

        Session session = null;
        synchronized (recycled){
            int size = recycled.size();
            if(size > 0 ){
                session = recycled.get(size - 1);
                recycled.remove(size - 1);
            }
        }

        if(null != session){
            System.out.println("recycled from old session!");
            session.setManager(this);
        }else{
            // 创建session实例
            System.out.println("create new session from ground up");
            session = new StandardSession(this);
        }

        return session;
    }



    /**
     * 创建Session对象实例
     * @return
     */
    @Override
    public Session createSession() {
        System.out.println("start create session!");

        /**
         * 先判断一下回收队列中是否有可以复用的session对象
         */
        Session session = null;
        synchronized (recycled){
            int size = recycled.size();
            if(size > 0 ){
                session = recycled.get(size - 1);
                recycled.remove(size - 1);
            }
        }

        if(null != session){
            System.out.println("recycled from old session!");
            session.setManager(this);
        }else{
            // 创建session实例
            System.out.println("create new session from ground up");
            session = new StandardSession(this);
        }

        // 设置session id
        // 创建32位随机sessionId，比如：21a01f6f461e4eacace02e17816ae242
        String sessionId = UUID.randomUUID().toString().replace("-","");

        // 不仅要设置sid，还要把这个session对象纳入Manager管理
        session.setId(sessionId);

        // 设置session的各个参数
        session.setMaxInactiveInterval(Constants.MAX_INACTIVE_INTERVAL);  // session最长活跃时间
        session.setNew(true);    // session是刚创建的
        session.setValid(true);  // session刚创建的时候当然是有效的
        session.setCreationTime(System.currentTimeMillis()); // session创建时间


        return session;
    }

    /**
     * 根据session id找出某个session
     * @param id The session id for the session to be returned
     *
     * @return
     * @throws IOException
     */
    @Override
    public Session findSession(String id) throws IOException {
        return sessions.get(id);
    }

    /**
     * 这个方法返回当前Manager管理的所有session对象
     * @return
     */
    @Override
    public List<Session> findSessions() {
        List<Session> list = new ArrayList<Session>();
        for(Session session: sessions.values()){
            list.add(session);
        }
        return list;
    }

    @Override
    public void load() throws ClassNotFoundException, IOException {

    }

    /**
     * 从sessions列表中删除某个session
     * @param session Session to be removed
     */
    @Override
    public void remove(Session session) {
        synchronized (sessions){
            sessions.remove(session.getId());
        }
    }

    /**
     * 将session对象从manager中删除
     * 这个remove()方法和上面的remove()方法有啥不同呢？
     * 上面的remove()方法是将session id作为key，从sessions map<key,value>中删除元素的
     * 而removePendingSession()方法，是将session对象作为value，从sessions map<key,value>中删除元素的
     * 主要是为了处理null pending session的情况：
     * 要删除的session对象的sesionId，和manager管理的sessions map<key,value>中的key不一样
     * 如果直接根据session对象的sesionId删除的话，就会删除不成功。
     * @param session
     */
    public void removePendingSession(Session session){
        Collection<Session> list = sessions.values();
        list.remove(session);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {

    }

    @Override
    public void unload() throws IOException {

    }

    /**
     * 获取当前Manager管理的可回收的session
     * @return
     */
    public List<Session> getRecycled() {
        return recycled;
    }

    public void setRecycled(List<Session> recycled) {
        this.recycled = recycled;
    }
}
