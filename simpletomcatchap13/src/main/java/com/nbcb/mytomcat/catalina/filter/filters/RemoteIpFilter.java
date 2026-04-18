package com.nbcb.mytomcat.catalina.filter.filters;

import javax.servlet.*;
import java.io.IOException;

/**
 * 限制特定客户端ip能够访问
 */
public class RemoteIpFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain)
            throws IOException, ServletException {
        System.out.println("RemoteIpFilter executing ...");
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}
