package com.nbcb.mytomcat.catalina.cluster;

import org.apache.catalina.Container;
import org.apache.catalina.cluster.ClusterReceiver;
import org.apache.catalina.cluster.ClusterSender;

public interface Cluster {

    // ------------------------------------------------------------- Properties

    /**
     * Return descriptive information about this Cluster implementation and
     * the corresponding version number, in the format
     * <code>&lt;description&gt;/&lt;version&gt;</code>.
     */
    public String getInfo();

    /**
     * Return the name of the cluster that this Server is currently
     * configured to operate within.
     *
     * @return The name of the cluster associated with this server
     */
    public String getClusterName();

    /**
     * Set the time in seconds that the Cluster waits before
     * checking for changes and replicated data.
     *
     * @param checkInterval The time in seconds to sleep
     */
    public void setCheckInterval(int checkInterval);

    /**
     * Get the time in seconds that this Cluster sleeps.
     *
     * @return The value in seconds
     */
    public int getCheckInterval();

    /**
     * Set the name of the cluster to join, if no cluster with
     * this name is present create one.
     *
     * @param clusterName The clustername to join
     */
    public void setClusterName(String clusterName);

    /**
     * Set the Container associated with our Cluster
     *
     * @param container The Container to use
     */
    public void setContainer(Container container);

    /**
     * Get the Container associated with our Cluster
     *
     * @return The Container associated with our Cluster
     */
    public Container getContainer();

    /**
     * The debug detail level for this Cluster
     *
     * @param debug The debug level
     */
    public void setDebug(int debug);

    /**
     * Returns the debug level for this Cluster
     *
     * @return The debug level
     */
    public int getDebug();

    // --------------------------------------------------------- Public Methods

    /**
     * Returns a collection containing <code>ClusterMemberInfo</code>
     * on the remote members of this Cluster. This method does
     * not include the local host, to retrieve
     * <code>ClusterMemberInfo</code> on the local host
     * use <code>getLocalClusterInfo()</code> instead.
     *
     * @return Collection with all members in the Cluster
     */
    public ClusterMemberInfo[] getRemoteClusterMembers();

    /**
     * Returns a <code>ClusterSender</code> which is the interface
     * to use when sending information in the Cluster. senderId is
     * used as a identifier so that information sent through this
     * instance can only be used with the respectice
     * <code>ClusterReceiver</code>
     *
     * @return The ClusterSender
     */
    public ClusterSender getClusterSender(String senderId);

    /**
     * Returns a <code>ClusterReceiver</code> which is the interface
     * to use when receiving information in the Cluster. senderId is
     * used as a indentifier, only information send through the
     * <code>ClusterSender</code> with the same senderId can be received.
     *
     * @return The ClusterReceiver
     */
    public ClusterReceiver getClusterReceiver(String senderId);

    /**
     * Return cluster information about the local host
     *
     * @return Cluster information
     */
    public ClusterMemberInfo getLocalClusterMember();

}
