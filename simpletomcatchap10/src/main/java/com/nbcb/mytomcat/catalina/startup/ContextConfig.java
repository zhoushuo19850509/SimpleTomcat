package com.nbcb.mytomcat.catalina.startup;


import com.nbcb.mytomcat.catalina.authenticator.BasicAuthenticator;
import com.nbcb.mytomcat.catalina.core.StandardContext;
import com.nbcb.mytomcat.catalina.realm.SimpleRealm;
import org.apache.catalina.*;
import org.apache.catalina.deploy.LoginConfig;

/**
 * ContextConfig主要是用于加载Context层面的config配置
 * 比如我们在本章中讨论的security相关的配置
 * (由authenticatorConfig()实现security相关的配置加载)
 */
public class ContextConfig implements Lifecycle {


    private Context context;


    public ContextConfig(Context context) {
        System.out.println("ContextConfig constructed ...");
        this.context = context;
    }

    /**
     * 加载security(认证)相关的配置信息
     * 主要做两件事：
     * 1.设置context的realm具体实现类
     * 2.初始化具体的Authenticator实现类(比如BasicAuthenticator)
     * 3.把Authenticator实现类作为valve添加到context中
     */
    public void authenticatorConfig(){

        LoginConfig loginConfig = context.getLoginConfig();

        if(loginConfig.getAuthMethod().equals("BASIC")){
            Realm realm = new SimpleRealm();
            context.setRealm(realm);
        }

        if(loginConfig.getRealmName().equals("SimpleRealm")){
            /**
             * 将BasicAuthenticator作为valve传递给StandardContext
             * 这样后续context在执行pipleline的时候，就能够执行这个authenticator了
             * 这里暂时这么做，后续把这个动作放到SimpleContextConfig中去
             */
            Valve authenticator = new BasicAuthenticator();
            ((BasicAuthenticator) authenticator).setContainer(context);
            ((StandardContext) context).addValve(authenticator);
        }

    }


    /**
     * 下面这些方法都是实现了Lifecycle接口
     * 我们只使用start()
     * 用于在tomcat启动的时候，驱动ContextConfig，实现配置加载
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

    @Override
    public void start() throws LifecycleException {

        /**
         * ContextConfig启动的时候，
         * 调用authenticatorConfig()处理认证相关的内容
         */
        authenticatorConfig();
    }

    @Override
    public void stop() throws LifecycleException {

    }
}
