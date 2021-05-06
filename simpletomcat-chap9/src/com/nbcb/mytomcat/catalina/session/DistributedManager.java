package com.nbcb.mytomcat.catalina.session;

import org.apache.catalina.Cluster;
import org.apache.catalina.Container;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Session;
import org.apache.catalina.cluster.ClusterReceiver;
import org.apache.catalina.cluster.ClusterSender;
import org.apache.catalina.cluster.ReplicationWrapper;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * ���Managerר����������ֲ�ʽsession
 * 1.����sessionͬ������Ⱥ�еĸ����ڵ㣻
 * 2.��������Ⱥ�еĹ���session
 */
public class DistributedManager extends PersistentManagerBase {


    /**
     * ��session��Ϣ���͸���Ⱥ�и����ڵ�
     */
    private ClusterSender clusterSender;

    /**
     * ���ռ�Ⱥ�и����ڵ㷢������Ϣ����
     *
     */
    private ClusterReceiver clusterReceiver;


    protected String name = "DistributedManager";




    /**
     * ����session����֮�󣬽�session��Ϣͬ��������tomcat��Ⱥ�����ڵ���
     *
     * @return
     */
    public Session createSession(){
        /**
         * �ȵ��ø���(ManagerBase)������ԭ���ķ�ʽ������һ��session����
         */
        Session session = super.createSession();

        /**
         * Ȼ���´�����session����ת��Ϊbyte[]��ͬ������Ⱥ�ĸ����ڵ�
         */
        ObjectOutputStream oos = null;
        ByteArrayOutputStream bos = null;

        try {
            bos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(new BufferedOutputStream(bos));
            ((StandardSession)session).writeObject(oos);
            byte[] obs = bos.toByteArray();
            this.clusterSender.send(obs);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(oos != null){
                try {
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(bos != null){
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
        return null;
    }

    /**
     * ���ڴ������������ڵ����Ϣ
     *
     */
    public void processClusterReceiver(){

        /**
         * ��ͨ��clusterReceiver���Ӽ�Ⱥ��ȡ��Ϣ
         */
        Object[] objects = this.clusterReceiver.getObjects();

        ReplicationWrapper wrapper = null;
        Session session = null;
        byte[] buf = new byte[5000];

        /**
         * ������ȡ������Ϣ�б�
         */
        for (int i = 0; i < objects.length; i++) {


            /**
             * ��Ҫע����ǣ���ȡ������Ϣ����Ҳ�Ƿ�װ��ReplicationWrapper�����
             * ��֮ǰStandardCluster.start()�з��ͽڵ���Ϣ��һ����������յ���Wrapper�еĶ�����byte[]
             */
            wrapper = (ReplicationWrapper) objects[i];
            buf = wrapper.getDataStream();



            /**
             * ����ȡ����byte[]ת��Ϊ����
             */

            /**
             * ���ø����createSession()����һ��sessionʵ��
             */
            session = super.createSession();

            /**
             * Ȼ��ѴӼ�Ⱥ����ȡ��session����ֵ������´�����session
             * ����������˼�Ⱥ��sessionͬ��
             */

            
        }




    }

    /**
     * �첽�߳�
     */
    public void run(){
        processClusterReceiver();
        processExpires();
        processPersistenceChecks();
    }

    /**
     * ���˵��ø����start()��������Ҫ��ʼ��clusterSender/clusterReceiver
     * @throws LifecycleException
     */
    public void start() throws LifecycleException {
        Container container = getContainer();

        /**
         * ��ʼ��clusterSender/clusterReceiver
         */
        Cluster cluster = null;
        cluster = container.getCluster();
        this.clusterSender = cluster.getClusterSender(getName());
        this.clusterReceiver = cluster.getClusterReceiver(getName());


        super.start();

    }

    /**
     * getter()/setter()
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
