package com.nbcb.mytomcat.catalina.session;

import org.apache.catalina.Cluster;
import org.apache.catalina.Container;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Session;
import org.apache.catalina.cluster.ClusterReceiver;
import org.apache.catalina.cluster.ClusterSender;
import org.apache.catalina.cluster.ReplicationWrapper;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * 这个Manager专门用来管理分布式session
 * 1.负责将session同步到集群中的各个节点；
 * 2.负责清理集群中的过期session
 */
public class DistributedManager extends PersistentManagerBase {


    /**
     * 将session消息发送给集群中各个节点
     */
    private ClusterSender clusterSender;

    /**
     * 接收集群中各个节点发来的消息内容
     *
     */
    private ClusterReceiver clusterReceiver;


    protected String name = "DistributedManager";




    /**
     * 创建session对象之后，将session信息同步给整个tomcat集群各个节点上
     *
     * @return
     */
    public Session createSession(){
        /**
         * 先调用父类(ManagerBase)，按照原来的方式，创建一个session对象
         */
        Session session = super.createSession();

        /**
         * 然后将新创建的session对象转化为byte[]，同步到集群的各个节点
         */
        ObjectOutputStream oos = null;
        ByteArrayOutputStream bos = null;

        try {
            bos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(new BufferedOutputStream(bos));
            ((StandardSession)session).writeObject(oos);
            byte[] obs = bos.toByteArray();
            this.clusterSender.send(obs);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(oos != null){
                try {
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(bos != null){
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
        return null;
    }

    /**
     * 定期处理来自其他节点的消息
     *
     */
    public void processClusterReceiver(){

        /**
         * 先通过clusterReceiver，从集群拉取消息
         */
        Object[] objects = this.clusterReceiver.getObjects();

        ReplicationWrapper wrapper = null;
        Session session = null;
        byte[] buf = new byte[5000];

        /**
         * 遍历拉取到的消息列表
         */
        for (int i = 0; i < objects.length; i++) {


            /**
             * 需要注意的是，拉取到的消息对象，也是封装成ReplicationWrapper对象的
             * 和之前StandardCluster.start()中发送节点信息不一样，这里接收到的Wrapper中的对象是byte[]
             */
            wrapper = (ReplicationWrapper) objects[i];
            buf = wrapper.getDataStream();



            /**
             * 将拉取到的byte[]转化为对象
             */

            /**
             * 调用父类的createSession()创建一个session实例
             */
            session = super.createSession();

            /**
             * 然后把从集群中拉取的session对象赋值给这个新创建的session
             * 这样就完成了集群的session同步
             */

            
        }




    }

    /**
     * 异步线程
     */
    public void run(){
        processClusterReceiver();
        processExpires();
        processPersistenceChecks();
    }

    /**
     * 除了调用父类的start()方法，还要初始化clusterSender/clusterReceiver
     * @throws LifecycleException
     */
    public void start() throws LifecycleException {
        Container container = getContainer();

        /**
         * 初始化clusterSender/clusterReceiver
         */
        Cluster cluster = null;
        cluster = container.getCluster();
        this.clusterSender = cluster.getClusterSender(getName());
        this.clusterReceiver = cluster.getClusterReceiver(getName());


        super.start();

    }

    /**
     * getter()/setter()
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
