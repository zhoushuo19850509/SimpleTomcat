package com.nbcb.mytomcat.catalina.startup;

import com.nbcb.mytomcat.catalina.authenticator.BasicAuthenticator;
import com.nbcb.mytomcat.catalina.connector.HttpConnector;
import com.nbcb.mytomcat.catalina.core.*;
import com.nbcb.mytomcat.catalina.loader.WebappLoader;
import org.apache.catalina.*;
import org.apache.catalina.deploy.LoginConfig;

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

        /**
         * 下面是Security相关的代码
         */
        LoginConfig loginConfig = new LoginConfig();

        /**
         * 这个就是我们在security中讨论的authenticator接口的实现类
         * 我们自己实现了BasicAuthenticator,那么在初始化的时候，就把authMethod设置为BASIC
         * 当然还有其他实现类。具体如下：
         *
         * The authentication method to use for application login.  Must be
         * BASIC, DIGEST, FORM, or CLIENT-CERT.
         */
        String authMethod = "BASIC";

        /**
         * 这个就是我们在security中讨论的Realm接口的实现类
         * 我们自己实现了 SimpleRealm,那么在初始化的时候，就把realName设置为SimpleRealm
         * 当然还有其他实现类比如：
         * DataSourceRealm
         * MemoryRealm
         * 等等
         */
        String realmName = "SimpleRealm";

        loginConfig.setRealmName(realmName);
        loginConfig.setAuthMethod(authMethod);

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
