package com.nbcb.mytomcat.catalina.model;

import java.util.ArrayList;
import java.util.List;

/**
 * User java bean保存的是登录用户的信息，包括username/password/roles
 * 用于security authentication
 */
public class User {
    private String name;
    private String password;
    List<String> roles;

    public User(String name, String password) {
        this.name = name;
        this.password = password;
        roles = new ArrayList<>();
    }

    public void addRole(String role){
        roles.add(role);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
