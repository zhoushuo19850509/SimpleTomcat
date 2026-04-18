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
 * 我们这次新增的代码，实现了Realm接口
 * 需要实现的接口都有@Override标签标注出来了
 * 重点方法包括：
 * 1.authenticate()
 * 2.hasRole()
 *
 * 这个SimpleRealm几乎是最简单的实现了
 * 在SimpleRealm初始化的时候加载固定的用户列表
 * 然后通过authenticate()判断当前登录用户是否是合法用户
 *
 */
public class SimpleRealm implements Realm {


    private List<User> users;

    /**
     * constructor
     * 创建用户列表
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
     * 这个authenticate()方法实现了Realm接口
     * 后续我们就用这个方法判断客户端传递的username/password是否正确
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
     * 根据username/password获取对应的用户信息(User对象)
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
