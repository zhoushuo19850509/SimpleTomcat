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
 * Session�ӿڵ�ʵ�֣���ؼ��ķ���Ϊ��
 * expire() ������session��ʱ���߼�
 */
public class StandardSession implements Session, HttpSession
{

    /**
     * ����ǰ���Session�����Manager����ʵ��
     */
    private Manager manager;

    /**
     * sessionId 32λ������
     */
    private String sessionId;

    /**
     * ��ǰSession�����������������
     * ��Щ���Ա����ڷ����session����������ͻ��������cookie
     */
    private Map<String, Object> attributes;


    /**
     * ��ʶ���session�����ǲ��Ǹմ�����
     */
    private boolean isNew = false;

    /**
     * ��ʶ���Session�����Ƿ���Ч
     */
    private boolean isValid = false;

    /**
     * �����ʱ�� ��λ����
     */
    private int maxInactiveInterval = -1;

    /**
     * Session����ʱ��
     */
    private long createTime = 0L;

    /**
     * �ϴ�(���һ��)����Session��ʱ��
     */
    private long lastAccessTime;

    /**
     * ��η���Session��ʱ��
     */
    private long thisAccessTime;

    private boolean expire = false;

    /**
     * constructor
     * ��constructor�У�
     * 1.����ָ�����session����������manager
     * 2.Ҫ��ʼ��sessionId
     * @param manager
     */
    public StandardSession(Manager manager) {
        this.manager = manager;

        // ��ʼ��attributes map
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
     * ��������session����ʱ��
     * ��Ҫ��ʼ������ʱ�����
     * @param time The new creation time
     */
    @Override
    public void setCreationTime(long time) {
        this.createTime = time;
        this.lastAccessTime = time;
        this.thisAccessTime = time;
    }

    /**
     * ����sessionId
     * @return
     */
    @Override
    public String getId() {
        return this.sessionId;
    }

    /**
     * ����session id
     * ͬʱ����Ҫ���������飺
     * 1.����Manager�����session id
     *   ��������������ģ����ǵ���setId()��������ĳ��session��Id��һ���������´���session��id
     *   ����Ҳ��һ����������Ǹ���ĳ��ԭ��session��Id��
     *   ��ʱ����Ҫ�ر�С�ģ���Ϊmanager��ԭ��ά�ֵ�session��Ϣ������key/value��ʽ����ģ�
     *   <key,value>:<sessionId,Session����>
     *   ������ǽ���ֻ�������˵�ǰsession�����sessionId����
     *   �ͻ�����쳣��������Ȼ�������õ�sessionId��session����������manager��������manager����������sessionId��key/value��
     *   <old sessionId,Session����>
     *   <new sessionId,Session����>
     *   ��Ȼ����key/value��һ����value��ͬһ��session���󣬵���manager��ƽ�׶����һ��session����
     *   ��ˣ��ڽ���sessionId��Ӧ��session����manager����֮ǰ��Ҫ��ɾ���ϵ�session ID
     * 2.���´�����session����Manager����
     * 3.ע��session�ļ����¼�(��������ٲ���)
     * @param id The new session identifier
     */
    @Override
    public void setId(String id) {


        if(null != id && null != this.manager){
            manager.remove(this);
        }

        // ����session id
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
     * ��������Ż�һ�£�ͨ��StandardSessionFacade
     * ����session�����������Ӱ�ȫ
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
     * �жϵ�ǰSessionʵ���Ƿ���Ч
     * ����Ҫ��չһ�����������ʵ��
     * ��Manager��Session����Ч�Խ��й���
     * @return
     */
    @Override
    public boolean isValid() {
        return this.isValid;
    }

    /**
     * ����ͻ���֮ǰ�Ѿ�������Session����ô�ͻ��˺������ʵ�ʱ�򣬾ͻ����һ��Session.access()����
     * ��ʾ�ͻ��˷�����֮ǰ������session
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
     * session���ڣ�����Ҫ�ú�ʵ��һ��session���ڵĻ���
     */
    @Override
    public void expire() {
        /**
         * expire��������Ϊtrue
         */
        this.expire = true;

        /**
         * valid����Ϊfalse
         */
        setValid(false);

        /**
         * ����ǰsession��Manager��remove��
         */
        if(null != manager){
            manager.remove(this);
        }

        /**
         * Ȼ��ѵ�ǰsessionʵ������
         */
        recycle();
    }


    /**
     * Session exipre֮�󣬻��Session����
     * ���յ��߼�Ϊ�����Session�����ֶ�
     * ������ͨ��Manager���յ�ǰSession����
     * Ϊɶ�ǻ��գ�������ֱ�ӽ����session�����manager��ֱ��remove���������أ�
     * ��ҪҲ��Ϊ������Manager����session��Ч��
     * ���ǲο�StandardManager.recycle()/createSession()������
     * ���Կ���Manager�ڴ���session��ʱ�򣬻����޴�recycle�����л�ȡ�ɻ��յ�session����ʵ��
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
     * ������Щ������ʵ����HttpSession
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
     * �������Ҫʵ��һ���߼����ж�session�Ƿ����´�����
     * @return
     */
    @Override
    public boolean isNew() {
        return this.isNew;
    }

    /**
     * ����ǰsession����д��stream
     * stream��ɶ��˼�أ�
     * 1.��FileStoreӦ�ó����У��@��stream�䌍����FileOutStream��
     *   ��session����д���ļ�ϵͳ
     * 2.��JDBCStoreӦ�ó����У����stream��ʵ����ByteArrayStream��
     *   ��session����д�����ݿ���binary�ֶ�
     * 3.��DistributedManager�У����streamҲ��ByteArrayOutputStream
     *   ��session������byte[]����ʽ�����ݵ������ֲ�ʽ�ڵ���
     * ���������������У�ȫ������һ��writeObject()������"Find Usages"
     * ˵���ˣ������һ����session����ת��Ϊbyte stream��ͨ�÷���
     * @param stream
     */
    void writeObject(ObjectOutputStream stream) throws IOException {

        /**
         * ��session����(��)�ֶ�д��stream
         */
        stream.writeObject(new Long(createTime));
        stream.writeObject(new Long(lastAccessTime));
        stream.writeObject(new Integer(maxInactiveInterval));
        stream.writeObject(new Boolean(isNew));
        stream.writeObject(new Boolean(isValid));
        stream.writeObject(new Long(thisAccessTime));
        stream.writeObject(sessionId);


        /**
         * ��session�ĸ���attributesд��stream
         * ����д����߼��ο�apache�ٷ�ʵ��
         * ��attribute����key/value��ֵ��д���ļ�
         */
        // FIXME ������߼�������
    }

    /**
     * ��InputStream�ж�ȡsessionʵ�������ֶε���Ϣ
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
