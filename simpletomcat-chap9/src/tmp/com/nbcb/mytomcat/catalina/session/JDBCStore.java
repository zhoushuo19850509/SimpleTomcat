package com.nbcb.mytomcat.catalina.session;

import org.apache.catalina.Session;

import java.io.IOException;

public class JDBCStore extends StoreBase {

    @Override
    public void save(Session session) throws IOException {

    }

    /**
     * ������Щ���������ݿ��е�session�б��session id
     * @return
     * @throws IOException
     */
    @Override
    public String[] keys() throws IOException {
        return new String[0];
    }

    @Override
    public Session load(String id) throws ClassNotFoundException, IOException {
        return null;
    }

    /**
     * ����Щ���������ݿ��(����)sessionɾ��
     * @param id Session identifier of the Session to be removed
     *
     * @throws IOException
     */
    @Override
    public void remove(String id) throws IOException {

    }
}
