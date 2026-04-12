20210807
今天开始chap10 Security的学习
Security的功能，在tomcat官网上有完整的描述：
"Realm Configuration How-To"

此外，在springboot官网guide上，也要几个专题来说Security相关的内容，不知道是不是一回事。

20210808
今天继续学习Security
梳理相关的类

20210809
今天继续学习Security
把几个基础的类搭建起来了

20210810
今天继续学习Security
今天针对tomcat官网，把整个security的流程过了一遍。
想通了一些事情。
比如如何把authenticator加入到context pipeline、valve执行顺序等
明天计划把这个流程写一下
顺便把valve执行的原理、流程梳理一下。
把lifecycle的原理也梳理一下。
明天计划总共要梳理3个主题

同时，通过authenticate的功能开发过程，总结一下tomcat新增功能、新增Filter的一般规律。


20210814
今天继续学习security
上次我们通过tomcat官网源码，大致了解了Securiy的整体架构，这次我们再梳理一下。

20210817
梳理重点代码：BasicAuthenticator.authenticate()方法的逻辑

20210911
又有差不多一个月的时间没有推进tomcat工程了。
原因有2：
1.了解了Springboot Security相关的内容。了解security在后端服务中的应用；
2.这次集中批量下载了很多电子书，大部分是文史哲的。
要尽快回到tomcat了，之前梳理清楚的流程又忘得差不多了。
这是不太好的 ，之前保持的状态又没有了

20211001
今天学习servlet specification的security部分。


20211010
这两天，包括国庆，都在学tomcat/http security部分
学习了http权威指南security相关的内容，收获还是挺多的。

20230916
过去了两年多，真是汗颜。
想想在这两年时间里，有哪些收获呢？
1.对java concurrent programming有了一定的实践的了解，有助于我们学习tomcat
2.学习了netty，对NIO有了一定的了解，有助于我们学习tomcat nio部分的内容
3.系统学习了UML，对于后续我们梳理tomcat整体架构有了一定的了解
4.系统学习了spring security，有助于我们理解tomcat chap10 security相关的内容
5.提升了git的操作方式，有助于我们管理simpletomcat的版本
6.我们学习了很多网上资料，各种主题，和tomcat有关的，
我印象最深刻的就是一篇介绍如何通过调整tomcat代码，调整http keepalive策略
最终实现负载均衡优雅切换的目标。
这些主题解答了很多有关tomcat的疑问。

所以说，这两年光阴也没有白费。现在我们重新再把tomcat拾起来


20240902
又过去了一年。这次回到tomcat 有这些因素：
1.今天看了这篇网文：《我是如何从零到成为 Apache 顶级项⽬的 Committer》
此人介绍了自己如何接触GitHub，如何成为Apache Pulsa等两个开源项目的committer，B格还是很高的。
让人非常羡慕。我希望通过这篇文章，尝试为tomcat贡献代码，后续进而找机会成为某个开源项目的commiter

2.最近调研的一个疑难杂症： max-file-size(超出multipart文件上传的阈值)
在这个疑难杂症调研过程中，对tomcat代码的熟悉给我助力很多。

3.看了很长时间的鲁迅、摄影。这些东西虽然有意思，但是毕竟不是我们的专业。
这导致两个问题：这种非本专业的内容，我看得津津有味，别人也有条件看(说白了就是没有门槛)
二是反过来会影响我自身的专业。
而对于tomcat源码调研、参与开源项目这个事，就不是每个人都有条件可以轻松从事了。这个是有一定门槛的。

所以，我在说什么懂了吧，加强专业能力，不是一句口号喊喊的。
专业课和选修课的关系，职业和爱好的关系，你懂的吧，不用我多说了吧。

所以，我觉得正事还是要回到tomcat来。
爱好可以有，文学、摄影，都是很好的爱好，但是爱好毕竟只是爱好，作为调剂可以，但是不能影响专业。




