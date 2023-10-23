package com.nbcb.mytomcat.catalina.realm;

import com.nbcb.mytomcat.catalina.model.User;
import org.apache.catalina.Container;
import org.apache.catalina.Realm;

import java.beans.PropertyChangeListener;
import java.security.Principal;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

/**
 * ������������Ĵ��룬ʵ����Realm�ӿ�
 * ��Ҫʵ�ֵĽӿڶ���@Override��ǩ��ע������
 * �ص㷽��������
 * 1.authenticate()
 * 2.hasRole()
 *
 * ���SimpleRealm��������򵥵�ʵ����
 * ��SimpleRealm��ʼ����ʱ����ع̶����û��б�
 * Ȼ��ͨ��authenticate()�жϵ�ǰ��¼�û��Ƿ��ǺϷ��û�
 *
 */
public class SimpleRealm implements Realm {


    private List<User> users;

    /**
     * constructor
     * �����û��б�
     */
    public SimpleRealm() {

        User user1 = new User("fdq","123456");
        user1.addRole("manager");
        user1.addRole("programmer");

        User user2 = new User("zhoushuo","nbcb");
        user2.addRole("programmer");

        users = new ArrayList<>();
        users.add(user1);
        users.add(user2);

    }

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

    /**
     * ���authenticate()����ʵ����Realm�ӿ�
     * �������Ǿ�����������жϿͻ��˴��ݵ�username/password�Ƿ���ȷ
     *
     * @param username Username of the Principal to look up
     * @param credentials Password or other credentials to use in
     * @return
     */
    @Override
    public Principal authenticate(String username, String credentials) {
        User user = getUser(username, credentials);
        if(null != user){
            return new GeneralPrincipal(this, username, credentials, user.getRoles());
        }
        return null;
    }

    /**
     * ����username/password��ȡ��Ӧ���û���Ϣ(User����)
     * @param username
     * @param password
     * @return
     */
    private User getUser(String username, String password){
        for(User user : users){
            if(user.getName().equals(username) && user.getPassword().equals(password)){
                return user;
            }
        }
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
