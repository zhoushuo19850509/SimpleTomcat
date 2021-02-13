package com.nbcb.mytomcat.catalina.session;

import org.apache.catalina.Session;

public class DistributedManager extends PersistentManagerBase {

    /**
     * 创建session对象之后，将session信息同步给整个tomcat集群各个节点上
     * @return
     */
    public Session createSession(){
        return null;
    }
}
