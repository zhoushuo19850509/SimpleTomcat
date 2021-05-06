package test.cluster;




import com.nbcb.mytomcat.catalina.cluster.ClusterMemberInfo;
import com.nbcb.mytomcat.catalina.cluster.MulticastReceiver;
import org.apache.catalina.cluster.ReplicationWrapper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * ������룬��Ҫ������֤MulticastReceiver�Ĺ���
 * ģ��MulticastReceiver�Ӽ�Ⱥ��ȡ��Ⱥ�����ڵ����Ϣ
 * ����ο�StandardCluster.processReceive()
 *
 * �ر�ע��
 * ���������շ���ClusterMemberInfo�������������¶����
 * com.nbcb.mytomcat.catalina.cluster���package�µ�ClusterMemberInfo
 *
 * ���ˣ����ǵ���ClusterInfoReceiver֮ͨ�󣬾��ܹ�ͨ��UDPЭ���ڼ�Ⱥ�ڲ�ͬ��ͨ�ö�����
 * ��Ȼ����ǰ��������У�ͬ���Ķ��󻹱Ƚϼ򵥣���������Session����ͬ���Ƿ���������µ�����
 *
 */
public class ClusterInfoReceiver {

    public static void main(String[] args) {
        ClusterInfoReceiver clusterInfoReceiver = new ClusterInfoReceiver();
        clusterInfoReceiver.startReceive();

    }


    public void startReceive(){
        System.out.println("start ClusterInfoReceiver");


        try {
            /**
             * �ȴ���MulticastReceiver����
             */

            // senderId��ʶ������
            String senderId = "StandardCluster";

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
                System.out.println("try to get object(ClusterMemberInfo) from the Cluster");

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
                            ClusterMemberInfo memberInfo = null;
                            Object tmpObj = ois.readObject();
                            memberInfo = (ClusterMemberInfo) tmpObj;

                            /**
                             * ��ӡһ��ClusterMemberInfo������Ϣ
                             */
                            System.out.println("getClusterInfo : " + memberInfo.getClusterInfo());
                            System.out.println("getClusterName : " + memberInfo.getClusterName());
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
