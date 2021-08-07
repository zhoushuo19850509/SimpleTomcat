package com.nbcb.mytomcat.catalina.cluster;

import java.io.Serializable;

public class ClusterMemberInfo implements Serializable {

    private String clusterName = null;

    private String hostName = null;

    private String clusterInfo = null;

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getClusterInfo() {
        return clusterInfo;
    }

    public void setClusterInfo(String clusterInfo) {
        this.clusterInfo = clusterInfo;
    }
}
