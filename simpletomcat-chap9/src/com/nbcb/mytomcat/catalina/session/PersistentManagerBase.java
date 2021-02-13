package com.nbcb.mytomcat.catalina.session;


import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleListener;

public class PersistentManagerBase extends ManagerBase
    implements Runnable, Lifecycle

{



    /**
     * 1.定期检测失效的session
     * 2.定期将session持久化
     */
    @Override
    public void run() {

    }

    /**
     * 处理那些过期的session对象
     */
    public void processExpires(){

    }

    /**
     * 下面三个方法，是PersistentManagerBase对session对象实现持久化管理。
     * 啥意思呢？就是内存中的session对象空闲了很久，
     * 或者活跃的session对象太多达到了MaxActive的配置数量。
     * 那么PersistentManagerBase会把这些session Swap out到持久层(文件系统或者数据库)。
     * 以保证内存中的session不会过多，影响tomcat JVM的性能。
     *
     * 后续要用到session的时候，再通过Swap in，从持久层加载到内存
     */
    public void processMaxIdleSwaps(){

    }
    public void processMaxActiveSwaps(){

    }

    public void processMaxIdleBackups(){

    }

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

    @Override
    public void start() throws LifecycleException {

    }

    @Override
    public void stop() throws LifecycleException {

    }
}
