[TOC]





# Netty

题外话：在开始了解Netty之前，请务必完全知晓IO的基本概念，这将助于你更好的学习Netty。

飞机票：[IO](../../../../BaseJava/IO/IO.md)、[NIO](../../../../BaseJava/IO/NIO.md)


## 什么是Netty？

先来看看[官网](https://netty.io)的介绍：

> Netty is *an asynchronous event-driven network application framework*
> for rapid development of maintainable high performance protocol servers & clients.
>
> Netty 是一个异步事件驱动的网络应用程序框架
> 用于快速开发可维护的高性能协议服务器和客户端。

再来看看百度百科：

> Netty是由[JBOSS](https://baike.baidu.com/item/JBOSS)提供的一个[java开源](https://baike.baidu.com/item/java开源/10795649)框架，现为 [Github](https://baike.baidu.com/item/Github/10145341)上的独立项目。Netty提供异步的、[事件驱动](https://baike.baidu.com/item/事件驱动/9597519)的网络应用程序框架和工具，用以快速开发高性能、高可靠性的[网络服务器](https://baike.baidu.com/item/网络服务器/99096)和客户端程序。
>
> 也就是说，Netty 是一个基于NIO的客户、服务器端的编程框架，使用Netty 可以确保你快速和简单的开发出一个网络应用，例如实现了某种协议的客户、[服务端](https://baike.baidu.com/item/服务端/6492316)应用。Netty相当于简化和流线化了网络应用的编程开发过程，例如：基于TCP和UDP的socket服务开发。
>
> “快速”和“简单”并不用产生维护性或性能上的问题。Netty 是一个吸收了多种协议（包括FTP、SMTP、HTTP等各种二进制文本协议）的实现经验，并经过相当精心设计的项目。最终，Netty 成功的找到了一种方式，在保证易于开发的同时还保证了其应用的性能，稳定性和伸缩性

咱们自己总结一下：Netty是一个基于NIO的网络编程框架，他具有异步、以事件为驱动的特性，同时高性能和可扩展。

## 特性

PS：自己总结的再好有官方的解释精准吗？废话少说，直接搬运官方的，咱们做个翻译官。

### 设计

- Unified API for various transport types - blocking and non-blocking socket

  为多样的传输类型提供统一的API——阻塞和非阻塞的Socket

- Based on a flexible and extensible event model which allows clear separation of concerns

  基于灵活且可扩展的事件模型，允许清晰的关注点分离

- Highly customizable thread model - single thread, one or more thread pools such as SEDA

  高度可定制的线程模型——单线程、一个或多个线程池，如 SEDA

- True connectionless datagram socket support (since 3.1)

  真正的无连接数据报套接字支持（自 3.1 起）

### 使用简单

- Well-documented Javadoc, user guide and examples

  有据可查的 Javadoc、用户指南和示例

- No additional dependencies, JDK 5 (Netty 3.x) or 6 (Netty 4.x) is enough

  没有额外的依赖，JDK 5 (Netty 3.x) 或 6 (Netty 4.x) 就足够了

  - Note: Some components such as HTTP/2 might have more requirements. Please refer to [the Requirements page](https://netty.io/wiki/requirements.html) for more information.

    注意：某些组件（例如 HTTP/2）可能有更多要求。 请参阅[需求页面](https://netty.io/wiki/requirements.html)了解更多信息。

### 性能

- Better throughput, lower latency

  更高的吞吐量，更低的延迟

- Less resource consumption

  更少的资源消耗

- Minimized unnecessary memory copy

  最小化不必要的内存复制

### 安全 

- Complete SSL/TLS and StartTLS support

  完整的 SSL/TLS 和 StartTLS 支持 

### 社区

- Release early, release often

  发布的更早和更频繁

- The author has been writing similar frameworks since 2003 and he still finds your feed back precious!

  作者自 2003 年以来一直在编写类似的框架，他仍然认为您的反馈很宝贵！

## 系统架构

这是官网的系统架构图：

![System Architecture](Netty简介.assets/components.jpg)

## 构成部分

### Channel

Channel是 Java NIO 的一个基本构造。可以看作是传入或传出数据的载体。因此，它可以被打开或关闭，连接或者断开连接。

### CallBack

CallBack通常被称为回调，提供给另一个方法作为引用，这样后者就可以在某个合适的时间调用前者。

### Future

Future 提供了另外一种通知应用操作已经完成的方式。这个对象作为一个异步操作结果的占位符,它将在将来的某个时候完成并提供结果。

Netty 的异步编程模型都是建立在 Future 与 Callback 概念之上的。

### Event 和 Handler

Netty 使用不同的事件来通知我们更改的状态或操作的状态，这使我们能够根据发生的事件触发适当的行为。

## 核心组件

### Bootstrap相关

Bootstarp（客户端） 和 ServerBootstrap（服务端） 被称为引导类，指对应用程序进行配置，并使他运行起来的过程。

#### Bootstrap

Bootstrap 是客户端的引导类，Bootstrap 在调用 bind()（连接UDP）和 connect()（连接TCP）方法时，会新创建一个 Channel，仅创建一个单独的、没有父 Channel 的 Channel 来实现所有的网络交换。

#### ServerBootstrap

ServerBootstrap 是服务端的引导类，ServerBootstarp 在调用 bind() 方法时会创建一个 ServerChannel 来接受来自客户端的连接，并且该 ServerChannel 管理了多个子 Channel 用于同客户端之间的通信。

### Channel相关

#### Channel

底层网络传输 API 必须提供给应用 I/O操作的接口，如read()，write()，connenct()，bind()等等。对于我们来说，这是结构几乎总是会成为一个“Socket”。 Netty 中的接口 Channel 定义了与 Socket 丰富交互的操作集：bind、 close、 config、connect、isActive、 isOpen、isWritable、 read、write 等等。 

Netty 提供大量的 Channel 实现来专门使用。这些包括 AbstractChannel，AbstractNioByteChannel，AbstractNioChannel，EmbeddedChannel， LocalServerChannel，NioSocketChannel 等等。

与Channel相关的概念有以下四个：

![ChannelPipeline](Netty简介.assets/Channel.jpg)

- ChannelHandler，核心处理业务就在这里，用于处理业务请求。
- ChannelHandlerContext，用于传输业务数据。
- ChannelPipeline，用于保存处理过程需要用到的ChannelHandler和ChannelHandlerContext。

#### ChannelHandler

ChannelHandler 是对 Channel 中数据的处理器，这些处理器可以是系统本身定义好的编解码器，也可以是用户自定义的。这些处理器会被统一添加到一个 ChannelPipeline 的对象中，然后按照添加的顺序对 Channel 中的数据进行依次处理。

#### ChannelPipeline

ChannelPipeline 提供了一个容器给 ChannelHandler 链，并提供了一个API 用于管理沿着链入站和出站事件的流动。每个 Channel 都有自己的ChannelPipeline，当 Channel 创建时自动创建的。

#### ChannelFuture

Netty 中所有的 I/O 操作都是异步的，即操作不会立即得到返回结果，所以 Netty 中定义了一个 ChannelFuture 对象作为这个异步操作的“代言人”，表示异步操作本身。如果想获取到该异步操作的返回值，可以通过该异步操作对象的addListener() 方法为该异步操作添加监听器，为其注册回调：当结果出来后马上调用执行。

#### ByteBuf

- ByteBuf是一个存储字节的容器，最大特点就是**使用方便**，它既有自己的读索引和写索引，方便你对整段字节缓存进行读写，也支持get/set，方便你对其中每一个字节进行读写，他的数据结构如下图所示：

![ByteBuf](Netty简介.assets/Buffer.jpg)



他有三种使用模式：

1. Heap Buffer 堆缓冲区
    堆缓冲区是ByteBuf最常用的模式，他将数据存储在JVM的堆空间。

2. Direct Buffer 直接缓冲区

   直接缓冲区是ByteBuf的另外一种常用模式，他的内存分配都不发生在堆，JDK1.4引入的NIO的ByteBuffer类，允许jvm通过本地方法调用分配内存，这样做有两个好处

   - 通过免去中间交换的内存拷贝，提升IO处理速度；直接缓冲区的内容可以驻留在垃圾回收扫描的堆区以外。
   - DirectBuffer 在 -XX:MaxDirectMemorySize=xxM大小限制下，使用 Heap 之外的内存，GC对此”无能为力”，也就意味着规避了在高负载下频繁的GC过程对应用线程的中断影响。

3. Composite Buffer 复合缓冲区
    复合缓冲区相当于多个不同ByteBuf的视图，这是Netty提供的，Jdk不提供这样的功能。

除此之外，他还提供一大堆Api方便你使用，在这里我就不一一列出了。

#### Codec

Netty中的编码/解码器，通过他你能完成字节与实体Bean的相互转换，从而达到自定义协议的目的。 在Netty里面最有名的就是HttpRequestDecoder和HttpResponseEncoder了。

### Event相关

#### EventLoop

EventLoop 定义了Netty的核心抽象，用于处理 Channel 的 I/O 操作。在内部，将会为每个Channel分配一个EventLoop，用于处理用户连接请求、对用户请求的处理等所有事件。EventLoop 本身只是一个线程驱动，在其生命周期内只会绑定一个线程，让该线程处理一个 Channel 的所有 IO 事件。

一个 Channel 一旦与一个 EventLoop 相绑定，那么在 Channel 的整个生命周期内是不能改变的。

一个 EventLoop 可以与多个 Channel 绑定。即 Channel 与 EventLoop 的关系是 n:1，而 EventLoop 与线程的关系是 1:1。

####  EventLoopGroup

EventLoopGroup 是一个 EventLoop 池，包含很多的 EventLoop。

## 线程模型

### Reactor模式

详细见[Reactor模式](../../../DesignModes/Reactor模式.md)

### Netty线程模型

Netty的线程模型就是一个实现了Reactor模式的经典模式。

#### 结构对应
NioEventLoop ———— Initiation Dispatcher
Synchronous EventDemultiplexer ———— Selector
Evnet Handler ———— ChannelHandler
ConcreteEventHandler ———— 具体的ChannelHandler的实现

#### 模式对应
Netty服务端使用了“多Reactor线程模式”
MainReactor ———— bossGroup(NioEventLoopGroup) 中的某个NioEventLoop
SubReactor ———— workerGroup(NioEventLoopGroup) 中的某个NioEventLoop
Acceptor ———— ServerBootstrapAcceptor
ThreadPool ———— 用户自定义线程池

#### 流程

1. 当服务器程序启动时，会配置ChannelPipeline，ChannelPipeline中是一个ChannelHandler链，所有的事件发生时都会触发Channelhandler中的某个方法，这个事件会在ChannelPipeline中的ChannelHandler链里传播。然后，从bossGroup事件循环池中获取一个NioEventLoop来现实服务端程序绑定本地端口的操作，将对应的ServerSocketChannel注册到该NioEventLoop中的Selector上，并注册ACCEPT事件为ServerSocketChannel所感兴趣的事件。
2. NioEventLoop事件循环启动，此时开始监听客户端的连接请求。
3. 当有客户端向服务器端发起连接请求时，NioEventLoop的事件循环监听到该ACCEPT事件，Netty底层会接收这个连接，通过accept()方法得到与这个客户端的连接(SocketChannel)，然后触发ChannelRead事件(即，ChannelHandler中的channelRead方法会得到回调)，该事件会在ChannelPipeline中的ChannelHandler链中执行、传播。
4. ServerBootstrapAcceptor的readChannel方法会该SocketChannel(客户端的连接)注册到workerGroup(NioEventLoopGroup) 中的某个NioEventLoop的Selector上，并注册READ事件为SocketChannel所感兴趣的事件。启动SocketChannel所在NioEventLoop的事件循环，接下来就可以开始客户端和服务器端的通信了。



## 为什么

### 为什么要有Netty

先假设没有Netty，我们怎们实现网络编程：

远古：

- java.net + java.io

近代：

- java.nio

其他：

- Mina，Grizzly等（未来可能会有其他）

其中Mina和Grizzly，我们后续放到对比在仔细研究。远古时代的Net和IO想必大家都知道，BIO在高并发时存在性能问题。于是NIO应运而生，虽到但是总感觉缺点什么：

1. 复杂的API（有多少同学至今还区分不了ByteBuffer的flip()、clear()、rewind()、compact()？）
2. 为了编写高质量的NIO代码，你需要足够了解多线程编程、网络编程，甚至你还得知道Reactor模式。
3. 另外，NIO只帮我们做了功能，但是没有做全功能。一个没有BUG的代码，绝不不仅仅包含正常的逻辑分支，他还包含各种异常的情况。在网络编程中，客户端断连重连、网络闪断、半包读写、失败缓存、网络拥塞和异常码流等等情况更是家常便饭，NIO并没有提供完整的解决方案。

### 为什么要用Netty

- 统一的 API，支持多种传输类型，阻塞和非阻塞的。
- 简单而强大的线程模型。
- 自带编解码器解决 TCP 粘包/拆包问题。
- 自带各种协议栈。
- 真正的无连接数据包套接字支持。
- 比直接使用 Java 核心 API 有更高的吞吐量、更低的延迟、更低的资源消耗和更少的内存复制。
- 安全性不错，有完整的 SSL/TLS 以及 StartTLS 支持。
- 社区活跃
- 成熟稳定，经历了大型项目的使用和考验，而且很多开源项目都使用到了 Netty， 比如我们经常接触的 Dubbo、RocketMQ 等等。
- ......

### 为什么封装好

假如我们要完成客户端-服务端SayHello这样的通信，在Socket时代一个简单的Demo是这样的：

客户端：

```java
import java.io.*;
import java.net.Socket;

public class Client {

    public static final String host = "localhost";
    public static final int port = 8080;

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket(host, port);
        BufferedReader bufferedReader;
        BufferedWriter bufferedWriter;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bufferedWriter.write("hello服务器!!!!");
            bufferedWriter.newLine();
            bufferedWriter.flush();
            String line = bufferedReader.readLine();
            System.out.println(Thread.currentThread().getName() + " 从客户端收到内容: " + line);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }

}
```

服务端：

```java
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static int port = 8080;

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        Socket socket = serverSocket.accept();
        BufferedReader bufferedReader;
        BufferedWriter bufferedWriter;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            String line = bufferedReader.readLine();
            System.out.println(Thread.currentThread().getName() + " 从客户端收到内容: " + line);
            bufferedWriter.write("服务器端传来相应");
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
```

到了近代呢，再看看NIO如何实现：

客户端：

```java
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class EchoClient {
  public static final String host = "localhost";
  public static final int port = 8080;

  public static void main(String[] args) {
    SocketChannel socketChannel = null;
    try {
      socketChannel = SocketChannel.open();
      socketChannel.connect(new InetSocketAddress(host, port));
      // write
      String newData = "hello i'm client" + System.currentTimeMillis();
      byte[] data = newData.getBytes(StandardCharsets.UTF_8);
      ByteBuffer lenBuf = ByteBuffer.wrap(int2byte(data.length));
      ByteBuffer textBuf = ByteBuffer.wrap(data);
      ByteBuffer[] byteBuffer = new ByteBuffer[] {lenBuf, textBuf};
      socketChannel.write(byteBuffer);
      // read
      lenBuf.clear();
      textBuf.clear();
      socketChannel.read(byteBuffer);
      System.out.println("from server:" + new String(textBuf.array()));
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (socketChannel != null) {
        try {
          socketChannel.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  public static byte[] int2byte(int len) {
    byte[] b = new byte[4];
    b[0] = (byte) (len >> 24);
    b[1] = (byte) (len >> 16 & 0XFF);
    b[2] = (byte) (len >> 8 & 0XFF);
    b[3] = (byte) (len & 0XFF);
    return b;
  }
}
```

服务端：

```java
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

public class EchoServer {
  public static final String host = "localhost";
  public static final int port = 8080;

  public static void main(String[] args) {
    ServerSocketChannel serverSocketChannel = null;
    Selector selector;
    try {
      serverSocketChannel = ServerSocketChannel.open();
      serverSocketChannel.configureBlocking(false);
      serverSocketChannel.socket().bind(new InetSocketAddress(host, port));
      selector = Selector.open();
      serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

      while (selector.select() > 0) {
        Set<SelectionKey> selectionKeySet = selector.selectedKeys();
        Iterator<SelectionKey> selectionKeyIterator = selectionKeySet.iterator();
        while (selectionKeyIterator.hasNext()) {
          SelectionKey selectionKey = selectionKeyIterator.next();
          if (selectionKey.isValid()) {
            if (selectionKey.isAcceptable()) {
              ServerSocketChannel serverChannel = (ServerSocketChannel) selectionKey.channel();
              SocketChannel clientChannel = serverChannel.accept();
              clientChannel.configureBlocking(false);
              clientChannel.register(selector, SelectionKey.OP_READ, new MessageInfo());
            }
            if (selectionKey.isReadable()) {
              SocketChannel clientChannel = (SocketChannel) selectionKey.channel();
              MessageInfo messageInfo = (MessageInfo) selectionKey.attachment();
              if (messageInfo.getLenBuf() == null) {
                messageInfo.setLenBuf(ByteBuffer.allocate(4));
              }
              // 消息长度
              int len = messageInfo.getLen();
              // 判断消息长度是否读取完成
              if (len != -1) {
                if (messageInfo.contentBuf == null) {
                  // 创建指定消息长度的buf
                  messageInfo.contentBuf = ByteBuffer.allocate(len);
                }
                // 如果消息未读取完成，继续读取
                if (messageInfo.contentBuf.position() < len) {
                  if (clientChannel.read(messageInfo.contentBuf) == -1) {
                    clientChannel.register(selector, SelectionKey.OP_WRITE, messageInfo);
                  }
                }

                if (len == 0) {
                  System.out.println("空消息");
                  selectionKey.cancel();
                  selectionKey.channel().close();
                } else if (messageInfo.contentBuf.position() == len) {
                  messageInfo.contentBuf.flip();
                  System.out.println(new String(messageInfo.contentBuf.array()));
                  clientChannel.register(selector, SelectionKey.OP_WRITE, messageInfo);
                }
              }
              // 消息长度未读取完成
              else {
                int i = clientChannel.read(messageInfo.getLenBuf());
                if (i == -1) {
                  System.out.println("消息异常:" + messageInfo.getLenBuf());
                  break;
                }
                if (messageInfo.getLenBuf().position() == 4) {
                  messageInfo.getLenBuf().flip();
                  messageInfo.setLen(messageInfo.getLenBuf().getInt());
                }
              }
            }
            if (selectionKey.isWritable()) {
              SocketChannel clientChannel = (SocketChannel) selectionKey.channel();
              String newData = "hello i'm server" + System.currentTimeMillis();
              ByteBuffer byteBuffer = ByteBuffer.wrap(newData.getBytes(StandardCharsets.UTF_8));
              clientChannel.write(byteBuffer);
              selectionKey.cancel();
              selectionKey.channel().close();
            }
          }
          selectionKeySet.remove(selectionKey);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (serverSocketChannel != null) {
        try {
          serverSocketChannel.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  /** 消息对象 */
  static class MessageInfo {
    private int len = -1;
    private ByteBuffer lenBuf;
    private ByteBuffer contentBuf;

    public int getLen() {
      return len;
    }

    public void setLen(int len) {
      this.len = len;
    }

    public ByteBuffer getLenBuf() {
      return lenBuf;
    }

    public void setLenBuf(ByteBuffer lenBuf) {
      this.lenBuf = lenBuf;
    }

    public ByteBuffer getContentBuf() {
      return contentBuf;
    }

    public void setContentBuf(ByteBuffer contentBuf) {
      this.contentBuf = contentBuf;
    }
  }
}
```

最后，我们再来看看Netty如何实现：

客户端：

```java
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;

public class EchoClient {

  private final String host;
  private final int port;

  public EchoClient(String host, int port) {
    this.host = host;
    this.port = port;
  }

  public void start() throws Exception {
    EventLoopGroup group = new NioEventLoopGroup();
    try {
      Bootstrap b = new Bootstrap(); 
      b.group(group) 
          .channel(NioSocketChannel.class) 
          .remoteAddress(new InetSocketAddress(host, port)) 
          .handler(
              new ChannelInitializer<SocketChannel>() { 
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                  ch.pipeline().addLast(new EchoClientHandler());
                }
              });

      ChannelFuture f = b.connect().sync(); 
      f.channel().closeFuture().sync(); 
    } finally {
      group.shutdownGracefully().sync(); 
    }
  }

  public static void main(String[] args) throws Exception {
    final String host = "127.0.0.1";
    final int port = 8080;
    new EchoClient(host, port).start();
  }

  @ChannelHandler.Sharable 
  public static class EchoClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
      ctx.writeAndFlush(
          Unpooled.copiedBuffer(
              "Netty rocks!", 
              CharsetUtil.UTF_8));
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, ByteBuf in) {
      System.out.println("Client received: " + in.toString(CharsetUtil.UTF_8));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { 
      cause.printStackTrace();
      ctx.close();
    }
  }
}
```

服务端：

```java
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;

public class EchoServer {

  private final int port;

  public EchoServer(int port) {
    this.port = port;
  }

  public static void main(String[] args) throws Exception {
    int port = 8080; 
    new EchoServer(port).start(); 
  }

  public void start() throws Exception {
    NioEventLoopGroup group = new NioEventLoopGroup();
    try {
      ServerBootstrap b = new ServerBootstrap();
      b.group(group) 
          .channel(NioServerSocketChannel.class) 
          .localAddress(new InetSocketAddress(port)) 
          .childHandler(
              new ChannelInitializer<SocketChannel>() { 
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                  System.out.println("initChannel ch:" + ch);
                  ch.pipeline().addLast(new EchoServerHandler());
                }
              });
      ChannelFuture f = b.bind().sync();
      System.out.println(
          EchoServer.class.getName() + " started and listen on " + f.channel().localAddress());
      f.channel().closeFuture().sync(); 
    } finally {
      group.shutdownGracefully().sync();
    }
  }


  @ChannelHandler.Sharable 
  public static class EchoServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
      ByteBuf in = (ByteBuf) msg;
      System.out.println("Server received: " + in.toString(CharsetUtil.UTF_8)); 
      ctx.write(in); 
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
      ctx.writeAndFlush(Unpooled.EMPTY_BUFFER) 
          .addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
      cause.printStackTrace(); 
      ctx.close(); 
    }
  }
}
```

到此可见，BIO代码虽少，但是开发需要了解API。NIO的代码实现最为复杂。Netty则是提供流式编程，针对数据的处理，专门提供 ChannelHandler处理，你大可以放心的在Handler干自己想干的事。

### 为什么性能高

#### 异步非阻塞

##### 非阻塞

Netty是一款基于NIO（Nonblocking I/O，非阻塞IO）开发的网络通信框架，对比于BIO（Blocking I/O，阻塞IO），他的并发性能得到了很大提高。下图展示了BIO和NIO的工作模式。相比BIO，NIO的特点有：1、不阻塞、2、单线程能处理更多的连接。

![BIO&NIO](Netty简介.assets/BIO&NIO.png)



Netty的I/O线程NioEventLoop由于聚合了多路复用器Selector，可以同时并发处理成百上千个客户端的SocketChannel。由于读写操作都是非阻塞的，这就可以充分提升I/O线程的运行效率，避免由频繁的I/O阻塞导致的线程挂起。

> IO多路复用技术通过把多个IO的阻塞复用到同一个Select的阻塞上，从而使得系统在单线程的情况下可以同时处理多个客户端请求。

##### 异步

另外，由于Netty采用了异步通信模式，一个I/O线程可以并发处理N个客户端连接和读写操作，这从根本上解决了传统同步阻塞I/O一连接一线程模型，架构的性能、弹性伸缩能力和可靠性都得到了极大的提升。

#### 高效的Reactor线程模型

Netty服务端采用Reactor主从多线程模型

1. 主线程：Acceptor 线程池用于监听Client 的TCP 连接请求
2. 从线程：Client 的IO 操作都由一个特定的NIO 线程池负责，负责消息的读取、解码、编码和发送
3. Client连接有很多，但是NIO 线程数是比较少的，一个NIO 线程可以同时绑定到多个Client，同时一个Client只能对应一个线程，避免出现线程安全问题

#### 无锁化的串行设计

串行设计：数据的处理尽可能在一个线程内完成，期间不进行线程切换，避免了多线程竞争和同步锁的使用。

​		采用环形数组缓冲区实现无锁化并发编程，代替传统的线程安全容器或者锁。

#### 高效的并发编程

#### 高性能的序列化框架

内部默认支持了Google的Protobuf，当然不仅如此，Netty还支持用户自定义序列化框架，以便实现更高的性能。

#### 零拷贝

零拷贝。在操作系统层面上，零拷贝是指避免在用户态与内核态之间来回拷贝数据的技术。在Netty中，零拷贝与操作系统层面上的零拷贝不完全一样，Netty的零拷贝完全是在用户态（Java层面）的，更多是数据操作的优化。 

主要体现在以下几个方面：

- ByteBuffer

  Netty发送和接收消息主要使用ByteBuffer，ByteBuffer使用直接内存（DirectMemory）直接进行Socket读写。

  原因：如果使用传统的堆内存进行Socket读写，JVM会将堆内存Buffer拷贝一份到直接内存中，然后再写入Socket，多了一次缓冲区的内存拷贝。DirectMemory可以直接通过DMA发送到网卡接口。

- Composite Buffers

  传统的ByteBuffer，如果需要将两个ByteBuffer中的数据组合到一起，我们需要首先创建一个size=size1+size2大小的新的数组，然后将两个数组中的数据拷贝到新的数组中。但是使用Netty提供的组合ByteBuf，就可以避免这样的操作，因为CompositeByteBuf并没有真正将多个Buffer组合起来，而是保存了它们的引用，从而避免了数据的拷贝，实现了零拷贝。

- 对于FileChannel.transferTo的使用

  Netty中使用了FileChannel的transferTo方法，该方法依赖于操作系统实现零拷贝。

- 通过wrap操作实现零拷贝

  通过wrap操作，我们可以将byte[]数组、ByteBuf、ByteBuffer等包装成一个Netty ByteBuf对象， 进而避免拷贝操作。

- 通过slice操作实现零拷贝

  ByteBuf支持slice操作，可以将ByteBuf分解为多个共享同一个存储区域的ByteBuf，避免内存的拷贝。

#### 内存池

支持通过内存池的方式循环利用ByteBuf，避免了频繁创建和销段ByteBuf带来的性能损耗。

#### 灵活的Tcp参数配置能力

可配置的I/O线程数、TCP参数等，为不同的用户场景提供定制化的调优参数，满足不同的性能场景。

## 对比

### Netty&Tomcat

Netty和Tomcat最大的区别就在于通信协议，Tomcat是基于Http协议的，他的实质是一个基于Http协议的Web容器，但是Netty不一样，他能通过编程自定义各种协议，因为Netty能够通过Codec自己来编码/解码字节流，完成类似Redis访问的功能，这就是Netty和Tomcat最大的不同。

有人说Netty的性能就一定比Tomcat性能高，其实不然，Tomcat从6.x开始就支持了NIO模式，并且后续还有APR模式——一种通过JNI调用Apache网络库的模式，相比于旧的BIO模式，并发性能得到了很大提高，特别是APR模式，而Netty是否比Tomcat性能更高，则要取决于Netty程序作者的技术实力了。

### Netty&Mina 

未完待续...
