[TOC]



# 简介

> 

## 数据结构

### 基础数据结构

#### Strings

#### Hashes

#### Lists

#### Sets

#### Sorted Sets

##### 跳跃表

> 推荐文章：https://mp.weixin.qq.com/s?__biz=Mzg5MzU2NDgyNw==&mid=2247487151&idx=1&sn=30b017e0d25b80848e645fc77d772655
&source=41#wechat_redirect

### 高级数据结构

#### Bitmaps

#### HyperLogLogs

#### Geospatial Indexes

#### Streams

### 底层数据结构

#### SDS

#### Ziplist

#### Quicklist

#### Dict

## 特性

### 事务（Transactions）

### Pub/Sub

### Lua Scripting

### Keys with a limited time-to-live

### LRU eviction of keys

### Automatic failover

### 持久化

#### AOF

#### RDB

# 客户端

## Java

### Jedis

Jedis 是老牌的 Redis 的 Java 实现客户端，提供了比较全面的 Redis 命令的支持，其官方网址是：http://tool.oschina.net/uploads/apidocs/redis/clients/jedis/Jedis.html。

优点：

- 支持全面的 Redis 操作特性（可以理解为API比较全面）。

缺点：

- 使用阻塞的 I/O，且其方法调用都是同步的，程序流需要等到 sockets 处理完 I/O 才能执行，不支持异步；
- Jedis 客户端实例不是线程安全的，所以需要通过连接池来使用 Jedis。

### lettuce

lettuce （[ˈletɪs]），是一种可扩展的线程安全的 Redis 客户端，支持异步模式。如果避免阻塞和事务操作，如BLPOP和MULTI/EXEC，多个线程就可以共享一个连接。lettuce 底层基于 Netty，支持高级的 Redis 特性，比如哨兵，集群，管道，自动重新连接和Redis数据模型。lettuce 的官网地址是：https://lettuce.io/

优点：

- 支持同步异步通信模式；
- Lettuce 的 API 是线程安全的，如果不是执行阻塞和事务操作，如BLPOP和MULTI/EXEC，多个线程就可以共享一个连接。

### Redisson

Redisson 是一个在 Redis 的基础上实现的 Java 驻内存数据网格（In-Memory Data Grid）。它不仅提供了一系列的分布式的 Java 常用对象，还提供了许多分布式服务。其中包括( BitSet, Set, Multimap, SortedSet, Map, List, Queue, BlockingQueue, Deque, BlockingDeque, Semaphore, Lock, AtomicLong, CountDownLatch, Publish / Subscribe, Bloom filter, Remote service, Spring cache, Executor service, Live Object service, Scheduler service) Redisson 提供了使用Redis 的最简单和最便捷的方法。Redisson 的宗旨是促进使用者对Redis的关注分离（Separation of Concern），从而让使用者能够将精力更集中地放在处理业务逻辑上。Redisson的官方网址是：https://redisson.org/

优点：

- 使用者对 Redis 的关注分离，可以类比 Spring 框架，这些框架搭建了应用程序的基础框架和功能，提升开发效率，让开发者有更多的时间来关注业务逻辑；
- 提供很多分布式相关操作服务，例如，分布式锁，分布式集合，可通过Redis支持延迟队列等。

缺点：

- Redisson 对字符串的操作支持比较差。

### 总结

搭配lettuce + Redisson

Jedis 和 lettuce 是比较纯粹的 Redis 客户端，几乎没提供什么高级功能。Jedis 的性能比较差，所以如果你不需要使用 Redis 的高级功能的话，优先推荐使用 lettuce。

Redisson 的优势是提供了很多开箱即用的 Redis 高级功能，如果你的应用中需要使用到 Redis 的高级功能，建议使用 Redisson。具体 Redisson 的高级功能可以参考：https://redisson.org/



# 闲扯

## Redis的十万个为什么

### Redis 为什么早期版本选择单线程？

#### 官方解释

因为 Redis 是基于内存的操作，**CPU 不是 Redis 的瓶颈**，Redis 的瓶颈最有可能是 **机器内存的大小** 或者 **网络带宽**。既然单线程容易实现，而且 CPU 不会成为瓶颈，那就顺理成章地采用单线程的方案了。

#### 简单总结一下

1. 使用单线程模型能带来更好的 **可维护性**，方便开发和调试；
2. 使用单线程模型也能 "**并发**" 的处理客户端的请求；*(I/O 多路复用机制)*
3. Redis 服务中运行的绝大多数操作的 **性能瓶颈都不是 CPU**；

> **强烈推荐** 各位亲看一下这篇文章：
>
> - 为什么 Redis 选择单线程模型 · Why's THE Design? - https://draveness.me/whys-the-design-redis-single-thread

### Redis 为什么这么快？

简单总结：

1. **纯内存操作**：读取不需要进行磁盘 I/O，所以比传统数据库要快上不少；
2. **单线程，无锁竞争**：这保证了没有线程的上下文切换，不会因为多线程的一些操作而降低性能；
3. **多路 I/O 复用模型，非阻塞 I/O**：采用多路 I/O 复用技术可以让单个线程高效的处理多个网络连接请求（尽量减少网络 IO 的时间消耗）；
4. **高效的数据结构，加上底层做了大量优化**：Redis 对于底层的数据结构和内存占用做了大量的优化，例如不同长度的字符串使用不同的结构体表示，HyperLogLog 的密集型存储结构等等..

# 附件

## 常用命令

1、获取最大允许连接数

```shell
config get maxclients
```

2、获取当前连接情况

```shell
info clients
```

3、杀死指定连接

```shell
CLIENT KILL ip:port   
```

4、从服务器变为主服务器

```shell
 SLAVEOF NO ONE
```

5、scan实现模糊查询

> - SCAN相关命令包括SSCAN 命令、HSCAN 命令和 ZSCAN 命令，分别用于集合、哈希键及有续集等
> 

命令格式：

 ```shell
 SCAN cursor [MATCH pattern] [COUNT count]
 ```

  命令解释：scan 游标 MATCH <返回和给定模式相匹配的元素> count 每次迭代所返回的元素数量

>   SCAN命令是增量的循环，每次调用只会返回一小部分的元素。所以不会有KEYS命令的坑(key的数量比较多，一次KEYS查询会block其他操作)。  
>   SCAN命令返回的是一个游标，从0开始遍历，到0结束遍历。
>   通过scan中的MATCH <pattern> 参数，可以让命令只返回和给定模式相匹配的元素，实现模糊查询的效果

例如：

```shell
示例：
scan 0 match DL* count 5 
sscan myset 0 match f*
```

注意：游标的值，其实就是查出来结果的number号

6、哨兵模式下，如果通过哨兵查询主从服务器？

使用 sentinel masters  命令列出主服务器信息

> 备注：以下列出的是 Sentinel 接受的命令：
>
> - PING ：返回 PONG 。
> - sentinel masters ：列出所有被监视的主服务器，以及这些主服务器的当前状态。
> - sentinel slaves ：列出给定主服务器的所有从服务器，以及这些从服务器的当前状态。
> - sentinel get-master-addr-by-name ： 返回给定名字的主服务器的 IP 地址和端口号。 如果这个主服务器正在执行故障转移操作， 或者针对这个主服务器的故障转移操作已经完成， 那么这个命令返回新的主服务器的 IP 地址和端口号。
> - sentinel reset ： 重置所有名字和给定模式 pattern 相匹配的主服务器。 pattern 参数是一个 Glob 风格的模式。 重置操作清楚主服务器目前的所有状态， 包括正在执行中的故障转移， 并移除目前已经发现和关联的， 主服务器的所有从服务器和 Sentinel 。
> - sentinel failover ： 当主服务器失效时， 在不询问其他 Sentinel 意见的情况下， 强制开始一次自动故障迁移 （不过发起故障转移的 Sentinel 会向其他 Sentinel 发送一个新的配置，其他 Sentinel 会根据这个配置进行相应的更新）。



