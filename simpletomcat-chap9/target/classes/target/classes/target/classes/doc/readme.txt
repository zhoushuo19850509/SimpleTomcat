这章引入了session的概念

1.session整体功能
我在实现session功能之前，首先要搞清楚session的概念。
session其实就是在服务端保存了某个客户端某次访问服务端的会话信息。
使得无状态的http协议请求，实现了状态保持的功能。

2.tomcat session功能的局限
这个功能当然很重要，但是我们也要看到，session并非tomcat的核心功能。
原因是随着我们服务器集群规模越来越大，如何保证tomcat节点间的session共享？
一种实现思路是在负载均衡端(F5)，进行会话保持。
一种实现思路是在tomcat中实现集群中各个节点的session同步。
当然，因为tomcat不是专业做集群的，
因此，tomcat官方也建议我们不要在在实际生产环境中大规模使用tomcat集群Session同步的功能。
什么是大规模呢？tomcat10的官方文档中提示，一般不要在超过4个节点的集群中使用Session同步的功能。

3.session一般保存在哪里？
那么，session保存在哪里呢？一般来说，在高负载系统中(比如网银、手机银行)，
我们把session保存在Redis集群中。
原因是，session的调用一般非常频繁，几乎所有涉及客户访问的servlet请求，都会用到session。
session性能高低，直接决定了客户的访问效率。
但是session数据的存取，逻辑又相对简单，无非就是根据sessionid获取session对象。
因此，放在Redis这种K/V的缓存中是比较合适的。

4.我们学习tomcat session的意义
既然tomcat session这么鸡肋，我们为啥还要学习呢？
(1)借助这次机会，深入理解session的原理
(2)借助这次机会，能够了解会话保持的原理
(3)借助这次机会，能够了解如何通过无状态的http请求实现客户端状态保持。
(4)借助这次机会，初步了解集群间同步的实现原理
(5)借助这次机会，初步了解像tomcat这类java容器，如何实现数据持久化






