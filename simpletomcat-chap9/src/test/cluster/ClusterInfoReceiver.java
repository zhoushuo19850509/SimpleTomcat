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
 * 这个代码，主要用于验证MulticastReceiver的功能
 * 模拟MulticastReceiver从集群拉取集群其他节点的信息
 * 具体参考StandardCluster.processReceive()
 *
 * 特别备注：
 * 这里我们收发的ClusterMemberInfo，必须是我们新定义的
 * com.nbcb.mytomcat.catalina.cluster这个package下的ClusterMemberInfo
 *
 * 至此，我们调试ClusterInfoReceiver通之后，就能够通过UDP协议在集群内部同步通用对象了
 * 当然，当前这个例子中，同步的对象还比较简单，后续看看Session对象同步是否会有其他新的问题
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
             * 先创建MulticastReceiver对象
             */

            // senderId标识发送者
            String senderId = "StandardCluster";

            // 组播地址
            InetAddress address = InetAddress.getByName("all-systems.mcast.net");
            int port = 8999;

            // 初始化MulticastSocket
            MulticastSocket multicastSocket = new MulticastSocket(port);
            multicastSocket.joinGroup(address);

            MulticastReceiver multicastReceiver =
                    new MulticastReceiver(senderId, port, multicastSocket, address);

            /**
             * 启动MulticastReceiver对象
             */
            multicastReceiver.start();

            ByteArrayInputStream bis = null;
            ObjectInputStream ois = null;

            /**
             * 启动一个while循环定期接收来自组播地址的消息
             */
            while(true){
                System.out.println("try to get object(ClusterMemberInfo) from the Cluster");

                try {
                    /**
                     * 接收到消息对象列表
                     */
                    Object[] objs = multicastReceiver.getObjects();

                    if(null != objs && objs.length > 0){
                        System.out.println("received sth!");
                        /**
                         * 遍历对象列表
                         */
                        for(Object obj : objs){
                            /**
                             * 这个对象应该是一个ReplicationWrapper对象
                             */
                            ReplicationWrapper wrapper = (ReplicationWrapper)obj;

                            /**
                             * 先看看这个消息是谁发送的
                             */
                            System.out.println("senderId: " + wrapper.getSenderId());

                            /**
                             * 再看看消息内容是啥
                             * 我们从组播地址获取到的消息内容是byte array
                             */
                            byte[] bytes = wrapper.getDataStream();
                            System.out.println("wrapper's data stream byte length: " + bytes.length);
                            bis = new ByteArrayInputStream(bytes);
                            ois = new ObjectInputStream(bis);

                            /**
                             * 将byte数组转化为对象
                             */
                            ClusterMemberInfo memberInfo = null;
                            Object tmpObj = ois.readObject();
                            memberInfo = (ClusterMemberInfo) tmpObj;

                            /**
                             * 打印一下ClusterMemberInfo对象信息
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
