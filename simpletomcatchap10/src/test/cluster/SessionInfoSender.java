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
 * 场景2 模拟DistributeManager，将新增session对象同步给集群其他节点
 * 参考DistributeManager.createSession()
 */
public class SessionInfoSender {

    public static void main(String[] args) {

        /**
         * 参考DistributedManager的逻辑
         * 将"DistributedManager"作为senderId
         * 这个senderId作为UDP通信的分组标识，专门用于DistributedManager在集群中共享session信息
         */
        String senderId = "DistributedManager";

        System.out.println("start SessionInfoSender");

        try {

            /**
             * 创建MuticastSender对象
             */

            /**
             * 组播地址和组播端口
             * 这个地址和端口和ClusterInfoSender一致
             * 在同一个组播地址中通信，通过senderId分组
             */
            InetAddress address = InetAddress.getByName("all-systems.mcast.net");
            int port = 8999;

            // 初始化MulticastSocket
            MulticastSocket multicastSocket = new MulticastSocket(port);
            multicastSocket.joinGroup(address);

            ClusterSender sender =
                    new MulticastSender(senderId, multicastSocket, address, port);

            /**
             * 创建要发送的Session对象
             */
            Session session = new StandardSession(null);
            session.setNew(true);
            session.setValid(true);
            session.setCreationTime(System.currentTimeMillis());
            session.setMaxInactiveInterval(Constants.MAX_INACTIVE_INTERVAL);
            session.setId("abc");


            // 把session信息发送给集群
            sender.send(session);

        } catch (IOException e) {
            e.printStackTrace();
        }





    }
}
