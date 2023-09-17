package com.nbcb.mytomcat.catalina.startup;

import com.nbcb.mytomcat.catalina.cluster.StandardCluster;
import com.nbcb.mytomcat.catalina.connector.HttpConnector;
import com.nbcb.mytomcat.catalina.core.*;
import com.nbcb.mytomcat.catalina.loader.WebappLoader;
import com.nbcb.mytomcat.catalina.realm.SimpleRealm;
import com.nbcb.mytomcat.catalina.realm.SimpleUserDatabaseRealm;
import com.nbcb.mytomcat.catalina.session.DistributedManager;
import com.nbcb.mytomcat.catalina.session.FileStore;
import com.nbcb.mytomcat.catalina.util.Constants;
import org.apache.catalina.*;
import org.apache.catalina.deploy.LoginConfig;
import org.apache.catalina.deploy.SecurityCollection;
import org.apache.catalina.deploy.SecurityConstraint;

import java.io.IOException;

/**
 * 这个BootStrap主要为了验证我们的Lifecyle功能
 */
public class BootStrap {

    public static void main(String[] args){
        /**
         * 调用Chap4的HttpConnector启动ServerSocket，处理来自客户端的请求
         */
        HttpConnector connector = new HttpConnector();

        /**
         * 定义各个Wrapper，这些Wrapper都挂靠在Context下
         * 后续，要把这些servlet统一配置到web.xml中去，而不是在这里硬编码
         */
        Wrapper wrapper1 = new SimpleWrapper();
        Wrapper wrapper2 = new SimpleWrapper();
        Wrapper wrapper3 = new SimpleWrapper();
        Wrapper wrapper4 = new SimpleWrapper();

        wrapper1.setName("Primitive");
        wrapper1.setServletClass("PrimitiveServlet");
        wrapper2.setName("Modern");
        wrapper2.setServletClass("ModernServlet");
        wrapper3.setName("MySession");
        wrapper3.setServletClass("MySessionServlet");
        wrapper4.setName("MySessionPlain");
        wrapper4.setServletClass("MySessionPlainServlet");

        LifecycleListener wrapperListener = new SimpleWrapperLifecycleListener();
        ((SimpleWrapper) wrapper1).addLifecycleListener(wrapperListener);
        ((SimpleWrapper) wrapper2).addLifecycleListener(wrapperListener);
        ((SimpleWrapper) wrapper3).addLifecycleListener(wrapperListener);
        ((SimpleWrapper) wrapper4).addLifecycleListener(wrapperListener);

        /**
         * Context换成了我们这章新的Context: StandardContext
         */
        Context context = new StandardContext();
        context.addChild(wrapper1);
        context.addChild(wrapper2);
        context.addChild(wrapper3);
        context.addChild(wrapper4);

        /**
         * 调用我们自己定义的WebappLoader类，来加载本地的Servlet类
         * 然后设置给SimpleContext
         */
        Loader loader = new WebappLoader();
        context.setLoader(loader);
        loader.setContainer(context);

        Mapper mapper = new SimpleContextMapper();
        mapper.setProtocol("http");
        context.addMapper(mapper);

        /**
         * 这里要设置一个映射关系
         * 将客户端URL中Servlet相关的地址("/Modern")和Wrapper名称("Modern")映射起来
         */
        context.addServletMapping("/ModernServlet","Modern");
        context.addServletMapping("/PrimitiveServlet","Primitive");
        context.addServletMapping("/MySessionServlet","MySession");
        context.addServletMapping("/MySessionPlainServlet","MySessionPlain");


        /**
         * 定义一个Listener，专门是用于监听SimpleContext的
         */
        LifecycleListener listener = new SimpleContextLifecycleListener();
        ((StandardContext) context).addLifecycleListener(listener);
        /**
         * 这个方法很关键，就是把context设置给connector
         * 后续connector解析完客户端http请求之后，就会调用context.invoke()方法
         * 具体请参考chap4/HttpProcessor.process(): container.invoke(request,response);
         */
        connector.setContainer(context);


        /**
         * 下面开始设置日志对象： FileLogger
         */
        // 首先，设置一下系统参数："catalina.base"
        // FileLogger对象会用到这个值，后续会把日志放到这个路径下
        // 也就是本工程的地址： /Users/zhoushuo/Documents/workspace/TomcatWin
        System.setProperty("catalina.base",System.getProperty("user.dir"));
//        FileLogger logger = new FileLogger();
//
//        // 设置日志文件名称
//        logger.setPrefix("MyFileLog_");
//        logger.setSuffix(".log");
//
//        // 日志文件中是否打印时间戳
//        logger.setTimestamp(true);
//
//
//        // 日志打印在哪个具体的目录下
//        logger.setDirectory("webroot");
//        context.setLogger(logger);
//
//        logger.log("hello FileLogger");

        /**
         * 设置context的manager
         * 用于后续servlet类通过context获取manager类，继而访问session对象
         */
        /**
         * Manager1 StandardManager
         */
//        Manager manager = new StandardManager();

        /**
         * Manager2 PersistentManager
         */
//        PersistentManager manager = new PersistentManager();
//        manager.setStore(new FileStore());

        /**
         * Manager3 DistributedManager
         */
        DistributedManager manager = new DistributedManager();
        manager.setStore(new FileStore());
        context.setManager(manager);

        /**
         * 如果是选择DistributedManager，那么就要指定集群
         */
        StandardCluster cluster = new StandardCluster();
        cluster.setMulticastAddress(Constants.MULTICAST_ADDRESS);
        cluster.setMulticastPort(Constants.MULTICAST_PORT);
        cluster.setClusterName(Constants.ClUSTER_NAME);
        context.setCluster(cluster);

        /**
         * 下面是Security相关的代码
         */
        // Security component1  : SecurityCollection
        SecurityCollection securityCollection = new SecurityCollection();
        securityCollection.addMethod("GET");
        securityCollection.addPattern("/");

        // Security component2  : SecurityConstraint
        SecurityConstraint constraint = new SecurityConstraint();
        constraint.addCollection(securityCollection);
        constraint.addAuthRole("manager");

        // Security component3  : LoginConfig
        LoginConfig loginConfig = new LoginConfig();
        loginConfig.setRealmName("Simple Realm");
//        loginConfig.setRealmName("Simple User Database Realm");

        // Security component4  : Realm
        // Realm implement1: SimpleRealm
        Realm realm = new SimpleRealm();

        // Realm implement2: SimpleUserDatabaseRealm
//        Realm realm = new SimpleUserDatabaseRealm();


        context.setRealm(realm);
        context.addConstraint(constraint);
        context.setLoginConfig(loginConfig);





        try {
            connector.initialize();
            connector.start();


            /**
             * 因为SimpleContext实现了Lifecycle接口
             * 所以可以直接调用start()接口
             */
            ((Lifecycle)context).start();
            /**
             * wait until we press any key
             */
            System.in.read();

            ((Lifecycle)context).stop();

        } catch (LifecycleException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
