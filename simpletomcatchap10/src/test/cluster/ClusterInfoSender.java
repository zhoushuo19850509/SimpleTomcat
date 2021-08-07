package cluster;


import com.nbcb.mytomcat.catalina.cluster.ClusterMemberInfo;
import com.nbcb.mytomcat.catalina.cluster.MulticastSender;
import org.apache.catalina.cluster.ClusterSender;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * 这个代码，主要用于模拟MulticastSender的发送功能
 * 模拟StandardCluster，将新增节点信息同步给集群其他节点
 * 具体可以参考StandardCluster.start()
 *
 */
public class ClusterInfoSender {

    /**
     * 这个main方法，用于模拟MulticastSender的发送功能
     * @param args
     */
    public static void main(String[] args) {

        System.out.println("start ClusterInfoSender");

        try {

            /**
             * 创建MuticastSender对象
             */

            // senderId标识发送者
            String senderId = "StandardCluster";

            // 组播地址
            InetAddress address = InetAddress.getByName("all-systems.mcast.net");
            int port = 8999;

            // 初始化MulticastSocket
            MulticastSocket multicastSocket = new MulticastSocket(port);
            multicastSocket.joinGroup(address);

            ClusterSender sender =
                    new MulticastSender(senderId, multicastSocket, address, port);

            /**
             * 创建要发送的ClusterMemberInfo对象
             */
            ClusterMemberInfo clusterMemberInfo = new ClusterMemberInfo();
            clusterMemberInfo.setHostName(null);
            clusterMemberInfo.setClusterName("MySimpleTomcat1");  // 标识当前节点的信息，放在配置文件中
            clusterMemberInfo.setClusterInfo("StandardCluster/1.0");

            // 把当前节点信息发送给集群
            sender.send(clusterMemberInfo);

        } catch (IOException e) {
            e.printStackTrace();
        }





    }


}
