package com.nbcb.mytomcat.catalina.session;

import com.nbcb.mytomcat.catalina.util.Constants;
import org.apache.catalina.*;

import java.beans.PropertyChangeListener;
import java.io.IOException;

public abstract class StoreBase implements Lifecycle ,Store ,Runnable{

    /**
     * ��ǰStoreBase������첽�߳�
     */
    protected Thread thread = null;

    /**
     * Store���Ӧ��Manager
     */
    protected Manager manager = null;

    /**
     * @����ͳ��
     * �ڱ���StoreBase�첽�̴߳�����
     * �ж���session�ǿ���ʱ���������ֱ�ӻ�����
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
     * StoreBase������һ���������̣߳������ڵ�session
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
     * ��ӡһ����Щ��StoreBase�����session��Ϣ(�첽�߳�ִ�к�)
     */
    public void printStoreSessionStatusBefore(){
        /**
         * ��ӡStoreBase�����첽�̴߳���ǰ
         * �ڳ־û��㣬�ж���session
         */
        try {
            System.out.println("StoreBase before run()");
            System.out.println("[StoreBase]current session count(in file system) : " + keys().length);
            System.out.println("[StoreBase]current session count(in memory) : " + this.manager.findSessions().size());
            System.out.println("[StoreBase]current session recycled : " + ((ManagerBase)this.manager).getRecycled().size());

            /**
             * ͬʱ�����첽�̴߳���ǰ������һ���첽�̵߳�ͳ���������һ��
             */
            maxIdleExpireCount = 0;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * ��ӡһ����Щ��StoreBase�����session��Ϣ(�첽�߳�ִ��ǰ)
     */
    private void printStoreSessionStatusAfter(){


        /**
         * ��ӡStoreBase�����첽�̴߳����У������˶��ٳ־û�������Ҫexpire��session
         * ��������ο�Constants.MAX_INACTIVE_INTERVAL
         * Ŀǰ��60s
         */

        /**
         * ��ӡStoreBase�����첽�̴߳���֮��
         * �ڳ־û��㣬��ʣ����session
         */
        try {
            System.out.println("StoreBase after run()");
            System.out.println("current session count(in file system) : " + keys().length);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * ������ڵ�session
     * StoreBase�еĴ���ĳ�ʱsession����Ҫ����Щ�����ڳ־û����session
     *
     */
    public void processExpire(){
        try {
            /**
             * ��ͨ�������Storeʵ���࣬��ȡ�־û����б����session��session id�б�
             */
            String[] keys = keys();
            System.out.println("�־û��������ж���session�ļ��� " + keys.length);

            long now = System.currentTimeMillis();
            for(String key: keys){

                /**
                 * �ֽ��־û����е�session�ļ����ؽ��ڴ���
                 */
                Session session = load(key);
                /**
                 * һ������session����û�з����ˣ��ͽ����session expire��
                 */
                /**
                 * ���һ�η���ʱ��
                 */
                long lastAccessTime = session.getLastAccessedTime();

                /**
                 * ���ھ������һ�η����Ѿ���ȥ�˶���ʱ��(ms)
                 */
                long duration = (int) ((now - lastAccessTime) / 1000L);

                System.out.println("session in persistent(sid: " + key + ") " +
                        " �������һ�η��ʹ�ȥ��: " +duration + " s" );
                int maxInactiveInterval = session.getMaxInactiveInterval();
                if(duration > maxInactiveInterval){

                    /**
                     * �����ǰ�Ѿ���ʱ��session�Ѿ����ص��ڴ�����.
                     * ��ֻҪ�����Ǵӳ־ò�load������session����ֻҪ���Ϊ���վ����ˣ�
                     * PersistentManagerBase������ڴ��е�session����,Manager�Լ��ͻᴦ��
                     * StoreBase�ͱ����
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
                         * �����ǰsessionû�м��ص��ڴ棬���ڳ־û��㣬StoreBase��Ҫ���expire����
                         * ��Ϊexpire������������Զ�һ�㣬��ǰsession������ڳ־û��㣬
                         * �Ͳ������鷳Load��Manger������
                         * StoreBase��߾�ֱ�Ӵ�����ˡ�
                         */
                        System.out.println("persistent session(sid:" + session.getId() + ") " +
                                "keep idle too long! " +
                                "Idle time : " + duration + " seconds. " +
                                "So expire from persistence! ");
                        session.expire();
                    }

                    /**
                     * ������Store�����ʵ���࣬��������ڵ�session�ӳ־û���ɾ��
                     * ����FileStore�������session���ļ�ϵͳ��ɾ��
                     * ����JDBCStore�������session�����ݿ�ɾ��
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
     * ���·���ʵ����Lifecycle�ӿ�
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
     * StoreBase������ʱ����Ҫ����һ���첽�̣߳����ڼ��־û����й��ڵ�session
     * ��ôStoreBase���������������أ�һ�����ɶ�Ӧ�ĳ־û�Manager������ʱ����������
     * �ο�PersistentManagerBase.start()
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
