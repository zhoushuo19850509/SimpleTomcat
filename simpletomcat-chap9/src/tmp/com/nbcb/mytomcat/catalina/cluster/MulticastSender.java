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
     * MulticastSender���͸��鲥��ַ��ͨ�Ŷ˿�
     * ����˿�һ����������ļ�
     * ����������ʱ�ŵ�Constants.java����Ϊ��������
     */
    private int multicastPort;

    /**
     * MulticastSenderҪ�ͼ�Ⱥ�����ڵ�(ͨ��UDP����)ͨ��
     * ��Ҫ����һ��MulticastSocket����
     */
    private MulticastSocket multicastSocket;

    /**
     * �鲥��ַ
     * ����tomcat��Ⱥ�ĸ����ڵ㶼���������ַ�շ���Ϣ
     */
    private InetAddress multicastAddress = null;

    /**
     * ��ʶ������ID
     */
    private String senderId = null;


    /**
     * Constructor of MulticastSender��������
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
     * ����Ϣ���͵��鲥��ַ�������͸���Ⱥ�и����ڵ�
     * ��Ҫ�ر�ע�⣺�ڷ���byte[]֮ǰ����Ҫ�ȴ���ReplicationWrapperʵ��
     * ��Ҫ�͵�byte[]������ΪReplicationWrapper���������
     * �������յ�ʱ��Ҳ��һ�������յ�object֮����ת��ΪReplicationWrapper����
     * Ȼ�����ReplicationWrapper��senderId���ж���Ϣ�Ƿ��Ƿ����Լ���
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
     * �ֽ�objectת��Ϊbyte[]
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
