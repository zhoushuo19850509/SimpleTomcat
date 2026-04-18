package com.nbcb.mytomcat.catalina.filter;

import org.apache.catalina.Context;
import org.apache.catalina.Loader;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import java.util.Enumeration;

/**
 * 代表某个filter实例
 */
public class SimpleApplicationFilterConfig
        implements FilterConfig {

    private Context context;
    private SimpleFilterDef simpleFilterDef;
    private Filter filter;

    /**
     * constructor
     * @param context
     * @param simpleFilterDef
     */
    public SimpleApplicationFilterConfig(
            Context context,
            SimpleFilterDef simpleFilterDef) {
        this.context = context;
        this.simpleFilterDef = simpleFilterDef;
    }

    /**
     * 这个是核心方法，就是把filter配置信息，转化为filter实例
     * @return
     */
    public Filter getFilter(){

        String filterClass = simpleFilterDef.getFilterClass();

        if(null != this.filter){
            return filter;
        }

        Loader loader = context.getLoader();

        ClassLoader classLoader = loader.getClassLoader();

        Class myClass = null;
        try {
//            System.out.println("print the current of classloader" + classLoader.toString());
            myClass =    classLoader.loadClass(filterClass);
            this.filter = (Filter)myClass.newInstance();
//            System.out.println("class resource of the servlet instance: " + myClass.getResource("/"));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        return this.filter;

    }

    @Override
    public String getFilterName() {
        return this.simpleFilterDef.getFilterName();
    }

    @Override
    public ServletContext getServletContext() {
        return null;
    }

    @Override
    public String getInitParameter(String s) {
        return null;
    }

    @Override
    public Enumeration getInitParameterNames() {
        return null;
    }
}
