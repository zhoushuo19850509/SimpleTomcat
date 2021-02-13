package com.nbcb.mytomcat.catalina.session;


import com.nbcb.mytomcat.catalina.util.Constants;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Session;

import java.util.List;

public class StandardManager
        extends ManagerBase
        implements Runnable, Lifecycle {

    /**
     * ��ǰStandardManager�����������첽�߳�
     */
    private Thread thread = null;


    /**
     * ���ڼ��Manager�����µ�session��
     * ����Ƿ��й��ڵ�session����
     */
    @Override
    public void run() {
        while(true){
            try {
                Thread.sleep(Constants.CHECK_INTERVAL * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            processExpires();
            printSessionStatus();
        }

    }

    /**
     * ��ӡManager�����µĸ���session��״̬
     * ������Ծsession����������Ϊ��ʱ��session�����ն����е�session���Լ��´�����session
     */
    public void printSessionStatus(){
        List<Session> sessions = findSessions();

        /**
         * �ȴ�ӡ��Ծsession����
         */
        System.out.println("Active session list: ");
        for (int i = 0; i < sessions.size(); i++) {
            Session session = sessions.get(i);
            System.out.println("[" + i + "] sid: " + session.getId() );
        }
        /**
         * �ٴ�ӡ���ն��е�session
         */
        System.out.println("Recycled session list: ");
        for (int i = 0; i < recycled.size(); i++) {
            Session recycledSession = recycled.get(i);
            System.out.println("[" + i + "] recycled Session " );
        }
    }


    /**
     * ������Щ���ڵ�session����
     */
    public void processExpires(){


        /**
         * ������session���һ�ε�¼��ʱ��
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

            System.out.println("sid: " + sid + " �������һ�η��ʹ�ȥ��: " +duration + " s" );

            int maxInactiveInterval = session.getMaxInactiveInterval();
            if(duration > maxInactiveInterval){
                session.expire();
                System.out.println("session has expired! sid: " + sid);
                System.out.println("session �����ʱ��Ϊ�� " + maxInactiveInterval);
                System.out.println("�������һ�η��ʹ�ȥ�ˣ� " + duration);
            }
        }
    }


    /**
     * ���·�����ʵ��Lifecycle�ӿڵ�
     * ����Manager�������������ڣ�
     * ��Manager������ʱ�򣬻��tomcat����Ŀ¼�¶�ȡһ���ļ���
     * SESSIONS.ser
     * ����ļ������˳־û���tomcat����Ŀ¼�µ�session��Ϣ
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
     * StandardManager������ʱ����Ҫ��2�����飺
     * 1.���������ļ�ϵͳ�Ƿ�����Ҫ������session������������ϴιرպ󣬳־û��������ļ�ϵͳ��session��Ϣ
     * 2.�����첽�̣߳����ڽ����ڵ�session�����
     * @throws LifecycleException
     */
    @Override
    public void start() throws LifecycleException {
        System.out.println("StrandardManager started! ");

        load();

        threadStart();

    }

    @Override
    public void stop() throws LifecycleException {

    }

    public void load(){


    }

    private void threadStart(){

        thread = new Thread(this, "StandardManagerThread");
        thread.setDaemon(true);
        thread.start();

    }


}
