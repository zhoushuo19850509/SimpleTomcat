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
     * MulticastReceiver���鲥��ַ������Ϣ��ͨ�Ŷ˿�
     * ����˿�һ����������ļ�
     * ����������ʱ�ŵ�Constants.java����Ϊ��������
     */
    private int multicastPort;

    /**
     * MulticastReceiverҪ�ͼ�Ⱥ�����ڵ�(ͨ��UDP����)ͨ��
     * ��Ҫ����һ��MulticastSocket����
     */
    private MulticastSocket multicastSocket;

    /**
     * �鲥��ַ
     * ����tomcat��Ⱥ�ĸ����ڵ㶼���������ַ�շ���Ϣ
     */
    private InetAddress multicastAddress = null;

    /**
     * ��ǰMulticastReceiver������߳�
     */
    private Thread thread = null;

    /**
     * ��ʶ��ǰReceiver��Ӧ��sender
     */
    private String senderId = null;

    /**
     * MulticastReceiver�����첽�̣߳�������ȡ��Ⱥ�������ڵ����Ϣ,�ŵ����������
     */
    private List<ReplicationWrapper> msgList = new ArrayList<ReplicationWrapper>();


    /**
     * Constructor of MulticastSender��������
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
     * MulticastReceiver�����첽�̣߳�������ȡ��Ⱥ�������ڵ����Ϣ,�ŵ�һ�������
     * ���getObjects()�����������������ж�ȡ�ڵ���Ϣ����ȡ���������
     * @return �Ӽ�Ⱥ�ڵ���յ���(ReplicationWrapper)����
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
     * ���������鲥��ַ������
     * ��Ȼ��Ҫ����ReplicationWrapper.senderId���ж������ǲ��Ƿ����Լ���
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
     * Receiver�ᶨ�ڴ��鲥��ַ�������Լ�Ⱥ����Ϣ
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
