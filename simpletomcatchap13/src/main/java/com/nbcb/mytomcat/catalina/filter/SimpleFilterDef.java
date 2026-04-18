package com.nbcb.mytomcat.catalina.filter;

import lombok.Data;

/**
 * 这个代码包含filter定义，对应web.xml中的filter配置信息：
 *     <filter>
 *       <filter-name>Remote Address Filter</filter-name>
 *       <filter-class>org.org.apache.catalina.filters.RemoteAddrFilter</filter-class>
 *     </filter>
 */
@Data
public class SimpleFilterDef {

    private String filterName;

    private String filterClass;

}
