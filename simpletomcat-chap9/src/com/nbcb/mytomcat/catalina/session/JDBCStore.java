package com.nbcb.mytomcat.catalina.session;

import org.apache.catalina.Session;

import java.io.IOException;

public class JDBCStore extends StoreBase {

    @Override
    public void save(Session session) throws IOException {

    }

    @Override
    public Session load(String id) throws ClassNotFoundException, IOException {
        return null;
    }
}
