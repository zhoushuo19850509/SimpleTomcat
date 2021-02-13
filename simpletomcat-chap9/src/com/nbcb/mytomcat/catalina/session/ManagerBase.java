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
 * ManagerBaseʵ����Manager�ӿڣ�������Mangerʵ����Ļ���
 * ManagerBaseʵ���˸���Manager�����õ��Ĺ�����������Ҫ�Ǻ�session��صģ�������
 * add()
 * remove()
 * findSession()
 * findSessions()
 *
 */
public class ManagerBase implements Manager {


    /**
     * Manager�����session�б�
     * ��Map��ʽ��֯
     * <key,value> Ϊ<session id, session����>
     */
    private Map<String ,Session> sessions = new HashMap<String,Session>();

    /**
     * Manager����ı����յ�Session
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
     * ����Manager���������һ��session
     * ʵ�ַ�ʽ���ǰ����session�ŵ�recycle������
     * ����createSession��ʱ�򣬿���ֱ�Ӹ���recycle�����пɻ��յ�session����
     * �����Ͳ��ô����µ�session�����ˣ���������session������Ч��
     * @param session
     */
    public void recycle(Session session){
        synchronized (recycled){
            recycled.add(session);
        }

    }

    /**
     * Manager���Ҫ�����session
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
     * ����Session����ʵ��
     * @return
     */
    @Override
    public Session createSession() {
        System.out.println("start create session!");

        /**
         * ���ж�һ�»��ն������Ƿ��п��Ը��õ�session����
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
            // ����sessionʵ��
            System.out.println("create new session from ground up");
            session = new StandardSession(this);
        }

        // ����session id
        // ����32λ���sessionId�����磺21a01f6f461e4eacace02e17816ae242
        String sessionId = UUID.randomUUID().toString().replace("-","");

        // ����Ҫ����sid����Ҫ�����session��������Manager����
        session.setId(sessionId);

        // ����session�ĸ�������
        session.setMaxInactiveInterval(Constants.MAX_INACTIVE_INTERVAL);  // session���Ծʱ��
        session.setNew(true);    // session�Ǹմ�����
        session.setValid(true);  // session�մ�����ʱ��Ȼ����Ч��
        session.setCreationTime(System.currentTimeMillis()); // session����ʱ��


        return session;
    }

    /**
     * ����session id�ҳ�ĳ��session
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
     * ����������ص�ǰManager���������session����
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
     * ��sessions�б���ɾ��ĳ��session
     * @param session Session to be removed
     */
    @Override
    public void remove(Session session) {
        synchronized (sessions){
            sessions.remove(session.getId());
        }
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {

    }

    @Override
    public void unload() throws IOException {

    }
}
