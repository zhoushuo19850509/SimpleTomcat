

1.Authenticator

所谓的Authenticator，其实一个valve，挂靠在context下的valve。
这个valve比较特殊，执行顺序在wrapper之前：
在wrapper执行servlet之前，就要执行一下。
用来判断当前客户端是否有权限访问本servlet.

具体代码包含：
Authenticator接口
AuthenticatorBase实现类(基类)

具体Authenticator实现类：
BasicAuthenticator
DigestAuthenticator
FormAuthenticator
NonLoginAuthenticator
SSLAuthenticator

备注：除了Authenticator接口，其他所有的相关实现类都放在authenticator package下

2.Realm
Realm挂靠在Context下的一个组件
Realm这个组件用于判断当前用户是否有权限访问本servlet。
怎么判断是否有权限呢？有好多实现方案。比如把用户名、密码保存在数据库(JDBCRealm)；
或者把用户名、密码直接配置在配置文件(tomcat-users.xml)中，tomcat启动的时候加载到内存(MemoryRealm)。

Authenticator就是调用Realm这个组件来判断用户权限的。

具体代码包括：
Realm(接口)

RealmBase实现类(基类)

具体Realm实现类：
JDBCRealm
JNDIRealm
MemoryRealm
UserDatabaseRealm

书本中有2个试验性质的Realm实现类，需要自己实现
SimpleRealm
SimpleUserDatabaseRealm

备注：除了Realm接口，其他所有的相关实现类都放在realm package下

3.GenericPrincipal

接口：
Principal

实现：
GenericPrincipal

GenericPrincipal关联了一个Realm/username/password
判断当前用户对于某个role是否有权限

备注：放在realm package下

4.LoginConfig

5.SecurityCollection

6.SecurityConstraint

7.SimpleContextConfig

这个类主要设置StandardContext相关的配置。
主要原理就是借助StandardContext的lifecycle接口，在StandardContext启动的时候，
设置StandardContext的一些配置。
本章围绕Security主题，将一个Authenticator作为一个valve，
设置到StandardContext的pipeline中去。后续StandardContext在执行servlet之前，
会首先调用这个Authenticator作，判断当前客户端是否有权限执行servlet

试验代码
以上是catalina官方的security相关的代码
以下几个是书本中列出来试验性质的代码
1.SimpleContextConfig
一个简单的配置相关的类，我猜应该是从配置文件(比如web.xml这种)读取配置，然后把配置信息放到这个类中。
后续在Digest这章中会有优化。

2.SimpleRealm
模拟RealmBase类，实现了Ream接口

3.SimpleUserDatabaseRealm
模拟某个具体的Realm实现类，继承了RealmBase类

4.BootStrap1
启动类1
在启动类中引入了如下组件：
SecurityCollection
SecurityConstraint
LoginConfig
SimpleRealm

用于实现Security相关的功能。

5.BootStrap2
启动类2
和BootStrap1相比，引入了我们自己开发的SimpleUserDatabaseRealm




