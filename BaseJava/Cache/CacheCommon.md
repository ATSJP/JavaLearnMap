[TOC]



# 缓存

## 缓存同步策略

首先需要明确，除了把整个缓存和持久化存储都做串行化，或者操作一个的时候直接锁死另一个，或者通过消息队列操作缓存以达到最终一致性，不然必然会有产生数据不一致的可能。选择哪种缓存策略是根据自己的业务场景进行取舍。

典型的缓存模式，一般有如下几种：

- **Cache Aside**
- **Read/Write Through**
- **Write Behind**

### Cache Aside

```flow
st=>start: 开始
e=>end: 结束
op_db=>operation: 从DB加载数据
op_cache=>operation: 将DB数据放入Cache中
cond_re_wr=>condition: 是否是读？
cond_cache=>condition: 缓存是否有数据
io=>inputoutput: 输出数据

op_db_write=>operation: 更新DB数据
op_cache_write=>operation: 删除缓存

st->cond_re_wr
cond_re_wr(no)->op_db_write
op_db_write(right)->op_cache_write->e

cond_re_wr(yes)->cond_cache
cond_cache(no)->op_db(right)->op_cache
cond_cache(yes)->io
op_cache->io
io->e
```

经常用到的一种策略模式。这种模式主要流程如下：

- **失效**：应用程序先从 Cache 取数据，没有得到，则从数据库中取数据，成功后，放到缓存中。
- **命中**：应用程序从 Cache 中取数据，取到后返回。
- **更新**：先把数据存到数据库中，成功后，再**删除**缓存。

这种缓存策略会在如下情况出现脏数据：

```sequence
title: 脏数据产生场景
participant A线程
participant B线程
participant Cache

A线程->Cache:查询数据
Cache-->>A线程:不存在
A线程->A线程:加载DB数据
B线程->B线程:更新DB数据
B线程->Cache:删除缓存
A线程->Cache:将DB数据放入缓存
```



- 线程 A 查询一个数据，在缓存中没有找到，于是去数据库查，拿到数据之后还没放到缓存中。
- 这时候线程 B 过来更新这一条数据，先更新数据库之后准备令缓存失效，由于缓存还没放进来所以不需要失效。
- 线程 A 把数据放到了缓存中。但是这个数据其实已经是脏数据了，跟数据库中不同步。

这种情况发生概率要稍微低，因为需要在数据库的查询操作和缓存的更新操作之间，插入一个数据库的修改和缓存的修改操作，一般情况下，后者需要的时间是要远高于前者的。当然也不能忽视这种情况确实存在。

### Read/Write Through

Read/Write Through 套路是把更新数据库的操作由缓存自己代理了，对应用层来说是透明的，应用不再需要关心数据同步问题。

```flow
st=>start: 开始
e=>end: 结束
op_db=>operation: 开辟缓存空间
op_cache=>operation: 将低速存储器数据放入Cache中
cond_re_wr=>condition: 是否是读？
cond_cache_read=>condition: 缓存是否有数据
io=>inputoutput: 输出数据

cond_cache_write=>condition: 缓存是否有数据
op_db_write=>operation: 更新低速存储器数据
op_cache_write=>operation: 将数据写入Cache

st->cond_re_wr

cond_re_wr(yes)->cond_cache_read
cond_cache_read(no)->op_db(right)->op_cache
cond_cache_read(yes)->io
op_cache->io
io->e

cond_re_wr(no)->cond_cache_write
cond_cache_write(yes)->op_cache_write->op_db_write
cond_cache_write(no)->op_db_write
op_db_write->e
```

#### Read Through

Read Through 就是在查询操作中更新缓存，也就是说，当缓存失效的时候（过期或 LRU 换出），Cache Aside 是由调用方负责把数据加载入缓存，而 Read Through 则用缓存服务自己来加载，从而对应用方是透明的。

#### Write Through

Write Through 套路和 Read Through 相仿，不过是在更新数据时发生。当有数据更新的时候，如果没有命中缓存，直接更新数据库，然后返回。如果命中了缓存，则更新缓存，然后再由 Cache 自己更新数据库（这是一个同步操作）。


这种方式的风险也显而易见，如果追求落库之后再返回成功，效率必然降低很多，缓存的意义就不大了。如果追求落到缓存上就算成功，那问题又抛给了缓存的丢失风险上。

### Write Behind

Write Behind 又叫 Write Back。就是 Linux 文件系统的 Page Cache 的算法。Write Back 套路，一句说就是，在更新数据的时候，只更新缓存，不更新数据库，而我们的缓存会异步地批量更新数据库。这个设计的好处就是让数据的 I/O 操作飞快无比（因为直接操作内存嘛）。

因为异步，Write Back 还可以合并对同一个数据的多次操作，所以性能的提高是相当可观的。性能很强悍，问题也很明显，数据不是强一致性的，而且可能会丢失。另外 Write Behind 实际上的逻辑还比较复杂，因为需要追踪定位哪些数据需要做持久化。

## 缓存常见问题

### 一致性问题

如何保证Cache、DB数据保持强一致性？这不就类似于分布式事务？未完待续...

PS：当然Cache的主要目的是为了性能，如果必须强一致性，则一定会带来性能损失，未必是个好主意，凡事需要结合业务以及实际应用场景来平衡。

### 缓存穿透

请求去查询一条压根儿数据库中根本就不存在的数据，也就是缓存和数据库都查询不到这条数据，但是请求每次都会打到数据库上面去。

可能引发的问题：数据库压力过大，扛不住。

解决办法：

- 缓存短时间的空值

- 布隆过滤器：可以用来判断超大数据量中单个数据的存在性问题。但是存在误差，如果布隆过滤器判断不存在，那肯定不存在，但是如果判断存在，也有一定的可能误判。

### 缓存雪崩

是指在我们设置缓存时采用了相同的过期时间，导致缓存在某一时刻同时失效，请求全部转发到数据库，数据库瞬时压力过重雪崩。

可能引发的问题：数据库压力过大，扛不住。

解决办法：

- 将缓存失效时间分散开，比如我们可以在原有的失效时间基础上增加一个随机值。

### 缓存击穿

对于一些设置了过期时间的Key，如果这些Key可能会在某些时间点被超高并发地访问，是一种非常“热点”的数据。这个时候，需要考虑一个问题：缓存被“击穿”的问题，这个和缓存雪崩的区别在于这里针对某一 key 缓存，雪崩则是很多 key。

可能引发的问题：单个 key 过期的瞬间大量请求过来，导致数据库压力过大崩了。

解决办法：

- 加锁，单个请求去更新缓存就行了，别的排队等着。
- 双层的超时值，保存一个用于更新缓存的超时值，到达这个值之后去延长一下真是的超时时间。

### 热点Key

缓存中的某些Key（可能对应用与某个促销商品）对应的 value 存储在集群中一台机器，使得所有流量涌向同一机器，成为系统的瓶颈。

解决办法：

- 客户端缓存（多级缓存），将热点 key 对应 value 并缓存在客户端本地，并且设置一个失效时间。
- 将单个热点分散为多个子 key，时期请求的时候 hash 到不同的机器上处理。





















