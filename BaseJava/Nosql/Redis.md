[TOC]



## Redis

### 一、介绍

### 二、常用命令

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

### 三、使用

- **1、哨兵模式下，如果通过哨兵查询主从服务器？**

  使用 sentinel masters  命令列出主服务器信息

  > 备注：以下列出的是 Sentinel 接受的命令：
  >
  > - PING ：返回 PONG 。
  > - sentinel masters ：列出所有被监视的主服务器，以及这些主服务器的当前状态。
  > - sentinel slaves ：列出给定主服务器的所有从服务器，以及这些从服务器的当前状态。
  > - sentinel get-master-addr-by-name ： 返回给定名字的主服务器的 IP 地址和端口号。 如果这个主服务器正在执行故障转移操作， 或者针对这个主服务器的故障转移操作已经完成， 那么这个命令返回新的主服务器的 IP 地址和端口号。
  > - sentinel reset ： 重置所有名字和给定模式 pattern 相匹配的主服务器。 pattern 参数是一个 Glob 风格的模式。 重置操作清楚主服务器目前的所有状态， 包括正在执行中的故障转移， 并移除目前已经发现和关联的， 主服务器的所有从服务器和 Sentinel 。
  > - sentinel failover ： 当主服务器失效时， 在不询问其他 Sentinel 意见的情况下， 强制开始一次自动故障迁移 （不过发起故障转移的 Sentinel 会向其他 Sentinel 发送一个新的配置，其他 Sentinel 会根据这个配置进行相应的更新）。



