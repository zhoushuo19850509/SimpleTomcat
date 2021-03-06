package com.nbcb.mytomcat.catalina.session;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.apache.catalina.Manager;
import org.apache.catalina.Session;
import org.apache.catalina.SessionListener;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.Principal;
import java.util.*;


/**
 * Session接口的实现，最关键的方法为：
 * expire() 包含了session超时的逻辑
 */
public class StandardSession implements Session, HttpSession
{

    /**
     * 管理当前这个Session对象的Manager对象实例
     */
    private Manager manager;

    /**
     * sessionId 32位随机编号
     */
    private String sessionId;

    /**
     * 当前Session对象所属的相关属性
     * 这些属性保存在服务端session，不会带到客户端浏览器cookie
     */
    private Map<String, Object> attributes;


    /**
     * 标识这个session对象是不是刚创建的
     */
    private boolean isNew = false;

    /**
     * 标识这个Session对象是否有效
     */
    private boolean isValid = false;

    /**
     * 最长空闲时间 单位是秒
     */
    private int maxInactiveInterval = -1;

    /**
     * Session创建时间
     */
    private long createTime = 0L;

    /**
     * 上次(最近一次)访问Session的时间
     */
    private long lastAccessTime;

    /**
     * 这次访问Session的时间
     */
    private long thisAccessTime;

    private boolean expire = false;

    /**
     * constructor
     * 在constructor中：
     * 1.必须指定这个session对象所属的manager
     * 2.要初始化sessionId
     * @param manager
     */
    public StandardSession(Manager manager) {
        this.manager = manager;

        // 初始化attributes map
        this.attributes = new HashMap<String, Object>();
    }

    @Override
    public String getAuthType() {
        return null;
    }

    @Override
    public void setAuthType(String authType) {

    }

    @Override
    public long getCreationTime() {
        return this.createTime;
    }

    /**
     * 除了设置session创建时间
     * 还要初始化几个时间参数
     * @param time The new creation time
     */
    @Override
    public void setCreationTime(long time) {
        this.createTime = time;
        this.lastAccessTime = time;
        this.thisAccessTime = time;
    }

    /**
     * 返回sessionId
     * @return
     */
    @Override
    public String getId() {
        return this.sessionId;
    }

    /**
     * 设置session id
     * 同时，还要做几个事情：
     * 1.更新Manager管理的session id
     *   这个背景是这样的，我们调用setId()方法设置某个session的Id，一般是设置新创建session的id
     *   但是也有一种情况，就是更新某个原有session的Id。
     *   这时就需要特别小心，因为manager中原来维持的session信息，是以key/value形式保存的：
     *   <key,value>:<sessionId,Session对象>
     *   如果我们仅仅只是设置了当前session对象的sessionId属性
     *   就会出现异常：我们虽然把新设置的sessionId的session对象纳入了manager管理，但是manager还保留了老sessionId的key/value！
     *   <old sessionId,Session对象>
     *   <new sessionId,Session对象>
     *   虽然两个key/value对一个的value是同一个session对象，但是manager中平白多出了一个session对象！
     *   因此，在将新sessionId对应的session纳入manager管理之前，要先删除老的session ID
     * 2.将新创建的session纳入Manager管理
     * 3.注册session的监听事件(这个后续再补充)
     * @param id The new session identifier
     */
    @Override
    public void setId(String id) {


        if(null != id && null != this.manager){
            manager.remove(this);
        }

        // 设置session id
        this.sessionId = id;

        if(null != manager){
            manager.add(this);
        }

    }

    @Override
    public String getInfo() {
        return null;
    }

    @Override
    public long getLastAccessedTime() {
        return this.lastAccessTime;
    }


    /**
     *
     * @return
     */
    @Override
    public ServletContext getServletContext() {
        return null;
    }

    @Override
    public Manager getManager() {
        return null;
    }

    @Override
    public void setManager(Manager manager) {
        this.manager = manager;
    }

    @Override
    public int getMaxInactiveInterval() {
        return this.maxInactiveInterval;
    }


    @Override
    public void setMaxInactiveInterval(int interval) {
        this.maxInactiveInterval = interval;
    }

    @Override
    public void setNew(boolean isNew) {
        this.isNew = isNew;
    }

    @Override
    public Principal getPrincipal() {
        return null;
    }

    @Override
    public void setPrincipal(Principal principal) {

    }


    /**
     * 这里可以优化一下，通过StandardSessionFacade
     * 创建session对象，这样更加安全
     * @return
     */
    @Override
    public HttpSession getSession() {
        return (HttpSession)this;
    }

    @Override
    public void setValid(boolean isValid) {
        this.isValid = isValid;
    }

    /**
     * 判断当前Session实例是否有效
     * 后续要扩展一下这个方法的实现
     * 由Manager对Session的有效性进行管理
     * @return
     */
    @Override
    public boolean isValid() {
        return this.isValid;
    }

    /**
     * 如果客户端之前已经创建了Session，那么客户端后续访问的时候，就会调用一下Session.access()方法
     * 表示客户端访问了之前创建的session
     */
    @Override
    public void access() {
        this.isNew = false;
        this.lastAccessTime = this.thisAccessTime;
        this.thisAccessTime = System.currentTimeMillis();
    }

    @Override
    public void addSessionListener(SessionListener listener) {

    }

    /**
     * session过期，这里要好好实现一下session过期的机制
     */
    @Override
    public void expire() {
        /**
         * expire过期设置为true
         */
        this.expire = true;

        /**
         * valid设置为false
         */
        setValid(false);

        /**
         * 将当前session从Manager中remove掉
         */
        if(null != manager){
            manager.remove(this);
        }

        /**
         * 然后把当前session实例回收
         */
        recycle();
    }


    /**
     * Session exipre之后，会把Session回收
     * 回收的逻辑为：清空Session各个字段
     * 并最终通过Manager回收当前Session对象
     * 为啥是回收，而不是直接将这个session对象从manager中直接remove掉并销毁呢？
     * 主要也是为了提升Manager创建session的效率
     * 我们参考StandardManager.recycle()/createSession()方法，
     * 可以看到Manager在创建session的时候，会有限从recycle队列中获取可回收的session对象实例
     */
    @Override
    public void recycle() {
        this.attributes.clear();
        this.sessionId = null;
        this.createTime = 0L;
        this.lastAccessTime = 0L;
        this.expire = false;
        this.isNew = false;
        this.maxInactiveInterval = -1;
        Manager savedManager = manager;
        this.manager = null;

        if(null != savedManager && (savedManager instanceof ManagerBase)){
            ((ManagerBase)savedManager).recycle(this);
        }
    }


    @Override
    public Object getNote(String name) {
        return null;
    }

    @Override
    public Iterator getNoteNames() {
        return null;
    }

    @Override
    public void removeNote(String name) {

    }

    @Override
    public void removeSessionListener(SessionListener listener) {

    }

    @Override
    public void setNote(String name, Object value) {

    }


    /**
     * 以下这些方法是实现了HttpSession
     * @return
     */
    @Override
    public HttpSessionContext getSessionContext() {
        return null;
    }

    @Override
    public Object getAttribute(String s) {
        return attributes.get(s);
    }

    @Override
    public Object getValue(String s) {
        return null;
    }

    @Override
    public Enumeration getAttributeNames() {
        return null;
    }

    @Override
    public String[] getValueNames() {
        return new String[0];
    }


    @Override
    public void setAttribute(String s, Object o) {
        this.attributes.put(s,o);
    }

    @Override
    public void putValue(String s, Object o) {

    }

    @Override
    public void removeAttribute(String s) {

    }

    @Override
    public void removeValue(String s) {

    }

    @Override
    public void invalidate() {

    }

    /**
     * 这个后续要实现一下逻辑，判断session是否是新创建的
     * @return
     */
    @Override
    public boolean isNew() {
        return this.isNew;
    }

    /**
     * 将当前session对象写入stream
     * stream是啥意思呢？
     * 1.在FileStore应用场景中，這個stream其實就是FileOutStream，
     *   将session对象写入文件系统
     * 2.在JDBCStore应用场景中，这个stream其实就是ByteArrayStream，
     *   将session对象写入数据库表的binary字段
     * 3.在DistributedManager中，这个stream也是ByteArrayOutputStream
     *   将session对象以byte[]的形式，传递到各个分布式节点中
     * 可以在整个工程中，全局搜索一下writeObject()方法的"Find Usages"
     * 说白了，这就是一个将session对象转化为byte stream的通用方法
     * @param stream
     */
    void writeObject(ObjectOutputStream stream) throws IOException {

        /**
         * 把session各个(简单)字段写入stream
         */
        stream.writeObject(new Long(createTime));
        stream.writeObject(new Long(lastAccessTime));
        stream.writeObject(new Integer(maxInactiveInterval));
        stream.writeObject(new Boolean(isNew));
        stream.writeObject(new Boolean(isValid));
        stream.writeObject(new Long(thisAccessTime));
        stream.writeObject(sessionId);


        /**
         * 把session的各个attributes写入stream
         * 具体写入的逻辑参考apache官方实现
         * 将attribute各个key/value键值对写入文件
         */
        // FIXME 把这端逻辑补充上
    }

    /**
     * 从InputStream中读取session实例各个字段的信息
     * @param stream
     * @throws IOException
     * @throws ClassNotFoundException
     */
    void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        createTime = ((Long)stream.readObject()).longValue();
        lastAccessTime = ((Long)stream.readObject()).longValue();
        maxInactiveInterval = ((Integer) stream.readObject()).intValue();
        isNew = ((Boolean) stream.readObject()).booleanValue();
        isValid = ((Boolean) stream.readObject()).booleanValue();
        thisAccessTime = ((Long) stream.readObject()).longValue();
        sessionId = (String) stream.readObject();
    }


    public long getThisAccessTime() {
        return thisAccessTime;
    }

    public void setThisAccessTime(long thisAccessTime) {
        this.thisAccessTime = thisAccessTime;
    }

    @Override
    public String toString() {
        return "StandardSession{" +
                "sessionId='" + sessionId + '\'' +
                ", isNew=" + isNew +
                ", isValid=" + isValid +
                ", maxInactiveInterval=" + maxInactiveInterval +
                ", createTime=" + createTime +
                ", lastAccessTime=" + lastAccessTime +
                ", thisAccessTime=" + thisAccessTime +
                ", expire=" + expire +
                '}';
    }
}
