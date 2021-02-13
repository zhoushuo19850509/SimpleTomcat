本章包含的核心代码包括：



1.Session.java
session接口，这个只要复用org.apache.catalina.Session就行了

2.StandardSession.java
实现了Session接口

3.Manager.java
Manager接口，这个也只要复用org.apache.catalina.Manager就行了

4.StandardManager.java
实现了Manager接口


5.ManagerBase.java
StandardManagerd的父类


6.PersistentManagerBase
PersistentManager和DistributedManager的父类

7.PersistentManager.java
用于持久化

8.DistributedManager
用于分布式session管理

9.Store
Store接口，这个只要复用org.apache.catalina.Store就行了
主要是用于持久化相关的

10.StoreBase
实现了Store接口

11.FileStore/JDBCStore
StoreBase的子类，主要是实现持久化相关的内容



具体结构参考UML架构图

