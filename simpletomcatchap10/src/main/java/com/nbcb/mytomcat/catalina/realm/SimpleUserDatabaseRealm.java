package com.nbcb.mytomcat.catalina.realm;

import org.apache.catalina.realm.RealmBase;

import java.security.Principal;


public class SimpleUserDatabaseRealm extends RealmBase {

    @Override
    protected String getName() {
        return null;
    }

    @Override
    protected String getPassword(String username) {
        return null;
    }

    @Override
    protected Principal getPrincipal(String username) {
        return null;
    }
}
