后续要新建工程，就非常方便了，只要
1.新建一个maven工程
  比如： simpletomcat-chap8

2.复制src目录、pom.xml文件(部分内容)、lib目录

3.设置JDK1.8

4.设置source folder(src)

5.设置要引入的jar包
主要是引入servlet.jar/tomcat-util.jar

5.编译

6.启动BootStrap.java验证我们发布的servlet是否能够正常访问
http://localhost:8080/servlet/ModernServlet
http://localhost:8080/servlet/PrimitiveServlet
