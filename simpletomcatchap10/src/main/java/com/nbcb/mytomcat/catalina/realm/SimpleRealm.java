package com.nbcb.mytomcat.catalina.realm;

import org.apache.catalina.Container;
import org.apache.catalina.Realm;

import java.beans.PropertyChangeListener;
import java.security.Principal;
import java.security.cert.X509Certificate;

/**
 * ������������Ĵ��룬ʵ����Realm�ӿ�
 * ��Ҫʵ�ֵĽӿڶ���@Override��ǩ��ע������
 * �ص㷽��������
 * 1.authenticate()
 * 2.hasRole()
 */
public class SimpleRealm implements Realm {

    @Override
    public Container getContainer() {
        return null;
    }

    @Override
    public void setContainer(Container container) {

    }

    @Override
    public String getInfo() {
        return null;
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {

    }

    @Override
    public Principal authenticate(String username, String credentials) {
        return null;
    }

    @Override
    public Principal authenticate(String username, byte[] credentials) {
        return null;
    }

    @Override
    public Principal authenticate(String username, String digest, String nonce, String nc, String cnonce, String qop, String realm, String md5a2) {
        return null;
    }

    @Override
    public Principal authenticate(X509Certificate[] certs) {
        return null;
    }

    @Override
    public boolean hasRole(Principal principal, String role) {
        return false;
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {

    }
}
