package com.nbcb.mytomcat.catalina.filter;

import javax.servlet.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 保存各个filter，
 * 逐个执行各个filter
 */
public class SimpleApplicationFilterChain implements FilterChain {


    private List<SimpleApplicationFilterConfig> filterList = new ArrayList<>();
    private Servlet servlet;

    /**
     * filter chain链条中总共有多少filter
     */
    private int filterCount = 0;

    /**
     * 当前已经执行到的filter index
     */
    private int pos = 0;


    /**
     * 核心方法：
     * 1.执行filter chiain
     * 2.执行servlet
     */
    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse)
            throws IOException, ServletException {

        /**
         * 先执行filter chain
         */
        if(pos < filterCount){
            SimpleApplicationFilterConfig filterConfig = filterList.get(pos++);
            Filter filter = filterConfig.getFilter();
            filter.doFilter(servletRequest, servletResponse, this);
            /**
             * 这个return的意思是，如果filter chain没有全部执行完毕，那么不要往后执行
             * 只有等filter chain全部执行完毕，才执行后面的service
             * 否则servlet就会重复执行了
             */
            return;
        }

        System.out.println("finish all filters ...");

        /**
         * 等到filter chain中的fileter全部执行完，就执行servlet
         */
        this.servlet.service(servletRequest, servletResponse);
        System.out.println("finish invoke the servlet instance");


    }


    /**
     * 把filter添加到filter chain
     * @param filterConfig
     */
    public void addFilter(SimpleApplicationFilterConfig filterConfig){
        filterCount++;
        this.filterList.add(filterConfig);
    }

    public void setServlet(Servlet servlet) {
        this.servlet = servlet;
    }
}
