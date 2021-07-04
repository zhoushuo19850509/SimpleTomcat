package com.nbcb.mytomcat.catalina.session;


import com.nbcb.mytomcat.catalina.util.Constants;
import org.apache.catalina.*;

import java.io.IOException;
import java.util.List;


/**
 * PersistentManagerBase�ǳ־û�Manager�Ļ���
 * ��StandardManagerһ����Ҳ�������첽�̶߳��ڼ����ڵ�session����
 * PersistentManagerBase�����Ĺ����ǻ��session���־û�����
 *
 */
public class PersistentManagerBase extends ManagerBase
    implements Runnable, Lifecycle

{


    /**
     * ĳ��session�������ʱ�䳬����һ��ʱ�䣬
     * PersistentManagerBase������session swap to persistence
     * ��Ȼ��Ҫ�����������
     * ����ʱ�䳬��minIdleSwap��maxIdleSwap
     */
    private int minIdleSwap = -1;
    private int maxIdleSwap = -1;

    /**
     * ���Storeʵ������Ҫ�����ڵ�ǰPersistentManagerBase���ʳ־û��豸
     * ����session���־û��豸���ӳ־û��豸��ȡsession����
     */
    private Store store;


    /**
     * ��ǰPersistentManagerBase������߳�
     */
    private Thread thread = null;


    /**
     * @����ͳ��
     * �ڱ����첽�̴߳�����
     * �ж���session�ǿ���ʱ���������ֱ�ӻ�����
     */
    private int maxIdleExpireCount = 0;

    /**
     * @����ͳ��
     * �ڱ����첽�̴߳�����
     * �ж���session����Ϊ����ʱ����������ŵ��־û���
     */
    private int maxIdleSwapCount = 0;

    /**
     * @����ͳ��
     * �ڱ����첽�̴߳�����
     * �ж���session����Ϊ�ڴ��п���session���࣬���ŵ��־û���
     */
    private int maxActiveSwapCount = 0;

    /**
     * @����ͳ��
     * �ڱ����첽�̴߳�����
     * �ж���session����Ϊ����ʱ����������ݵ��־û���
     */
    private int maxIdleBackups = 0;

    /**
     * 1.���ڼ��ʧЧ��session
     * 2.���ڽ�session�־û�
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
     * ��ӡ�첽�̴߳���֮ǰ����session��״̬
     */
    public void printSessionStatusBefore(){

        /**
         * ���첽�̴߳���ǰ���ڴ��е�session�ж���
         */
        System.out.println("PersistentManagerBase before run()");
        System.out.println("current session count(in memory) : " + this.findSessions().size());
        System.out.println("current session count(recycled) : " + super.getRecycled().size());

        /**
         * �����첽�̴߳���ǰ���Ȱ���һ���첽�̵߳���������һ��
         */
        this.maxIdleExpireCount = 0;
        this.maxIdleSwapCount = 0;
        this.maxActiveSwapCount = 0;
        this.maxIdleBackups = 0;

    }

    /**
     * ��ӡ��ǰManager����ĸ���session��״̬(�����첽�̴߳���֮��)
     * �����������ڴ��еġ����������ڳ־û����еġ�
     * ���ݵ��־û����(�ڴ��кͳ־û����ж���)�����ڻ��ն��е�
     */
    public void printSessionStatusAfter(){

        /**
         * ���첽�̴߳���󣬱������ڴ��е�session��ʣ����
         */
        System.out.println("PersistentManagerBase after run()");
        System.out.println("current session count(in memory) : " + super.findSessions().size());
        System.out.println("current session count(recycled) : " + super.getRecycled().size());

        /**
         * ����ڴ�����session���ģ��ʹ�ӡһ��sid
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
         * �����첽�̣߳��ж���session����Ϊidleʱ�������ֱ�ӱ�����
         * idleʱ��ο��� Constants.MAX_INACTIVE_INTERVAL
         * Ŀǰ��60s
         * ������˵�����ﲻ��������Ӧ��idleʱ�������Managerֱ�ӻ�����
         * ��Ϊidleʱ����̵�һ�㶼���ŵ��־û�����
         */


        /**
         * �����첽�̣߳��ж����߳�����ΪIDLEʱ���������swap out to persistence
         * idleʱ��ο���
         * Constants.MIN_IDLE_SWAP = 10
         * MAX_IDLE_SWAP = 30
         * Ŀǰ��30s
         */
        System.out.println("PersistentManagerBase ����Щ����ʱ�������session�ŵ��־ò㡣" +
                "���������� " + this.maxIdleSwapCount);

        /**
         * �����첽�̣߳��ж���session����Ϊ�ڴ���session���࣬��swap out to persistence
         * ����߳����ο��� Constants.MAX_ACTIVE_SESSIONS
         *  Ŀǰ��50��
         */
        System.out.println("PersistentManagerBase ����Щ�ڴ��й����session�ŵ��־ò㡣" +
                "���������� " + this.maxActiveSwapCount);

        /**
         * �����첽�̣߳��ж����߳�����ΪIDLEʱ���������backup to persistence
         * idleʱ��ο���
         * Constants.MAX_IDLE_BACKUP
         * Ŀǰ��20s
         */

    }

    /**
     * �־û���������3���������ݣ�
     * 1.�����ڿ��е�session�ŵ��־û��㣻
     * 2.������Ļ�Ծsession�ŵ��־û��㣻
     * 3.������Ŀ���session���ݵ��־û���
     */
    public void processPersistenceChecks(){
        processMaxIdleSwaps();
        processMaxActiveSwaps();
        processMaxIdleBackups();
    }


    /**
     * ������Щ(�ڴ���)���ڵ�session����
     * �����߼���StandardManager��࣬���ǰ���Щ��ʱ��session����Ϊexpire������
     * ��Ϊ���Manager��Ȼ��PersistentManger�����ǻ��ǻ���һ����session�����ڴ���
     * �ⲿ��session��Ҫ��ǰPersistentMangerBase���д������ڰѹ���session�����
     */
    public void processExpires(){

        /**
         * ���(�ڴ���)����session���һ�ε�¼��ʱ��
         * ������һ�ε�¼��ʱ��͵�ǰʱ����ȣ�������һ����ʱ�����ͽ����session����Ϊ��ʱ
         */
        List<Session> sessionList = findSessions();
        long now = System.currentTimeMillis();
        for(Session session : sessionList){
            String sid = session.getId();

            /**
             * ���һ�η���ʱ��
             */
            long lastAccessTime = session.getLastAccessedTime();

            /**
             * ���ھ������һ�η����Ѿ���ȥ�˶���ʱ��(ms)
             */
            long duration = (int) ((now - lastAccessTime) / 1000L);

//            System.out.println("session in memory(sid: " + sid + ") " +
//                    " �������һ�η��ʹ�ȥ��: " +duration + " s" );
            int maxInactiveInterval = session.getMaxInactiveInterval();
            if(duration > maxInactiveInterval){
                session.expire();
                System.out.println("session in memory has expired! sid: " + sid);
                maxIdleExpireCount++;
            }
        }
    }

    /**
     * ��������������
     * 1.processMaxIdleSwaps��
     * 2.processMaxActiveSwaps��
     * 3.processMaxIdleBackups��
     *
     * ��PersistentManagerBase��session����ʵ�ֳ־û�����
     * ɶ��˼�أ������ڴ��е�session��������˺ܾã�
     * ���߻�Ծ��session����̫��ﵽ��MaxActive������������
     * ��ôPersistentManagerBase�����Щsession Swap out���־ò�(�ļ�ϵͳ�������ݿ�)��
     * �Ա�֤�ڴ��е�session������࣬Ӱ��tomcat JVM�����ܡ�
     *
     * ����Ҫ�õ�session��ʱ����ͨ��Swap in���ӳ־ò���ص��ڴ�
     */

    /**
     * ����1 processMaxIdleSwaps()
     * ʵ���߼��������ģ�
     * ������ǰManager�����session�б�һ������ĳ���̵߳Ŀ���ʱ�䳬������ֵ
     * ��swap out to persistence
     */
    public void processMaxIdleSwaps(){

        /**
         * ��ǰManager������ڴ��е�session�б�
         */
        List<Session> sessionList = findSessions();

        /**
         * ��ǰʱ��
         */
        long now = System.currentTimeMillis();

        /**
         * session������صĲ���
         */
        minIdleSwap = Constants.MIN_IDLE_SWAP;
        maxIdleSwap = Constants.MAX_IDLE_SWAP;

        if(maxIdleSwap < 0){
            return ;
        }

        /**
         * ����session�б�
         */
        for(Session session : sessionList){

            if(!session.isValid()){
                continue;
            }

            String sid = session.getId();

            /**
             * ���һ�η���ʱ��
             */
            long lastAccessTime = session.getLastAccessedTime();

            /**
             * ���ھ������һ�η����Ѿ���ȥ�˶���ʱ��(ms)
             */
            long duration = (int) ((now - lastAccessTime) / 1000L);

//            System.out.println("sid: " + sid + " �������һ�η��ʹ�ȥ��: " +duration + " s" );

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
     * Swap out����3����
     * 1.���浽�־û�
     * 2.�����session�ӵ�ǰmanager session������ɾ����
     * 3.�������session
     * @param session
     */
    public void swapOut(Session session){

        /**
         * ���ڴ��е�session�ŵ��־û���
         */
        writeSession(session);

        /**
         * ��Manger��������session��Manager��ɾ��
         */
        super.remove(session);

        /**
         * ��󣬽����session����
         */
        session.recycle();

    }

    /**
     * ���ط���
     * ������Ҫ��дһ��findSession��������Ϊ����PersistentManager��˵��
     * session�������ڴ棬Ҳ�п����ڳ־û���
     *
     * @param id The session id for the session to be returned
     *
     * @return
     * @throws IOException
     */
    @Override
    public Session findSession(String id) throws IOException {

        /**
         * �ȳ��Դ��ڴ�(Manager session����)ȡ
         */
        Session session = super.findSession(id);
        if(null != session){
            return session;
        }

        /**
         * �ٳ��Դӳ־û���ȡ
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
         * û���ӳ־û�����ص���Ҫ��session��˵���־û���û�ж�Ӧ��session
         */
        if(null == session ){
            return null;
        }

        /**
         * �жϴӳ־ò�load������session�Ƿ�ʧЧ��
         * ���ʧЧ�Ļ�����load������sessionҲûɶ��
         * ֱ�Ӵӳ־ò�ɾ��������
         */
        if(!session.isValid() || isSessionStale(session)){
            session.expire();
            store.remove(id);
        }

        session.setManager(this);

        /**
         * �Ѵӳ־û����ص�session����Manager��session����
         */
        add(session);


        return null;
    }


    /**
     * �жϵ�ǰsession�Ƿ��Ѿ���ʱ
     * @param session
     * @return ���session�Ѿ���ʱ���ͷ���true
     */
    protected boolean isSessionStale(Session session){
        /**
         * ���һ�η���ʱ��
         */
        long lastAccessTime = session.getLastAccessedTime();

        long now = System.currentTimeMillis();

        /**
         * ���ھ������һ�η����Ѿ���ȥ�˶���ʱ��(ms)
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
     * �����ǰPersistentManager�����session�����࣬��swap out to persistent
     *
     */
    public void processMaxActiveSwaps(){
//        System.out.println("start process processMaxActiveSwaps");

        /**
         * ��ǰ�ڴ��е�session�б�
         */
        List<Session> sessions = super.findSessions();


        /**
         * �ҳ���Щ�����session
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
         * ������Щ�����session
         */
        for (int i = 0; i < sessions.size() && toSwap > 0; i++) {
             Session session = sessions.get(i);


            /**
             * ���һ�η���ʱ��
             */
            long lastAccessTime = session.getLastAccessedTime();

            /**
             * ���ھ������һ�η����Ѿ���ȥ�˶���ʱ��(ms)
             */
            long duration = (int) ((now - lastAccessTime) / 1000L);

            /**
             * ���session�Ŀ���ʱ��ﵽһ��ʱ�䣬��swap out to persistent
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
     * ������ʱ�������session���ݵ��־û���
     * ���������processMaxIdleSwaps��������
     * �������ֻ�ǰ�session"����"���־û��㣬������ɾ���ڴ��е�session��Ϣ
     * ��processMaxIdleSwaps��ֱ�Ӱ�session swap out to persistence
     * ͬʱ���ڴ��е�session��Ϣɾ��
     */
    public void processMaxIdleBackups(){

        /**
         * ��ǰManager������ڴ��е�session�б�
         */
        List<Session> sessionList = findSessions();

        /**
         * ��ǰʱ��
         */
        long now = System.currentTimeMillis();



        if(Constants.MAX_IDLE_BACKUP < 0){
            return ;
        }

        /**
         * ����session�б�
         */
        for(Session session : sessionList){

            if(!session.isValid()){
                continue;
            }

            String sid = session.getId();

            /**
             * ���һ�η���ʱ��
             */
            long lastAccessTime = session.getLastAccessedTime();

            /**
             * ���ھ������һ�η����Ѿ���ȥ�˶���ʱ��(ms)
             */
            long duration = (int) ((now - lastAccessTime) / 1000L);

//            System.out.println("sid: " + sid + " �������һ�η��ʹ�ȥ��: " +duration + " s" );

            /**
             * �ڱ���֮ǰ�����ж�һ�£����session֮ǰ�Ƿ��б��ݹ�
             * ���֮ǰ�Ѿ����ݹ������ұ���֮��û���ٱ����ʹ������������ظ�������
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
                 * �������֮�󣬽���ʱ��ʱ��㱣�浽session��notes������
                 * ��ֹ�����ظ�����
                 */
                session.setNote(Constants.PERSISTED_LAST_ACCESSED_TIME,Long.valueOf(lastAccessTime));

                maxIdleBackups++;
            }
        }
    }

    /**
     * ����Store�ӿڣ���sessionд��־û���
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
     * �ж�ĳ��session id�Ƿ����ڴ���
     * @param id
     * @return
     */
    public boolean isLoaded(String id){
        try {
            /**
             * ͨ��ManagerBase������ĳ��Session
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
         * �����ǰPersistentManagerBaseû������store����
         * �����PersistentManagerBaseҲ��ûɶ����
         */
        if(null == store){
            System.out.println("PersistentManagerBase has no Store implement!");
        }

        /**
         * ����Store(��Ҫ������StoreBase)
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
