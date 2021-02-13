package com.nbcb.mytomcat.catalina.session;


import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleListener;

public class PersistentManagerBase extends ManagerBase
    implements Runnable, Lifecycle

{



    /**
     * 1.���ڼ��ʧЧ��session
     * 2.���ڽ�session�־û�
     */
    @Override
    public void run() {

    }

    /**
     * ������Щ���ڵ�session����
     */
    public void processExpires(){

    }

    /**
     * ����������������PersistentManagerBase��session����ʵ�ֳ־û�����
     * ɶ��˼�أ������ڴ��е�session��������˺ܾã�
     * ���߻�Ծ��session����̫��ﵽ��MaxActive������������
     * ��ôPersistentManagerBase�����Щsession Swap out���־ò�(�ļ�ϵͳ�������ݿ�)��
     * �Ա�֤�ڴ��е�session������࣬Ӱ��tomcat JVM�����ܡ�
     *
     * ����Ҫ�õ�session��ʱ����ͨ��Swap in���ӳ־ò���ص��ڴ�
     */
    public void processMaxIdleSwaps(){

    }
    public void processMaxActiveSwaps(){

    }

    public void processMaxIdleBackups(){

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

    }

    @Override
    public void stop() throws LifecycleException {

    }
}
