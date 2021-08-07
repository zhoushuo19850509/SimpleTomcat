package test.cluster;

import com.nbcb.mytomcat.catalina.cluster.MulticastReceiver;
import com.nbcb.mytomcat.catalina.session.StandardSession;
import org.apache.catalina.Session;
import org.apache.catalina.cluster.ReplicationWrapper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * ����2 ģ��DistributeManager�����ڴӼ�Ⱥ���������ڵ��session��Ϣ
 * �ο���
 * DistributedManager.processClusterReceiver()
 */
public class SessionInfoReceiver {
    public static void main(String[] args) {
        SessionInfoReceiver sessionInfoReceiver = new SessionInfoReceiver();
        sessionInfoReceiver.startReceive();

    }


    public void startReceive(){
        System.out.println("start SessionInfoReceiver");


        /**
         * �ο�DistributedManager���߼�
         * ��"DistributedManager"��ΪsenderId
         * ���senderId��ΪUDPͨ�ŵķ����ʶ��ר������DistributedManager�ڼ�Ⱥ�й���session��Ϣ
         */
        String senderId = "DistributedManager";

        try {
            /**
             * �ȴ���MulticastReceiver����
             */


            // �鲥��ַ
            InetAddress address = InetAddress.getByName("all-systems.mcast.net");
            int port = 8999;

            // ��ʼ��MulticastSocket
            MulticastSocket multicastSocket = new MulticastSocket(port);
            multicastSocket.joinGroup(address);

            MulticastReceiver multicastReceiver =
                    new MulticastReceiver(senderId, port, multicastSocket, address);

            /**
             * ����MulticastReceiver����
             */
            multicastReceiver.start();

            ByteArrayInputStream bis = null;
            ObjectInputStream ois = null;

            /**
             * ����һ��whileѭ�����ڽ��������鲥��ַ����Ϣ
             */
            while(true){
                System.out.println("try to get object(StandardSession) from the Cluster");

                try {
                    /**
                     * ���յ���Ϣ�����б�
                     */
                    Object[] objs = multicastReceiver.getObjects();

                    if(null != objs && objs.length > 0){
                        System.out.println("received sth!");
                        /**
                         * ���������б�
                         */
                        for(Object obj : objs){
                            /**
                             * �������Ӧ����һ��ReplicationWrapper����
                             */
                            ReplicationWrapper wrapper = (ReplicationWrapper)obj;

                            /**
                             * �ȿ��������Ϣ��˭���͵�
                             */
                            System.out.println("senderId: " + wrapper.getSenderId());

                            /**
                             * �ٿ�����Ϣ������ɶ
                             * ���Ǵ��鲥��ַ��ȡ������Ϣ������byte array
                             */
                            byte[] bytes = wrapper.getDataStream();
                            System.out.println("wrapper's data stream byte length: " + bytes.length);
                            bis = new ByteArrayInputStream(bytes);
                            ois = new ObjectInputStream(bis);

                            /**
                             * ��byte����ת��Ϊ����
                             */
                            Session session = null;
                            Object tmpObj = ois.readObject();
                            session = (StandardSession) tmpObj;

                            /**
                             * ��ӡһ��StandardSession������Ϣ
                             */
                            System.out.println("session id : " + session.getId());
                            System.out.println("session create time : " + session.getCreationTime());
                        }
                    }
                    Thread.sleep(5000);
                } catch (InterruptedException | ClassNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    if(null != bis){
                        bis.close();
                    }
                    if(null != ois){
                        ois.close();
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }



    }
}
