package cluster;

import com.nbcb.mytomcat.catalina.cluster.MulticastSender;
import com.nbcb.mytomcat.catalina.session.StandardSession;
import com.nbcb.mytomcat.catalina.util.Constants;
import org.apache.catalina.Session;
import org.apache.catalina.cluster.ClusterSender;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * ����2 ģ��DistributeManager��������session����ͬ������Ⱥ�����ڵ�
 * �ο�DistributeManager.createSession()
 */
public class SessionInfoSender {

    public static void main(String[] args) {

        /**
         * �ο�DistributedManager���߼�
         * ��"DistributedManager"��ΪsenderId
         * ���senderId��ΪUDPͨ�ŵķ����ʶ��ר������DistributedManager�ڼ�Ⱥ�й���session��Ϣ
         */
        String senderId = "DistributedManager";

        System.out.println("start SessionInfoSender");

        try {

            /**
             * ����MuticastSender����
             */

            /**
             * �鲥��ַ���鲥�˿�
             * �����ַ�Ͷ˿ں�ClusterInfoSenderһ��
             * ��ͬһ���鲥��ַ��ͨ�ţ�ͨ��senderId����
             */
            InetAddress address = InetAddress.getByName("all-systems.mcast.net");
            int port = 8999;

            // ��ʼ��MulticastSocket
            MulticastSocket multicastSocket = new MulticastSocket(port);
            multicastSocket.joinGroup(address);

            ClusterSender sender =
                    new MulticastSender(senderId, multicastSocket, address, port);

            /**
             * ����Ҫ���͵�Session����
             */
            Session session = new StandardSession(null);
            session.setNew(true);
            session.setValid(true);
            session.setCreationTime(System.currentTimeMillis());
            session.setMaxInactiveInterval(Constants.MAX_INACTIVE_INTERVAL);
            session.setId("abc");


            // ��session��Ϣ���͸���Ⱥ
            sender.send(session);

        } catch (IOException e) {
            e.printStackTrace();
        }





    }
}
