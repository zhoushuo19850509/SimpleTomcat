package com.nbcb.mytomcat.catalina.session;

import org.apache.catalina.Manager;
import org.apache.catalina.Session;
import org.apache.catalina.Store;

import java.beans.PropertyChangeListener;
import java.io.IOException;

public class StoreBase implements Store ,Runnable{
    @Override
    public String getInfo() {
        return null;
    }

    @Override
    public Manager getManager() {
        return null;
    }

    @Override
    public void setManager(Manager manager) {

    }

    @Override
    public int getSize() throws IOException {
        return 0;
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {

    }

    @Override
    public String[] keys() throws IOException {
        return new String[0];
    }

    @Override
    public Session load(String id) throws ClassNotFoundException, IOException {
        return null;
    }

    @Override
    public void remove(String id) throws IOException {

    }

    @Override
    public void clear() throws IOException {

    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {

    }

    @Override
    public void save(Session session) throws IOException {

    }

    /**
     * StoreBase会启动一个单独的线程，检测过期的session
     */
    @Override
    public void run() {

    }

    /**
     * 处理过期的session
     */
    public void processExpire(){

    }
}
