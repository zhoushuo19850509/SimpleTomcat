package com.nbcb.mytomcat.catalina.cluster;

import com.nbcb.mytomcat.catalina.util.Constants;
import org.apache.catalina.Container;
import org.apache.catalina.Cluster;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.cluster.ClusterReceiver;
import org.apache.catalina.cluster.ClusterSender;
import org.apache.catalina.cluster.ClusterMemberInfo;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * ��׼Clusterʵ��
 */
public class StandardCluster implements Cluster, Lifecycle, Runnable {


    /**
     * ��ǰ��ȺԼ����ͨ�Ŷ˿�
     * ��ǰ�ڵ����������鲥��ַ������Ϣ�����ǽ�����Ϣ
     * ����Ҫ����һ��Լ���Ķ˿�
     * ����˿�һ����������ļ�
     * ����������ʱ�ŵ�Constants.java����Ϊ��������
     */
    private int multicastPort;

    /**
     * ��ǰ�ڵ�Ҫ�ͼ�Ⱥ�����ڵ�(ͨ��UDP����)ͨ��
     * ��Ҫ����һ��MulticastSocket����
     */
    private MulticastSocket multicastSocket;

    /**
     * �鲥��ַ
     * ����tomcat��Ⱥ�ĸ����ڵ㶼���������ַ�շ���Ϣ
     */
    private InetAddress multicastAddress = null;


    private ClusterSender clusterSender = null;
    private ClusterReceiver clusterReceiver = null;

    /**
     * ��Ⱥ�ڵ���Ϣʵ��
     * �������catalina�ٷ�������ˣ������߼�Ҳ������
     */
    private ClusterMemberInfo clusterMemberInfo = null;


    /**
     * Clusterʵ��������ƣ�������־��ӡ
     */
    private String clusterImpName = "StandardCluster";

    /**
     * ��Ⱥ����
     */
    private String clusterName = null;

    /**
     * �ڵ���Ϣ
     */
    private static final String info = "StandardCluster/1.0";

    /**
     * ��ǰStandardCluster������߳�
     */
    private Thread thread = null;

    private List<ClusterMemberInfo> clusterMembers = new ArrayList<ClusterMemberInfo>();


    public String getName(){
        return clusterImpName;
    }


    @Override
    public String getInfo() {
        return info;
    }

    @Override
    public String getClusterName() {
        return this.clusterName;
    }

    @Override
    public void setCheckInterval(int checkInterval) {

    }

    @Override
    public int getCheckInterval() {
        return 0;
    }

    @Override
    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    @Override
    public void setContainer(Container container) {

    }

    @Override
    public Container getContainer() {
        return null;
    }

    @Override
    public void setDebug(int debug) {

    }

    @Override
    public int getDebug() {
        return 0;
    }

    @Override
    public ClusterMemberInfo[] getRemoteClusterMembers() {
        return new ClusterMemberInfo[0];
    }

    /**
     * �������������getClusterSender()
     * ��ʵ��createClusterSender()���Ӻ���
     * ��Ϊ���������ʵ�Ǵ���һ��MulticastSender����
     * ͨ��param(senderId)����ʶ��ͬ����;
     * ����StandardCluster.run()���첽�߳̾������������ClusterSender�����͵�ǰ�ڵ���Ϣ����Ⱥ
     * DistributedManager��ClusterSender������session��Ϣ����Ⱥ��ʵ��session�ڼ�Ⱥ�ڲ�ͬ����Ŀ��
     * ��ͬ�ĳ�������;��ͨ��senderId����������
     * @param senderId
     * @return
     */
    @Override
    public ClusterSender getClusterSender(String senderId) {
        MulticastSender sender = new MulticastSender(senderId, multicastSocket, multicastAddress, multicastPort);
        return sender;
    }

    @Override
    public ClusterReceiver getClusterReceiver(String senderId) {
        MulticastReceiver receiver = new MulticastReceiver(senderId,
                                                           multicastPort,
                                                           multicastSocket,
                                                           multicastAddress);

        /**
         * ��ȡMulticastReceiver����֮ǰ����Ҫ����MulticastReceiver����
         */
        receiver.start();
        return receiver;
    }

    @Override
    public ClusterMemberInfo getLocalClusterMember() {
        return null;
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

    /**
     * ʵ��LifeCycle�ӿ�,��StandardCluster���г�ʼ��
     * (1)��ʼ��ClusterMemberInfo����(���ؽڵ����)
     * (2)����ClusterSender����(MulticastSender)�����ڷ��ͽڵ���Ϣ����Ⱥ�����ڵ�
     * (3)����ClusterReceiver����(MuticastReceiver)�����ڽ��ռ�Ⱥ�����ڵ�Ľڵ���Ϣ
     * (4)ͨ��ClusterSender,���ոմ�����ClusterMemberInfo����(���ؽڵ����),
     *    ���͸���Ⱥ�����ڵ�;
     * @TODO
     * @throws LifecycleException
     */
    @Override
    public void start() throws LifecycleException {


        try {
            /**
             * ����һ��MultiSocket����
             * ���ں���sender/receiver
             */
            multicastSocket = new MulticastSocket(this.multicastPort);

            /**
             * MulticastSocket�������������һ���鲥��ַ
             */
            multicastSocket.joinGroup(this.multicastAddress);

            /**
             * ����ClusterSender����(MulticastSender)�����ڷ��ͽڵ���Ϣ����Ⱥ�����ڵ�
             */
            this.clusterSender = getClusterSender(getName());

            /**
             * ����ClusterReceiver����(MuticastReceiver)�����ڽ��ռ�Ⱥ�����ڵ�Ľڵ���Ϣ
             */
            this.clusterReceiver = getClusterReceiver(getName());

            /**
             * ��ʼ��ClusterMemberInfo����(���ؽڵ����)
             */
            this.clusterMemberInfo = new ClusterMemberInfo();
            clusterMemberInfo.setClusterInfo(getInfo());
            clusterMemberInfo.setClusterName(getClusterName());
            clusterMemberInfo.setHostName(null);

            /**
             * ͨ��ClusterSender,���ոմ�����ClusterMemberInfo����(���ؽڵ����),
             * ���͸���Ⱥ�����ڵ�;
             */
            clusterSender.send(clusterMemberInfo);


            /**
             * ���������̣߳����ڽ������������ڵ����Ϣ
             */
            threadStart();


        } catch (IOException e) {
            e.printStackTrace();
        }





    }

    @Override
    public void stop() throws LifecycleException {

    }

    /**
     * �첽�߳���Ҫ�ǵ���processReceive()����
     * ͨ��clusterReceiver.getObjects()����
     * ��ȡ��Ⱥ�����ڵ��Ա����Ϣ(ע��:������ClusterMemberInfo,
     * �����ǻ�ȡ��Ⱥ�����ڵ㷢�͵���Ϣ)
     */
    @Override
    public void run() {
        processReceive();
        try {
            Thread.sleep(Constants.CHECK_INTERVAL);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


    /**
     * ���ڽ���
     */
    public void processReceive(){
        Object[] objects = this.clusterReceiver.getObjects();

        for(Object object: objects){
            ClusterMemberInfo clusterMemberInfo = (ClusterMemberInfo) object;
            this.clusterMembers.add(clusterMemberInfo);
        }

        /**
         * ���ڴ�ӡһ�¼�Ⱥ�нڵ����Ϣ
         */
        printClusterMembersInfo();

    }

    public void threadStart(){

        thread = new Thread(this, "StandardCluster");
        thread.setDaemon(true);
        thread.start();

    }

    /**
     * ���������Ҫ�Ǵ�ӡ��ǰ��Ⱥ�����ڵ����Ϣ
     */
    public void printClusterMembersInfo(){

        System.out.println("��ǰ��Ⱥ�нڵ�Ϊ111��");
        for(ClusterMemberInfo clusterMemberInfo: this.clusterMembers){
            /**
             * ��ӡһ��clusterName
             * ��ע�����ֵ��BootStrap������
             */
            System.out.println(">>>>>>" + clusterMemberInfo.getClusterName());
        }


    }


    /*
      getter()/setter()
     */
    public int getMulticastPort() {
        return multicastPort;
    }

    public void setMulticastPort(int multicastPort) {
        this.multicastPort = multicastPort;
    }

    public InetAddress getMulticastAddress() {
        return multicastAddress;
    }


    public void setMulticastAddress(String address) {

        try {
            this.multicastAddress = InetAddress.getByName(address);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
