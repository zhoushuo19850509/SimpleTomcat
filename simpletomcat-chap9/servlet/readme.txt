这个package只是临时的，用来保存我们的一些servlet类
后续当然是建议单独设置一个工程，模拟servlet开发的工程。

为啥要把这个目录单独设置为Source呢？
那是因为我们还没有掌握将servlet name映射到某个package下的servlet的技术
后续掌握了之后，可以把servlet类放到有目录层级的package下。


1.MySessionCreateServlet
这个sevlet是用于创建session的

2.MySessionPlainServlet
这个servlet是普通servlet，
用于验证客户端在创建session成功后，后续再次访问tomcat，是否会把之前创建的session信息带上来



