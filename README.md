# spring-amqp-rpc
使用Spring Boot（1.5.9）+Spring AMQP+RabbitMQ实现RPC的demo

## 为何要使用MQ实现RPC
1. 相比于http接口实现的RPC，MQ实现的RPC客户端不需要知道RPC服务端的存在。
2. MQ实现的RPC服务端高可用，只需要简单地启动多个RPC服务即可，不需要额外的服务注册发现以及负载均衡。
3. 如果原有的MQ的普通消息需要知道执行结果，可以很方便地切换到RPC模式。
4. RabbitMQ RPC的工作方式非常擅长处理异步回调式的任务

## 为何要写这个demo
Spring AMQP的官方文档提供了一个RPC的demo，但是RPC服务端是同步返回结果的，同步的RPC服务会顺序执行RPC队列中的请求，
如果某一个请求执行较慢，会阻塞后面的请求并造成严重的性能问题。解决这种问题的方法是设置并发消费者（concurrentConsumers属性)或者启动多个RPC服务。
这在大多数情况下是有效的，但是如果这个任务是异步的，或者甚至是事件驱动的（比如NIO中的readable事件），那么同步阻塞消费者线程的方式就不太合适了。
该Demo中提供了3个服务端示例：
* 同步执行的RPC服务；多执行几次测试用例，客户端可能会超时（convertSendAndReceive默认5秒）。
* 异步执行的RPC服务，客户端为每个请求创建一个临时的回复队列（或者使用Direct reply-to）。
* 异步执行的RPC服务，客户端使用固定回复队列，需要提供额外的correlationId以关联请求和响应。

## 参考资料
* RabbitMQ官方文档，介绍RPC的工作方式:<https://www.rabbitmq.com/tutorials/tutorial-six-spring-amqp.html>
* Spring AMQP官方文档：<https://docs.spring.io/spring-amqp/docs/1.7.5.RELEASE/reference/html/_reference.html#direct-reply-to>
* Direct reply-to介绍：<https://www.rabbitmq.com/direct-reply-to.html>
