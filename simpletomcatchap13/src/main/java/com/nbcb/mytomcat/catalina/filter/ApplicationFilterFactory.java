package com.nbcb.mytomcat.catalina.filter;

import com.nbcb.mytomcat.catalina.core.StandardContext;
import org.apache.catalina.Wrapper;

import javax.servlet.Servlet;
import javax.servlet.ServletRequest;
import java.util.Map;

/**
 * 用于生成filter chain对象：
 * SimpleApplicationFilterChain
 */
public class ApplicationFilterFactory {

    /**
     * 用于生成filter chain，供后续执行各个filter
     * @param request
     * @param wrapper
     * @param servlet
     * @return
     */
    public static SimpleApplicationFilterChain createFilterChain(
            ServletRequest request,
            Wrapper wrapper,
            Servlet servlet
    ){
        SimpleApplicationFilterChain applicationFilterChain
                = new SimpleApplicationFilterChain();

        applicationFilterChain.setServlet(servlet);

        /**
         * 根据wrapper获取上层容器： StandardContext
         */
        StandardContext context = (StandardContext) wrapper.getParent();

        /**
         * 从context获取filter信息
         */
        Map<String, SimpleFilterDef> filterDefMap = context.getFilterMaps();

        /**
         * 遍历这些filter，一个个添加到SimpleApplicationFilterChain
         */
        for(String filterName : filterDefMap.keySet()){
            SimpleFilterDef filterDef = filterDefMap.get(filterName);
            SimpleApplicationFilterConfig filterConfig =
                    new SimpleApplicationFilterConfig(context, filterDef);
            applicationFilterChain.addFilter(filterConfig);
        }

        return applicationFilterChain;

    }

}
