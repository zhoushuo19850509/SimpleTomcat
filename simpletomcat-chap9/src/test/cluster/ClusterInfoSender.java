package cluster;


import com.nbcb.mytomcat.catalina.cluster.ClusterMemberInfo;
import com.nbcb.mytomcat.catalina.cluster.MulticastSender;
import org.apache.catalina.cluster.ClusterSender;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * ������룬��Ҫ����ģ��MulticastSender�ķ��͹���
 * ģ��StandardCluster���������ڵ���Ϣͬ������Ⱥ�����ڵ�
 * ������Բο�StandardCluster.start()
 *
 */
public class ClusterInfoSender {

    /**
     * ���main����������ģ��MulticastSender�ķ��͹���
     * @param args
     */
    public static void main(String[] args) {

        System.out.println("start ClusterInfoSender");

        try {

            /**
             * ����MuticastSender����
             */

            // senderId��ʶ������
            String senderId = "StandardCluster";

            // �鲥��ַ
            InetAddress address = InetAddress.getByName("all-systems.mcast.net");
            int port = 8999;

            // ��ʼ��MulticastSocket
            MulticastSocket multicastSocket = new MulticastSocket(port);
            multicastSocket.joinGroup(address);

            ClusterSender sender =
                    new MulticastSender(senderId, multicastSocket, address, port);

            /**
             * ����Ҫ���͵�ClusterMemberInfo����
             */
            ClusterMemberInfo clusterMemberInfo = new ClusterMemberInfo();
            clusterMemberInfo.setHostName(null);
            clusterMemberInfo.setClusterName("MySimpleTomcat1");  // ��ʶ��ǰ�ڵ����Ϣ�����������ļ���
            clusterMemberInfo.setClusterInfo("StandardCluster/1.0");

            // �ѵ�ǰ�ڵ���Ϣ���͸���Ⱥ
            sender.send(clusterMemberInfo);

        } catch (IOException e) {
            e.printStackTrace();
        }





    }


}
