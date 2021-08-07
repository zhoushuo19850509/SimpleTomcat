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
 * 标准Cluster实现
 */
public class StandardCluster implements Cluster, Lifecycle, Runnable {


    /**
     * 当前集群约定的通信端口
     * 当前节点无论是往组播地址发送消息，还是接收消息
     * 都需要这样一个约定的端口
     * 这个端口一般放在配置文件
     * 现在我们暂时放到Constants.java中作为常量保存
     */
    private int multicastPort;

    /**
     * 当前节点要和集群其他节点(通过UDP进行)通信
     * 就要创建一个MulticastSocket对象
     */
    private MulticastSocket multicastSocket;

    /**
     * 组播地址
     * 整个tomcat集群的各个节点都会往这个地址收发消息
     */
    private InetAddress multicastAddress = null;


    private ClusterSender clusterSender = null;
    private ClusterReceiver clusterReceiver = null;

    /**
     * 集群节点信息实例
     * 这个就用catalina官方的类好了，反正逻辑也不复杂
     */
    private ClusterMemberInfo clusterMemberInfo = null;


    /**
     * Cluster实现类的名称，用于日志打印
     */
    private String clusterImpName = "StandardCluster";

    /**
     * 集群名称
     */
    private String clusterName = null;

    /**
     * 节点信息
     */
    private static final String info = "StandardCluster/1.0";

    /**
     * 当前StandardCluster代表的线程
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
     * 这个方法看似是getClusterSender()
     * 其实叫createClusterSender()更加合适
     * 因为这个方法其实是创建一个MulticastSender方法
     * 通过param(senderId)来标识不同的用途
     * 比如StandardCluster.run()的异步线程就用这个创建的ClusterSender对象发送当前节点信息到集群
     * DistributedManager用ClusterSender对象发送session消息到集群，实现session在集群内部同步的目标
     * 不同的场景和用途，通过senderId来加以区别
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
         * 获取MulticastReceiver对象之前，先要启动MulticastReceiver对象
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
     * 实现LifeCycle接口,对StandardCluster进行初始化
     * (1)初始化ClusterMemberInfo对象(本地节点对象)
     * (2)创建ClusterSender对象(MulticastSender)，用于发送节点信息给集群各个节点
     * (3)创建ClusterReceiver对象(MuticastReceiver)，用于接收集群其他节点的节点信息
     * (4)通过ClusterSender,将刚刚创建的ClusterMemberInfo对象(本地节点对象),
     *    发送个集群其他节点;
     * @TODO
     * @throws LifecycleException
     */
    @Override
    public void start() throws LifecycleException {


        try {
            /**
             * 创建一个MultiSocket对象
             * 用于后续sender/receiver
             */
            multicastSocket = new MulticastSocket(this.multicastPort);

            /**
             * MulticastSocket常规操作：监听一个组播地址
             */
            multicastSocket.joinGroup(this.multicastAddress);

            /**
             * 创建ClusterSender对象(MulticastSender)，用于发送节点信息给集群各个节点
             */
            this.clusterSender = getClusterSender(getName());

            /**
             * 创建ClusterReceiver对象(MuticastReceiver)，用于接收集群其他节点的节点信息
             */
            this.clusterReceiver = getClusterReceiver(getName());

            /**
             * 初始化ClusterMemberInfo对象(本地节点对象)
             */
            this.clusterMemberInfo = new ClusterMemberInfo();
            clusterMemberInfo.setClusterInfo(getInfo());
            clusterMemberInfo.setClusterName(getClusterName());
            clusterMemberInfo.setHostName(null);

            /**
             * 通过ClusterSender,将刚刚创建的ClusterMemberInfo对象(本地节点对象),
             * 发送给集群其他节点;
             */
            clusterSender.send(clusterMemberInfo);


            /**
             * 启动背景线程，定期接收来自其他节点的信息
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
     * 异步线程主要是调用processReceive()方法
     * 通过clusterReceiver.getObjects()方法
     * 获取集群其他节点成员的信息(注意:是新增ClusterMemberInfo,
     * 而不是获取集群其他节点发送的信息)
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
     * 定期接收
     */
    public void processReceive(){
        Object[] objects = this.clusterReceiver.getObjects();

        for(Object object: objects){
            ClusterMemberInfo clusterMemberInfo = (ClusterMemberInfo) object;
            this.clusterMembers.add(clusterMemberInfo);
        }

        /**
         * 定期打印一下集群中节点的信息
         */
        printClusterMembersInfo();

    }

    public void threadStart(){

        thread = new Thread(this, "StandardCluster");
        thread.setDaemon(true);
        thread.start();

    }

    /**
     * 这个方法主要是打印当前集群各个节点的信息
     */
    public void printClusterMembersInfo(){

        System.out.println("当前集群中节点为111：");
        for(ClusterMemberInfo clusterMemberInfo: this.clusterMembers){
            /**
             * 打印一下clusterName
             * 备注：这个值在BootStrap中设置
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
