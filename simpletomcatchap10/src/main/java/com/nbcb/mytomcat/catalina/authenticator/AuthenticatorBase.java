package com.nbcb.mytomcat.catalina.authenticator;

import org.apache.catalina.*;
import org.apache.catalina.valves.ValveBase;

import javax.servlet.ServletException;
import java.io.IOException;

public class AuthenticatorBase
        extends ValveBase
        implements Authenticator, Lifecycle {


    // ���������Ҫ��ʵ��Valve�ӿ�
    @Override
    public void invoke(Request request, Response response, ValveContext context) throws IOException, ServletException {

    }


    // �������������Ҫ��ʵ��Lifecycle�ӿ�
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
