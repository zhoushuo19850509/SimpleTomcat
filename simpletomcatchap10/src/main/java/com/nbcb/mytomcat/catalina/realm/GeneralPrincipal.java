package com.nbcb.mytomcat.catalina.realm;

import org.apache.catalina.Realm;

import javax.security.auth.Subject;
import java.security.Principal;
import java.util.List;

public class GeneralPrincipal implements Principal {


    private Realm realm;
    private String username;
    private String password;
    private List<String> roles;


    public GeneralPrincipal(Realm realm, String username, String password, List<String> roles) {
        this.realm = realm;
        this.username = username;
        this.password = password;
        this.roles = roles;
    }

    public boolean hasRole(String role){
        return this.roles.contains(role);
    }


    /**
     * ������������ʵ��Principal�ӿڣ�ûɶ��
     * @return
     */
    @Override
    public String getName() {
        return null;
    }

    @Override
    public boolean implies(Subject subject) {
        return false;
    }
}
