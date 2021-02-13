

在实践session之前，我们先通过servlet规范，了解session的几个要点
总体来说，session主要是为了在无状态的http协议框架下，更好地管理客户端。
让我们分清楚各个客户端，到底谁是谁。

1.session信息追踪
2.session创建
3.session范围
4.往session中添加属性
5.session超时
6.session最近一次更新时间
7.重要的session语法
  7.1并发访问session
  7.2分布式session
  7.3客户端session的语法必须遵守规范