package com.nbcb.mytomcat.catalina.authenticator;


import com.nbcb.mytomcat.catalina.connector.HttpRequestImpl;
import org.apache.catalina.*;
import org.apache.catalina.util.Base64;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import java.io.IOException;
import java.security.Principal;

/**
 * Authenticator implement1 �� BasicAuthenticator
 * ���Authenticator��ʵ�ַ����ǣ�
 * 1.��http header��ֱ�ӽ�����"authorization"
 * 2.Ȼ���"authorization"�н�����username/password
 * 3.Ȼ�����realm���������username/password�Ƿ���ȷ
 * 4.���username/password��ȷ���ͼ���ִ�к����valve���������ȷ��ֱ���˳�
 */
public class BasicAuthenticator implements Valve,
        Authenticator, Lifecycle, Contained {


    @Override
    public String getInfo() {
        return null;
    }

    /**
     * ʵ��Valve�Ľӿڷ���
     * @param request The servlet request to be processed
     * @param response The servlet response to be created
     * @param context The valve context used to invoke the next valve
     *  in the current processing pipeline
     *
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void invoke(Request request, Response response, ValveContext context) throws IOException, ServletException {
        System.out.println("Valve BasicAuthenticator.invoke() ...");
        ServletRequest sreq = request.getRequest();

        boolean result = authenticate(request, response);

        /**
         * Authenticator���У�鲻ͨ������ֱ��return�˳���
         * ����ִ����һ��valve(����basic valve)
         * ����Ϊɶ����ֱ��return����ִ�к�����valve����ο�:
         * SimplePipeline.java���ڲ��ࣺ
         * SimplePipelineValveContext.invokeNext()
         * ����ֱ�Ӳο����ǵ� "����2-pipeline"
         */
        if(!result){
            System.out.println("BasicAuthenticatorУ��δͨ����ֱ���˳���");
            return;
        }
        System.out.println("BasicAuthenticatorУ��ͨ��������ִ��servlet ... ");
        System.out.println("--------------------------------------");

        /**
         * Authenticator���У��ɹ����Ż�ִ����һ��valve(����basic valve)
         */
        context.invokeNext(request,response);
    }


    /**
     * parseUsername()/parsePassword()���Ǵ�tomcat4����������
     * ���ڴ�http header�н���authorization
     * �����߼����£�
     *
     * http header�߼�:
     * key: "authorization"
     * value: "basic emhvdXNodW86MTIzNDU2"
     *
     * ����valveҪ˵��һ�£�emhvdXNodW86MTIzNDU2��ξ���
     * �ͻ��˵�username/password("zhoushuo:123456")����base64ת������
     * ��������˻�����base64 decode����
     *
     * �ο���
     * tomcat4
     * �����ķ����ο��� BasicAuthenticator.parseUsername()
     *
     */
    protected String parseUsername(String authorization) {

        authorization = authorization.substring(6).trim();

        // Decode and parse the authorization credentials
        String unencoded =
                new String(Base64.decode(authorization.getBytes()));
        int colon = unencoded.indexOf(':');
        if (colon < 0)
            return (null);
        String username = unencoded.substring(0, colon);
        return (username);

    }

    protected String parsePassword(String authorization) {

        authorization = authorization.substring(6).trim();

        // Decode and parse the authorization credentials
        String unencoded =
                new String(Base64.decode(authorization.getBytes()));
        int colon = unencoded.indexOf(':');
        if (colon < 0)
            return (null);
        String password = unencoded.substring(colon + 1);
        return (password);
    }

    /**
     * ʵ��Authenticator�Ľӿڷ���
     * @param request Request we are processing
     * @param response Response we are populating
     *
     * @return
     * @throws IOException
     */
    @Override
    public boolean authenticate(Request request, Response response) throws IOException {

        System.out.println("BasicAuthenticator start authenticate ... ");

        /**
         * 1.��request header�н�����authorization�ֶ�����
         */
        String authorization = ((HttpRequestImpl)request).getHeader("authorization");
        System.out.println("authorization parsed from header: " + authorization);

        /**
         * 2.�� authorization �н�����username/password
         */
        String username = parseUsername(authorization);
        String password = parsePassword(authorization);
        System.out.println("username: " + username);
        System.out.println("password: " + password);
        /**
         * 3.����context�е�realm.authenticate()����
         * ���ص�ǰ�����û���Principal��Ϣ
         */
        Realm realm = getContainer().getRealm();
        Principal principal = realm.authenticate(username, password);
        // ֻҪprincipal����Ϊnull����˵��У��ͨ��
        if(null != principal){
            return true;
        }

        return false;
    }

    /**
     * �����ĸ�������ʵ���� Lifecycle �Ľӿڷ���
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

    }

    @Override
    public void stop() throws LifecycleException {

    }


    private Context context;

    /**
     * �����ǰvalve��container(context)
     * @return
     */
    @Override
    public Container getContainer() {
        return this.context;
    }

    @Override
    public void setContainer(Container container) {
        this.context = (Context) container;
    }
}
