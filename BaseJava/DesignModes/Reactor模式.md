## 1.Reactor模式介绍

Reactor模式是事件驱动模型，有一个或多个并发输入源，有一个Service Handler，有多个Request Handlers；这个Service Handler会同步的将输入的请求（Event）多路复用的分发给相应的Request Handler。从结构上，这有点类似生产者消费者模式，即有一个或多个生产者将事件放入一个Queue中，而一个或多个消费者主动的从这个Queue中Poll事件来处理；而Reactor模式则并没有Queue来做缓冲，每当一个Event输入到Service Handler之后，该Service Handler会主动的根据不同的Event类型将其分发给对应的Request Handler来处理。

![img](https:////upload-images.jianshu.io/upload_images/4720632-772107bb22dac4a0.png?imageMogr2/auto-orient/strip|imageView2/2/w/527/format/webp)

Reactor类图

该图为Reactor模型，实现Reactor模式需要实现以下几个类：

1）EventHandler：事件处理器，可以根据事件的不同状态创建处理不同状态的处理器；

2）Handle：可以理解为事件，在网络编程中就是一个Socket，在数据库操作中就是一个DBConnection；

3）InitiationDispatcher：用于管理EventHandler，分发event的容器，也是一个事件处理调度器，Tomcat的Dispatcher就是一个很好的实现，用于接收到网络请求后进行第一步的任务分发，分发给相应的处理器去异步处理，来保证吞吐量；

4）Demultiplexer：阻塞等待一系列的Handle中的事件到来，如果阻塞等待返回，即表示在返回的Handle中可以不阻塞的执行返回的事件类型。这个模块一般使用操作系统的select来实现。在Java NIO中用Selector来封装，当Selector.select()返回时，可以调用Selector的selectedKeys()方法获取Set<SelectionKey>，一个SelectionKey表达一个有事件发生的Channel以及该Channel上的事件类型。

![img](https:////upload-images.jianshu.io/upload_images/4720632-9146b59382d59ebf.png?imageMogr2/auto-orient/strip|imageView2/2/w/546/format/webp)

Reactor时序图

1）初始化InitiationDispatcher，并初始化一个Handle到EventHandler的Map。

2）注册EventHandler到InitiationDispatcher中，每个EventHandler包含对相应Handle的引用，从而建立Handle到EventHandler的映射（Map）。

3）调用InitiationDispatcher的handle_events()方法以启动Event Loop。在Event Loop中，调用select()方法（Synchronous Event Demultiplexer）阻塞等待Event发生。

4）当某个或某些Handle的Event发生后，select()方法返回，InitiationDispatcher根据返回的Handle找到注册的EventHandler，并回调该EventHandler的handle_events()方法。

5）在EventHandler的handle_events()方法中还可以向InitiationDispatcher中注册新的Eventhandler，比如对AcceptorEventHandler来，当有新的client连接时，它会产生新的EventHandler以处理新的连接，并注册到InitiationDispatcher中。

 