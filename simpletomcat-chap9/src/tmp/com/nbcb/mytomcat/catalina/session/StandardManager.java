package com.nbcb.mytomcat.catalina.session;


import com.nbcb.mytomcat.catalina.util.Constants;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Session;

import java.util.List;

public class StandardManager
        extends ManagerBase
        implements Runnable, Lifecycle {

    /**
     * 当前StandardManager对象启动的异步线程
     */
    private Thread thread = null;


    /**
     * 定期检测Manager管理下的session，
     * 检查是否有过期的session对象
     */
    @Override
    public void run() {
        while(true){
            try {
                Thread.sleep(Constants.CHECK_INTERVAL * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            processExpires();
            printSessionStatus();
        }

    }

    /**
     * 打印Manager管理下的各个session的状态
     * 包括活跃session、本次设置为超时的session、回收队列中的session、以及新创建的session
     */
    public void printSessionStatus(){
        List<Session> sessions = findSessions();

        /**
         * 先打印活跃session队列
         */
        System.out.println("Active session list: ");
        for (int i = 0; i < sessions.size(); i++) {
            Session session = sessions.get(i);
            System.out.println("[" + i + "] sid: " + session.getId() );
        }
        /**
         * 再打印回收队列的session
         */
        System.out.println("Recycled session list: ");
        for (int i = 0; i < recycled.size(); i++) {
            Session recycledSession = recycled.get(i);
            System.out.println("[" + i + "] recycled Session " );
        }
    }


    /**
     * 处理那些过期的session对象
     */
    public void processExpires(){


        /**
         * 检查各个session最近一次登录的时间
         * 如果最近一次登录的时间和当前时间相比，超过了一定的时长，就将这个session设置为超时
         */
        List<Session> sessionList = findSessions();
        long now = System.currentTimeMillis();
        for(Session session : sessionList){
            String sid = session.getId();

            /**
             * 最近一次访问时间
             */
            long lastAccessTime = session.getLastAccessedTime();

            /**
             * 现在距离最近一次访问已经过去了多少时间(ms)
             */
            long duration = (int) ((now - lastAccessTime) / 1000L);

            System.out.println("sid: " + sid + " 距离最近一次访问过去了: " +duration + " s" );

            int maxInactiveInterval = session.getMaxInactiveInterval();
            if(duration > maxInactiveInterval){
                session.expire();
                System.out.println("session has expired! sid: " + sid);
                System.out.println("session 最长空闲时间为： " + maxInactiveInterval);
                System.out.println("距离最近一次访问过去了： " + duration);
            }
        }
    }


    /**
     * 以下方法是实现Lifecycle接口的
     * 这样Manager就有了生命周期：
     * 当Manager创建的时候，会从tomcat本地目录下读取一个文件：
     * SESSIONS.ser
     * 这个文件保存了持久化在tomcat本地目录下的session信息
     * @param listener The listener to add
     */
    @Override
    public void addLifecycleListener(LifecycleListener listener) {

    }

    @Override
    public LifecycleListener[] findLifecycleListeners() {
        return new LifecycleListener[0];
    }

    @Override
    public void removeLifecycleListener(LifecycleListener listener) {

    }

    /**
     * StandardManager启动的时候，主要做2件事情：
     * 1.看看本地文件系统是否有需要启动的session，这个可能是上次关闭后，持久化到本地文件系统的session信息
     * 2.启动异步线程，定期将过期的session清理掉
     * @throws LifecycleException
     */
    @Override
    public void start() throws LifecycleException {
        System.out.println("StrandardManager started! ");

        load();

        threadStart();

    }

    @Override
    public void stop() throws LifecycleException {

    }

    public void load(){


    }

    private void threadStart(){

        thread = new Thread(this, "StandardManagerThread");
        thread.setDaemon(true);
        thread.start();

    }


}
