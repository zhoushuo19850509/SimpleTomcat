package com.nbcb.mytomcat.catalina.core;

import com.nbcb.mytomcat.catalina.session.PersistentManager;
import com.nbcb.mytomcat.catalina.startup.ContextConfig;
import org.apache.catalina.*;
import org.apache.catalina.deploy.*;
import org.apache.catalina.util.CharsetMapper;
import org.apache.catalina.util.LifecycleSupport;

import javax.naming.directory.DirContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * StandardContext类似于Application
 * 包含一个或者多个Wrapper(Servlet)
 * StandardContext和之前的SimpleContext不同之处在于，加入了reload功能
 * 能够重启整个Application，从而使得最新的servlet生效
 */
public class StandardContext implements Context, Pipeline, Lifecycle {

    /**
     * the pipeline of the context
     */
    private SimplePipeline pipeline = new SimplePipeline(this);

    /**
     * The (Servlet) loader of this Wrapper
     */
    private Loader loader;

    private Mapper mapper = null;

    /**
     * 映射关系：
     * client's serlvet url(/Modern)和Wrapper名字的映射关系
     */
    Map<String,String> servletMap = new HashMap<String,String >();

    /**
     * save the child of this context
     * 对于SimpleContext来说，child就是挂载在这个context下的Wrapper(s)
     */
    Map<String, Container> childs = new HashMap<String , Container>();

    protected LifecycleSupport lifecycle = new LifecycleSupport(this);

    /**
     * SimpleContext的FileLogger对象
     */
    private Logger logger = null;

    /**
     * session manager
     */
    protected Manager manager;

    protected Cluster cluster;

    /**
     * Security相关属性
     */
    protected Realm realm = null;

    private LoginConfig loginConfig = null;



    /**
     * constructor
     */
    public StandardContext(){
        pipeline.setBasic(new SimpleContextValve());
    }

    @Override
    public Loader getLoader() {
        if(this.loader != null){
            return loader;
        }
        return null;
    }

    @Override
    public void invoke(Request request, Response response) throws IOException, ServletException {
        pipeline.invoke(request,response);
    }

    @Override
    public void setLoader(Loader loader) {
        this.loader = loader;
    }

    @Override
    public void addValve(Valve valve) {
        pipeline.addValve(valve);
    }

    /**
     * 添加映射关系
     * client's serlvet url(/Modern)和Wrapper名字的映射关系
     * @param pattern URL pattern to be mapped
     * @param name Name of the corresponding servlet to execute
     */
    @Override
    public void addServletMapping(String pattern, String name) {
        servletMap.put(pattern,name);
    }

    /**
     * 根据client's serlvet url(/Modern)，获取Wrapper名字(Modern)
     * @param pattern Pattern for which a mapping is requested
     * @return
     */
    @Override
    public String findServletMapping(String pattern) {
        return servletMap.get(pattern);
    }

    @Override
    public void addChild(Container child) {
        child.setParent((Container) this);
        childs.put(child.getName(),child);
    }

    @Override
    public Container findChild(String name) {
        return childs.get(name);
    }

    @Override
    public Valve getBasic() {
        return null;
    }

    @Override
    public void setBasic(Valve valve) {

    }



    @Override
    public Valve[] getValves() {
        return new Valve[0];
    }


    @Override
    public void removeValve(Valve valve) {

    }

    @Override
    public Object[] getApplicationListeners() {
        return new Object[0];
    }

    @Override
    public void setApplicationListeners(Object[] listeners) {

    }

    @Override
    public boolean getAvailable() {
        return false;
    }

    @Override
    public void setAvailable(boolean available) {

    }

    @Override
    public CharsetMapper getCharsetMapper() {
        return null;
    }

    @Override
    public void setCharsetMapper(CharsetMapper mapper) {

    }

    @Override
    public boolean getConfigured() {
        return false;
    }

    @Override
    public void setConfigured(boolean configured) {

    }


    @Override
    public boolean getCookies() {
        return true;
    }

    @Override
    public void setCookies(boolean cookies) {

    }

    @Override
    public boolean getCrossContext() {
        return false;
    }

    @Override
    public void setCrossContext(boolean crossContext) {

    }

    @Override
    public String getDisplayName() {
        return null;
    }

    @Override
    public void setDisplayName(String displayName) {

    }

    @Override
    public boolean getDistributable() {
        return false;
    }

    @Override
    public void setDistributable(boolean distributable) {

    }

    @Override
    public String getDocBase() {
        return null;
    }

    @Override
    public void setDocBase(String docBase) {

    }

    @Override
    public LoginConfig getLoginConfig() {
        return this.loginConfig;
    }

    @Override
    public void setLoginConfig(LoginConfig config) {
        this.loginConfig = config;

    }

    @Override
    public NamingResources getNamingResources() {
        return null;
    }

    @Override
    public void setNamingResources(NamingResources namingResources) {

    }

    @Override
    public String getPath() {
        return null;
    }

    @Override
    public void setPath(String path) {

    }

    @Override
    public String getPublicId() {
        return null;
    }

    @Override
    public void setPublicId(String publicId) {

    }

    @Override
    public boolean getReloadable() {
        return false;
    }

    @Override
    public void setReloadable(boolean reloadable) {

    }

    @Override
    public boolean getOverride() {
        return false;
    }

    @Override
    public void setOverride(boolean override) {

    }

    @Override
    public boolean getPrivileged() {
        return false;
    }

    @Override
    public void setPrivileged(boolean privileged) {

    }

    @Override
    public ServletContext getServletContext() {
        return null;
    }

    @Override
    public int getSessionTimeout() {
        return 0;
    }

    @Override
    public void setSessionTimeout(int timeout) {

    }

    @Override
    public String getWrapperClass() {
        return null;
    }

    @Override
    public void setWrapperClass(String wrapperClass) {

    }

    @Override
    public void addApplicationListener(String listener) {

    }

    @Override
    public void addApplicationParameter(ApplicationParameter parameter) {

    }

    @Override
    public void addConstraint(SecurityConstraint constraint) {

    }

    @Override
    public void addEjb(ContextEjb ejb) {

    }

    @Override
    public void addEnvironment(ContextEnvironment environment) {

    }

    @Override
    public void addErrorPage(ErrorPage errorPage) {

    }

    @Override
    public void addFilterDef(FilterDef filterDef) {

    }

    @Override
    public void addFilterMap(FilterMap filterMap) {

    }

    @Override
    public void addInstanceListener(String listener) {

    }

    @Override
    public void addLocalEjb(ContextLocalEjb ejb) {

    }

    @Override
    public void addMimeMapping(String extension, String mimeType) {

    }

    @Override
    public void addParameter(String name, String value) {

    }

    @Override
    public void addResource(ContextResource resource) {

    }

    @Override
    public void addResourceEnvRef(String name, String type) {

    }

    @Override
    public void addResourceLink(ContextResourceLink resourceLink) {

    }

    @Override
    public void addRoleMapping(String role, String link) {

    }

    @Override
    public void addSecurityRole(String role) {

    }


    @Override
    public void addTaglib(String uri, String location) {

    }

    @Override
    public void addWelcomeFile(String name) {

    }

    @Override
    public void addWrapperLifecycle(String listener) {

    }

    @Override
    public void addWrapperListener(String listener) {

    }

    @Override
    public Wrapper createWrapper() {
        return null;
    }

    @Override
    public String[] findApplicationListeners() {
        return new String[0];
    }

    @Override
    public ApplicationParameter[] findApplicationParameters() {
        return new ApplicationParameter[0];
    }

    @Override
    public SecurityConstraint[] findConstraints() {
        return new SecurityConstraint[0];
    }

    @Override
    public ContextEjb findEjb(String name) {
        return null;
    }

    @Override
    public ContextEjb[] findEjbs() {
        return new ContextEjb[0];
    }

    @Override
    public ContextEnvironment findEnvironment(String name) {
        return null;
    }

    @Override
    public ContextEnvironment[] findEnvironments() {
        return new ContextEnvironment[0];
    }

    @Override
    public ErrorPage findErrorPage(int errorCode) {
        return null;
    }

    @Override
    public ErrorPage findErrorPage(String exceptionType) {
        return null;
    }

    @Override
    public ErrorPage[] findErrorPages() {
        return new ErrorPage[0];
    }

    @Override
    public FilterDef findFilterDef(String filterName) {
        return null;
    }

    @Override
    public FilterDef[] findFilterDefs() {
        return new FilterDef[0];
    }

    @Override
    public FilterMap[] findFilterMaps() {
        return new FilterMap[0];
    }

    @Override
    public String[] findInstanceListeners() {
        return new String[0];
    }

    @Override
    public ContextLocalEjb findLocalEjb(String name) {
        return null;
    }

    @Override
    public ContextLocalEjb[] findLocalEjbs() {
        return new ContextLocalEjb[0];
    }

    @Override
    public String findMimeMapping(String extension) {
        return null;
    }

    @Override
    public String[] findMimeMappings() {
        return new String[0];
    }

    @Override
    public String findParameter(String name) {
        return null;
    }

    @Override
    public String[] findParameters() {
        return new String[0];
    }

    @Override
    public ContextResource findResource(String name) {
        return null;
    }

    @Override
    public String findResourceEnvRef(String name) {
        return null;
    }

    @Override
    public String[] findResourceEnvRefs() {
        return new String[0];
    }

    @Override
    public ContextResourceLink findResourceLink(String name) {
        return null;
    }

    @Override
    public ContextResourceLink[] findResourceLinks() {
        return new ContextResourceLink[0];
    }

    @Override
    public ContextResource[] findResources() {
        return new ContextResource[0];
    }

    @Override
    public String findRoleMapping(String role) {
        return null;
    }

    @Override
    public boolean findSecurityRole(String role) {
        return false;
    }

    @Override
    public String[] findSecurityRoles() {
        return new String[0];
    }

    @Override
    public String[] findServletMappings() {
        return new String[0];
    }

    @Override
    public String findStatusPage(int status) {
        return null;
    }

    @Override
    public int[] findStatusPages() {
        return new int[0];
    }

    @Override
    public String findTaglib(String uri) {
        return null;
    }

    @Override
    public String[] findTaglibs() {
        return new String[0];
    }

    @Override
    public boolean findWelcomeFile(String name) {
        return false;
    }

    @Override
    public String[] findWelcomeFiles() {
        return new String[0];
    }

    @Override
    public String[] findWrapperLifecycles() {
        return new String[0];
    }

    @Override
    public String[] findWrapperListeners() {
        return new String[0];
    }

    /**
     * 能够重启整个Application，从而使得最新的servlet生效
     */
    @Override
    public synchronized void reload() {


        /**
         * Step1 停止Wrappers
         * 停止当前context下所有的Wrapper，其实就是停止当前所有的servlet服务
         */
        for(String name: childs.keySet()){
            SimpleWrapper child = (SimpleWrapper)childs.get(name);
            if(child instanceof Lifecycle){
                try {
                    ((Lifecycle)child).stop();
                } catch (LifecycleException e) {
                    e.printStackTrace();
                }
            }
        }


        /**
         * Step2 停止Loader
         * 停止Context对应的loader
         */

        if(loader != null && loader instanceof Lifecycle){
            try {
                ((Lifecycle)loader).stop();
            } catch (LifecycleException e) {
                e.printStackTrace();
            }
        }

        /**
         * Step3 启动Loader
         */
        if(loader != null && loader instanceof Lifecycle){
            try {
                ((Lifecycle)loader).start();
            } catch (LifecycleException e) {
                e.printStackTrace();
            }
        }

        /**
         * Step4 启动Wrappers
         */
        for(String name: childs.keySet()){
            SimpleWrapper child = (SimpleWrapper)childs.get(name);
            if(child instanceof Lifecycle){
                try {
                    ((Lifecycle)child).start();
                } catch (LifecycleException e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * Step5 触发reload事件
         */
    }

    @Override
    public void removeApplicationListener(String listener) {

    }

    @Override
    public void removeApplicationParameter(String name) {

    }

    @Override
    public void removeConstraint(SecurityConstraint constraint) {

    }

    @Override
    public void removeEjb(String name) {

    }

    @Override
    public void removeEnvironment(String name) {

    }

    @Override
    public void removeErrorPage(ErrorPage errorPage) {

    }

    @Override
    public void removeFilterDef(FilterDef filterDef) {

    }

    @Override
    public void removeFilterMap(FilterMap filterMap) {

    }

    @Override
    public void removeInstanceListener(String listener) {

    }

    @Override
    public void removeLocalEjb(String name) {

    }

    @Override
    public void removeMimeMapping(String extension) {

    }

    @Override
    public void removeParameter(String name) {

    }

    @Override
    public void removeResource(String name) {

    }

    @Override
    public void removeResourceEnvRef(String name) {

    }

    @Override
    public void removeResourceLink(String name) {

    }

    @Override
    public void removeRoleMapping(String role) {

    }

    @Override
    public void removeSecurityRole(String role) {

    }

    @Override
    public void removeServletMapping(String pattern) {

    }

    @Override
    public void removeTaglib(String uri) {

    }

    @Override
    public void removeWelcomeFile(String name) {

    }

    @Override
    public void removeWrapperLifecycle(String listener) {

    }

    @Override
    public void removeWrapperListener(String listener) {

    }

    @Override
    public String getInfo() {
        return null;
    }

    /**
     * getLogger()/setLogger()方法，是实现了Container接口中的方法
     * @return
     */
    @Override
    public Logger getLogger() {
        return this.logger;
    }


    @Override
    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    /**
     * Context对象打印日志的实际方法
     * @param message
     */
    private void log(String message){
        Logger logger = this.getLogger();
        if(null != logger){
            logger.log(message);
        }
    }


    /**
     * 获取session manager对象
     * @return
     */
    @Override
    public Manager getManager() {
        return manager;
    }

    /**
     * 设置当前context的session manager
     * @param manager The newly associated Manager
     */
    @Override
    public void setManager(Manager manager) {
        this.manager = manager;

        if(null != manager){
            manager.setContainer(this);
        }

    }


    @Override
    public Cluster getCluster() {
        return this.cluster;
    }

    @Override
    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void setName(String name) {

    }

    @Override
    public Container getParent() {
        return null;
    }

    @Override
    public void setParent(Container container) {

    }

    @Override
    public ClassLoader getParentClassLoader() {
        return null;
    }

    @Override
    public void setParentClassLoader(ClassLoader parent) {

    }


    @Override
    public Realm getRealm() {
        return this.realm;
    }

    @Override
    public void setRealm(Realm realm) {
        this.realm = realm;

    }

    @Override
    public DirContext getResources() {
        return null;
    }

    @Override
    public void setResources(DirContext resources) {

    }

    @Override
    public void addContainerListener(ContainerListener listener) {

    }

    @Override
    public void addMapper(Mapper mapper) {
        mapper.setContainer((Container) this);
        this.mapper = mapper;
        int i = 0 ;
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {

    }

    @Override
    public Container[] findChildren() {
        return new Container[0];
    }

    public Map<String, Container> findChildrenList() {
        return childs;
    }

    @Override
    public ContainerListener[] findContainerListeners() {
        return new ContainerListener[0];
    }

    @Override
    public Mapper findMapper(String protocol) {
        return null;
    }

    @Override
    public Mapper[] findMappers() {
        return new Mapper[0];
    }


    @Override
    public Container map(Request request, boolean update) {
        return mapper.map(request,update);
    }

    @Override
    public void removeChild(Container child) {

    }

    @Override
    public void removeContainerListener(ContainerListener listener) {

    }

    @Override
    public void removeMapper(Mapper mapper) {

    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {

    }

    /**
     * 下面5个方法，都是实现了Lifecycle接口
     * @param listener The listener to add
     */

    /**
     * 这个字段用于标识当前SimpleContext实例是否已经启动
     */
    private boolean started = false;

    @Override
    public void addLifecycleListener(LifecycleListener listener) {
        lifecycle.addLifecycleListener(listener);
    }

    @Override
    public LifecycleListener[] findLifecycleListeners() {
        return new LifecycleListener[0];
    }

    @Override
    public void removeLifecycleListener(LifecycleListener listener) {

        lifecycle.removeLifecycleListener(listener);
    }

    /**
     * 当前context启动的时候，需要启动的内容包括(注意，需要按照顺序启动)：
     * 1.(Servlet) loader
     * 2.child container
     * 3.pipeline
     * @throws LifecycleException
     */
    @Override
    public void start() throws LifecycleException {
        log("Simple Context is now starting!");

        if(started){
            throw new LifecycleException("SimpleContext has already started");
        }


        lifecycle.fireLifecycleEvent(BEFORE_START_EVENT,null);

        started = true;
        /**
         * start the loader
         */
        if((loader != null) && (loader instanceof Lifecycle) ){
            ((Lifecycle) loader).start();
        }

        /**
         * start the child containers
         */
        for(String name: childs.keySet()){
            Container child = childs.get(name);
            ((Lifecycle)child).start();
        }

        /**
         * start the pipeline of this context
         */
        if(pipeline instanceof Lifecycle){
            ((Lifecycle)pipeline).start();
        }

        /**
         * StandardContext启动的时候，也初始化一下ContextConfig
         * 后续参考tomcat官方实现，以事件的方式启动ContextConfig
         */
        ContextConfig contextConfig = new ContextConfig(this);
        ((Lifecycle)contextConfig).start();

        lifecycle.fireLifecycleEvent(START_EVENT,null);


        /**
         * 设置默认的Manager
         * 一般可以在BootStrap中设置
         */
        if(null == getManager()){
            setManager(new PersistentManager());
        }

        /**
         * 启动Cluster
         */
        if(null != cluster && (cluster instanceof Lifecycle)){
            ((Lifecycle)cluster).start();
        }

        /**
         * 启动manager
         */
        if(null != manager && (manager instanceof Lifecycle)){
            ((Lifecycle)manager).start();
        }





        log("Simple Context has started!");
    }

    /**
     * 当前context停止的时候，需要停止的内容包括(注意，需要按照顺序停止，停止顺序和启动顺序正好相反)：
     * 1.pipeline
     * 2.child container
     * 3.(Servlet) loader
     * @throws LifecycleException
     */
    @Override
    public void stop() throws LifecycleException {
        log("Simple Context is now stopping!");
        lifecycle.fireLifecycleEvent(BEFORE_STOP_EVENT,null);
        lifecycle.fireLifecycleEvent(STOP_EVENT,null);

        started = false;

        /**
         * stop the pipeline of this context
         */
        if(pipeline instanceof Lifecycle){
            ((Lifecycle)pipeline).stop();
        }

        /**
         * stop the child containers
         */
        for(String name: childs.keySet()){
            Container child = childs.get(name);
            ((Lifecycle)child).stop();
        }

        /**
         * stop the loader
         */
        if((loader != null) && (loader instanceof Lifecycle) ){
            ((Lifecycle) loader).stop();
        }

        /**
         * 关闭manager
         */
        if(null != manager && (manager instanceof Lifecycle)){
            ((Lifecycle)manager).stop();
        }

        /**
         * 关闭Cluster
         */
        if(null != cluster && (cluster instanceof Lifecycle)){
            ((Lifecycle)cluster).stop();
        }

        log("Simple Context is stopped!");

    }
}
