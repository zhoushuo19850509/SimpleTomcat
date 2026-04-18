package com.nbcb.mytomcat.catalina.cluster;

import com.nbcb.mytomcat.catalina.util.Constants;
import org.apache.catalina.Logger;
import org.apache.catalina.cluster.ClusterReceiver;
import org.apache.catalina.cluster.ReplicationWrapper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.List;

public class MulticastReceiver extends ClusterSessionBase implements ClusterReceiver {

    /**
     * MulticastReceiver从组播地址接收消息的通信端口
     * 这个端口一般放在配置文件
     * 现在我们暂时放到Constants.java中作为常量保存
     */
    private int multicastPort;

    /**
     * MulticastReceiver要和集群其他节点(通过UDP进行)通信
     * 就要创建一个MulticastSocket对象
     */
    private MulticastSocket multicastSocket;

    /**
     * 组播地址
     * 整个tomcat集群的各个节点都会往这个地址收发消息
     */
    private InetAddress multicastAddress = null;

    /**
     * 当前MulticastReceiver代表的线程
     */
    private Thread thread = null;

    /**
     * 标识当前Receiver对应的sender
     */
    private String senderId = null;

    /**
     * MulticastReceiver会有异步线程，定期拉取集群中其他节点的消息,放到这个缓存里
     */
    private List<ReplicationWrapper> msgList = new ArrayList<ReplicationWrapper>();


    /**
     * Constructor of MulticastSender各个属性
     * @param senderId
     * @param multicastPort
     * @param multicastSocket
     * @param multicastAddress
     */
    public MulticastReceiver(String senderId,
                           int multicastPort,
                           MulticastSocket multicastSocket,
                           InetAddress multicastAddress) {
        this.multicastPort = multicastPort;
        this.multicastSocket = multicastSocket;
        this.multicastAddress = multicastAddress;
        this.senderId = senderId;
    }
    @Override
    public void setSenderId(String senderId) {

    }

    @Override
    public String getSenderId() {
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
    public void setCheckInterval(int checkInterval) {

    }

    @Override
    public int getCheckInterval() {
        return 0;
    }

    @Override
    public void setLogger(Logger logger) {

    }

    @Override
    public Logger getLogger() {
        return null;
    }

    @Override
    public void log(String message) {

    }

    /**
     * MulticastReceiver会有异步线程，定期拉取集群中其他节点的信息,放到一个缓存里。
     * 这个getObjects()方法负责从这个缓存中读取节点信息，读取后，清除缓存
     * @return 从集群节点接收到的(ReplicationWrapper)对象
     */
    @Override
    public Object[] getObjects() {

        synchronized (msgList){
            Object[]  objects = msgList.toArray();
            msgList.clear();
            return objects;
        }
    }

    /**
     * 接收来自组播地址的数据
     * 当然，要根据ReplicationWrapper.senderId来判断数据是不是发给自己的
     */
    private void receive(){
        System.out.println("multicast receiver start receive!");
        byte[] buffer = new byte[4096];
        DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
        ByteArrayInputStream ips = null;
        ObjectInputStream ois = null;

        try {
            System.out.println("MulticastReceiver start receive msg!");
            this.multicastSocket.receive(dp);
            System.out.println("multicast receiver received sth!");
            ips = new ByteArrayInputStream(buffer, 0, buffer.length);
            ois = new ObjectInputStream(ips);
            ReplicationWrapper obj = (ReplicationWrapper)ois.readObject();

            if(obj.getSenderId().equals(this.senderId)){
                msgList.add(obj);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    /**
     * Receiver会定期从组播地址接收来自集群的信息
     */
    @Override
    public void run() {
        receive();
        try {
            Thread.sleep(Constants.CHECK_INTERVAL * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void start() {

        threadStart();
    }


    @Override
    public void stop() {

    }

    public void threadStart(){

        thread = new Thread(this, "MulticastReceiver");
        thread.setDaemon(true);
        thread.start();

    }


}
