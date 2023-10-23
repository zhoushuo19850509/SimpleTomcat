package com.nbcb.mytomcat.catalina.startup;


import com.nbcb.mytomcat.catalina.authenticator.BasicAuthenticator;
import com.nbcb.mytomcat.catalina.core.StandardContext;
import com.nbcb.mytomcat.catalina.realm.SimpleRealm;
import org.apache.catalina.*;
import org.apache.catalina.deploy.LoginConfig;

/**
 * ContextConfig��Ҫ�����ڼ���Context�����config����
 * ���������ڱ��������۵�security��ص�����
 * (��authenticatorConfig()ʵ��security��ص����ü���)
 */
public class ContextConfig implements Lifecycle {


    private Context context;


    public ContextConfig(Context context) {
        System.out.println("ContextConfig constructed ...");
        this.context = context;
    }

    /**
     * ����security(��֤)��ص�������Ϣ
     * ��Ҫ�������£�
     * 1.����context��realm����ʵ����
     * 2.��ʼ�������Authenticatorʵ����(����BasicAuthenticator)
     * 3.��Authenticatorʵ������Ϊvalve��ӵ�context��
     */
    public void authenticatorConfig(){

        LoginConfig loginConfig = context.getLoginConfig();

        if(loginConfig.getAuthMethod().equals("BASIC")){
            Realm realm = new SimpleRealm();
            context.setRealm(realm);
        }

        if(loginConfig.getRealmName().equals("SimpleRealm")){
            /**
             * ��BasicAuthenticator��Ϊvalve���ݸ�StandardContext
             * ��������context��ִ��pipleline��ʱ�򣬾��ܹ�ִ�����authenticator��
             * ������ʱ��ô������������������ŵ�SimpleContextConfig��ȥ
             */
            Valve authenticator = new BasicAuthenticator();
            ((BasicAuthenticator) authenticator).setContainer(context);
            ((StandardContext) context).addValve(authenticator);
        }

    }


    /**
     * ������Щ��������ʵ����Lifecycle�ӿ�
     * ����ֻʹ��start()
     * ������tomcat������ʱ������ContextConfig��ʵ�����ü���
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
         * ContextConfig������ʱ��
         * ����authenticatorConfig()������֤��ص�����
         */
        authenticatorConfig();
    }

    @Override
    public void stop() throws LifecycleException {

    }
}
