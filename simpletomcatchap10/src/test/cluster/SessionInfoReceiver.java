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
 * 场景2 模拟DistributeManager，定期从集群接收其他节点的session信息
 * 参考：
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
         * 参考DistributedManager的逻辑
         * 将"DistributedManager"作为senderId
         * 这个senderId作为UDP通信的分组标识，专门用于DistributedManager在集群中共享session信息
         */
        String senderId = "DistributedManager";

        try {
            /**
             * 先创建MulticastReceiver对象
             */


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
                System.out.println("try to get object(StandardSession) from the Cluster");

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
                            Session session = null;
                            Object tmpObj = ois.readObject();
                            session = (StandardSession) tmpObj;

                            /**
                             * 打印一下StandardSession对象信息
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
