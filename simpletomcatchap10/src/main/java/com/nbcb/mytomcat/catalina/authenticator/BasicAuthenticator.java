package com.nbcb.mytomcat.catalina.authenticator;


import com.nbcb.mytomcat.catalina.connector.HttpRequestImpl;
import org.apache.catalina.*;
import org.apache.catalina.util.Base64;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import java.io.IOException;
import java.security.Principal;

/**
 * Authenticator implement1 ： BasicAuthenticator
 * 这个Authenticator的实现方案是，
 * 1.从http header中直接解析出"authorization"
 * 2.然后从"authorization"中解析出username/password
 * 3.然后调用realm，检验这个username/password是否正确
 * 4.如果username/password正确，就继续执行后面的valve，如果不正确，直接退出
 */
public class BasicAuthenticator implements Valve,
        Authenticator, Lifecycle, Contained {


    @Override
    public String getInfo() {
        return null;
    }

    /**
     * 实现Valve的接口方法
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
         * Authenticator如果校验不通过，就直接return退出，
         * 不会执行下一个valve(包括basic valve)
         * 至于为啥这里直接return不会执行后续的valve，请参考:
         * SimplePipeline.java的内部类：
         * SimplePipelineValveContext.invokeNext()
         * 或者直接参考我们的 "主题2-pipeline"
         */
        if(!result){
            System.out.println("BasicAuthenticator校验未通过，直接退出！");
            return;
        }
        System.out.println("BasicAuthenticator校验通过，继续执行servlet ... ");
        System.out.println("--------------------------------------");

        /**
         * Authenticator如果校验成功，才会执行下一个valve(包括basic valve)
         */
        context.invokeNext(request,response);
    }


    /**
     * parseUsername()/parsePassword()都是从tomcat4拷贝过来的
     * 用于从http header中解析authorization
     * 解析逻辑如下：
     *
     * http header逻辑:
     * key: "authorization"
     * value: "basic emhvdXNodW86MTIzNDU2"
     *
     * 其中valve要说明一下，emhvdXNodW86MTIzNDU2这段就是
     * 客户端的username/password("zhoushuo:123456")经过base64转化而来
     * 后续服务端会把这段base64 decode出来
     *
     * 参考：
     * tomcat4
     * 解析的方法参考： BasicAuthenticator.parseUsername()
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
     * 实现Authenticator的接口方法
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
         * 1.从request header中解析出authorization字段内容
         */
        String authorization = ((HttpRequestImpl)request).getHeader("authorization");
        System.out.println("authorization parsed from header: " + authorization);

        /**
         * 2.从 authorization 中解析出username/password
         */
        String username = parseUsername(authorization);
        String password = parsePassword(authorization);
        System.out.println("username: " + username);
        System.out.println("password: " + password);
        /**
         * 3.调用context中的realm.authenticate()方法
         * 返回当前访问用户的Principal信息
         */
        Realm realm = getContainer().getRealm();
        Principal principal = realm.authenticate(username, password);
        // 只要principal对象不为null，就说明校验通过
        if(null != principal){
            return true;
        }

        return false;
    }

    /**
     * 下面四个方法，实现了 Lifecycle 的接口方法
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
     * 设个当前valve的container(context)
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
