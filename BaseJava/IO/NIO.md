[TOC]



## NIO

NIO重点是把**Channel（通道）**，**Buffer（缓冲区）**，**Selector（选择器）**三个类之间的关系弄清楚。

![img](NIO.assets/NIO.jpg)

## 代码模板

NIO大致分为这几步骤：

1. 获取channel
2. 设置非阻塞
3. 创建多路复用器selector
4. channel和selector做关联
5. 根据selector返回的channel状态处理逻辑

```java
// 开启一个channel
ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
// 设置为非阻塞
serverSocketChannel.configureBlocking(false);
// 绑定端口
serverSocketChannel.bind(new InetSocketAddress(PORT));
// 打开一个多路复用器
Selector selector = Selector.open();
// 绑定多路复用器和channel
serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
// 获取到达的事件
while (selector.select() > 0) {
    Set<SelectionKey> keys = selector.keys();
    Iterator<SelectionKey> iterator = keys.iterator();
    while (iterator.hasNext()) {
        SelectionKey selectionKey = iterator.next();
        if (selectionKey.isAcceptable()) {
            // 处理逻辑
        }
        if (selectionKey.isReadable()) {
            // 处理逻辑
        }
    }
}
```

