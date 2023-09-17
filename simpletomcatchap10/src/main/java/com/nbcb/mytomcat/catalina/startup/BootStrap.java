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
 * ���BootStrap��ҪΪ����֤���ǵ�Lifecyle����
 */
public class BootStrap {

    public static void main(String[] args){
        /**
         * ����Chap4��HttpConnector����ServerSocket���������Կͻ��˵�����
         */
        HttpConnector connector = new HttpConnector();

        /**
         * �������Wrapper����ЩWrapper���ҿ���Context��
         * ������Ҫ����Щservletͳһ���õ�web.xml��ȥ��������������Ӳ����
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
         * Context���������������µ�Context: StandardContext
         */
        Context context = new StandardContext();
        context.addChild(wrapper1);
        context.addChild(wrapper2);
        context.addChild(wrapper3);
        context.addChild(wrapper4);

        /**
         * ���������Լ������WebappLoader�࣬�����ر��ص�Servlet��
         * Ȼ�����ø�SimpleContext
         */
        Loader loader = new WebappLoader();
        context.setLoader(loader);
        loader.setContainer(context);

        Mapper mapper = new SimpleContextMapper();
        mapper.setProtocol("http");
        context.addMapper(mapper);

        /**
         * ����Ҫ����һ��ӳ���ϵ
         * ���ͻ���URL��Servlet��صĵ�ַ("/Modern")��Wrapper����("Modern")ӳ������
         */
        context.addServletMapping("/ModernServlet","Modern");
        context.addServletMapping("/PrimitiveServlet","Primitive");
        context.addServletMapping("/MySessionServlet","MySession");
        context.addServletMapping("/MySessionPlainServlet","MySessionPlain");


        /**
         * ����һ��Listener��ר�������ڼ���SimpleContext��
         */
        LifecycleListener listener = new SimpleContextLifecycleListener();
        ((StandardContext) context).addLifecycleListener(listener);
        /**
         * ��������ܹؼ������ǰ�context���ø�connector
         * ����connector������ͻ���http����֮�󣬾ͻ����context.invoke()����
         * ������ο�chap4/HttpProcessor.process(): container.invoke(request,response);
         */
        connector.setContainer(context);


        /**
         * ���濪ʼ������־���� FileLogger
         */
        // ���ȣ�����һ��ϵͳ������"catalina.base"
        // FileLogger������õ����ֵ�����������־�ŵ����·����
        // Ҳ���Ǳ����̵ĵ�ַ�� /Users/zhoushuo/Documents/workspace/TomcatWin
        System.setProperty("catalina.base",System.getProperty("user.dir"));
//        FileLogger logger = new FileLogger();
//
//        // ������־�ļ�����
//        logger.setPrefix("MyFileLog_");
//        logger.setSuffix(".log");
//
//        // ��־�ļ����Ƿ��ӡʱ���
//        logger.setTimestamp(true);
//
//
//        // ��־��ӡ���ĸ������Ŀ¼��
//        logger.setDirectory("webroot");
//        context.setLogger(logger);
//
//        logger.log("hello FileLogger");

        /**
         * ����context��manager
         * ���ں���servlet��ͨ��context��ȡmanager�࣬�̶�����session����
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
         * �����ѡ��DistributedManager����ô��Ҫָ����Ⱥ
         */
        StandardCluster cluster = new StandardCluster();
        cluster.setMulticastAddress(Constants.MULTICAST_ADDRESS);
        cluster.setMulticastPort(Constants.MULTICAST_PORT);
        cluster.setClusterName(Constants.ClUSTER_NAME);
        context.setCluster(cluster);

        /**
         * ������Security��صĴ���
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
             * ��ΪSimpleContextʵ����Lifecycle�ӿ�
             * ���Կ���ֱ�ӵ���start()�ӿ�
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
