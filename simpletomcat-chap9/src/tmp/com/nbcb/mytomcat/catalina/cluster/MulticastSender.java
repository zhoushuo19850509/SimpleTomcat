package com.nbcb.mytomcat.catalina.cluster;

import org.apache.catalina.Logger;
import org.apache.catalina.cluster.ClusterMemberInfo;
import org.apache.catalina.cluster.ClusterSender;
import org.apache.catalina.cluster.ReplicationWrapper;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MulticastSender extends ClusterSessionBase implements ClusterSender{

    /**
     * MulticastSender发送给组播地址的通信端口
     * 这个端口一般放在配置文件
     * 现在我们暂时放到Constants.java中作为常量保存
     */
    private int multicastPort;

    /**
     * MulticastSender要和集群其他节点(通过UDP进行)通信
     * 就要创建一个MulticastSocket对象
     */
    private MulticastSocket multicastSocket;

    /**
     * 组播地址
     * 整个tomcat集群的各个节点都会往这个地址收发消息
     */
    private InetAddress multicastAddress = null;

    /**
     * 标识发送者ID
     */
    private String senderId = null;


    /**
     * Constructor of MulticastSender各个属性
     * @param senderId
     * @param multicastPort
     * @param multicastSocket
     * @param multicastAddress
     */
    public MulticastSender(String senderId,
                           MulticastSocket multicastSocket,
                           InetAddress multicastAddress,
                           int multicastPort) {
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
     * 将信息发送到组播地址，即发送给集群中各个节点
     * 需要特别注意：在发送byte[]之前，需要先创建ReplicationWrapper实例
     * 将要送的byte[]内容作为ReplicationWrapper对象的属性
     * 后续接收的时候也是一样，接收到object之后，先转化为ReplicationWrapper对象
     * 然后根据ReplicationWrapper的senderId来判断消息是否是发给自己的
     * @param b the bytearray to send
     */
    @Override
    public void send(byte[] b) {

        ReplicationWrapper wrapper = new ReplicationWrapper(b, this.senderId);
        ObjectOutputStream oos = null;
        ByteArrayOutputStream bos = null;


        try {
            bos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(new BufferedOutputStream(bos));

            oos.writeObject(wrapper);
            oos.flush();

            byte[] obs = bos.toByteArray();

            DatagramPacket packet = new DatagramPacket(obs, obs.length,
                    this.multicastAddress, this.multicastPort);

            this.multicastSocket.send(packet);
            System.out.println("multicast sender finish send object!");
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    /**
     * 现将object转化为byte[]
     * @param o The object to send
     */
    @Override
    public void send(Object o) {
        ByteArrayOutputStream bos = null;
        ObjectOutputStream oos = null;


        try {
            bos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(new BufferedOutputStream(bos));

            oos.writeObject(o);
            oos.flush();

            byte[] obs = bos.toByteArray();
            System.out.println("sender byte length :" + obs.length);

            send(obs);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(bos != null){
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(oos != null){
                try {
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }




}
