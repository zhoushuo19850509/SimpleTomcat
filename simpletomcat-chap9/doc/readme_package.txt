这个文档介绍一下怎么打包

打包方式其实很简单
1.cd 工程目录
2.mvn clean
3.mvn compile
4.mvn package
5.手工编辑jar包(备注1)
6.部署servlet(备注2)

备注1 手工编辑jar包
vi simpletomcat-chap9-1.0-SNAPSHOT.jar
编辑META-INF/MANIFEST.MF

加入如下的内容：
lib/servlet-1.0.jar lib/tomcat-util-1.0.jar


这里为啥需要手工处理。

这个问题的背景是这样的：
为了验证tomcat cluster功能，我们计划把tomcat部署到各个节点。
但是在部署过程中碰到了一些困难。
这些困难都登记在了《HowTomcatWorks问题整理.docx》文档中
我们都予以了解决。但是有一个问题还没有解决。那就是我们通过lib/*.jar引入的jar包，没法注册到MANIFEST.MF中去
具体内容参考
《HowTomcatWorks问题整理.docx》/问题8

本次修改内容：
1.项目可以打包为jar包并独立部署
2.调整项目整体架构，使用maven工程架构

关键改动包括：
1.pom.xml加入了jar包编译相关选项




备注2 部署servlet
tomcat最终的功能当然是要能够执行servlet。
我们把之前准备好的servlet class类，拷贝过去，使得tomcat能够访问这些servlet class
我们知道，servlet工程和tomcat工程师两个工程，按照道理来说应该分开打包。
为了方便起见，我们把两个工程放到了一起，一起打包。这个需要特别注意。

