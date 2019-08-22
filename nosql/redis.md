# NoSql

## redis

### redis使用

#### 一、介绍

#### 二、常用命令

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



#### 三、使用

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

### Springboot整合Redis

### Springboot整合Redisson

#### 一、介绍

#### 二、整合

##### 1、引入POM

```POM
        <spring-boot.version>2.1.1.RELEASE</spring-boot.version>
        <redisson.version>3.10.6</redisson.version>
        <redisson-springboot.version>3.10.6</redisson-springboot.version>
         
         <!-- redisson -->
         <dependency>
             <groupId>org.redisson</groupId>
             <artifactId>redisson-spring-boot-starter</artifactId>
             <version>${redisson-springboot.version}</version>
         </dependency>
         <dependency>
             <groupId>org.redisson</groupId>
             <artifactId>redisson</artifactId>
             <version>${redisson.version}</version>
         </dependency>
```

##### 2、配置文件

Tips:新建redisson-config.yml配置文件

```yml
# Redisson配置
singleServerConfig:
    address: "redis://127.0.0.1:6379"
    password: null
    clientName: null
    database: 0
    idleConnectionTimeout: 10000
    pingTimeout: 1000
    connectTimeout: 10000
    timeout: 3000
    retryAttempts: 3
    retryInterval: 1500
    reconnectionTimeout: 3000
    failedAttempts: 3
    subscriptionsPerConnection: 5
    subscriptionConnectionMinimumIdleSize: 1
    subscriptionConnectionPoolSize: 50
    connectionMinimumIdleSize: 32
    connectionPoolSize: 64
    dnsMonitoringInterval: 5000
    # dnsMonitoring: false

threads: 0
nettyThreads: 0
codec:
    class: "org.redisson.codec.JsonJacksonCodec"
transportMode: "NIO"
```

##### 3、写配置类

```java
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * @author sjp
 * @date 2019/4/30
 **/
@Configuration
public class RedissonConfig {

    @Bean
    public RedissonClient redisson() throws IOException {
        // 本模块使用的是yaml格式的配置文件，读取使用Config.fromYAML，如果是Json文件，则使用Config.fromJSON
        Config config = Config.fromYAML(RedissonConfig.class.getClassLoader().getResource("redisson-config.yml"));
        return Redisson.create(config);
    }

}
```

##### 4、测试类

```java
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RBucket;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RunWith(SpringRunner.class)
@Component
public class RedissonTest {

    @Autowired
    private RedissonClient redisson;

    @Autowired
    private RedisTemplate<String, String> template;

    @Test
    public void set() {
        redisson.getKeys().flushall();
        RBucket<String> rBucket =  redisson.getBucket("key");
        rBucket.set("1231");
        RMap<String, String> m = redisson.getMap("test", StringCodec.INSTANCE);
        m.put("1", "2");
        BoundHashOperations<String, String, String> hash = template.boundHashOps("test");
        String t = hash.get("1");
        assertThat(t).isEqualTo("2");
    }

}
```

#### 三、踩坑

##### 1、redisson连接池的连接无法释放

Springboot2.1.1 之前的版本在接入redisson的时候，redission无法自动释放redis连接，会导致池中连接都在占用状态，后续的请求将无法从连接池中获取连接，导致请求阻塞。

解决方案，使用 2.1.1及之后的版本即可

// TODO 具体原因待分析。

##### 2、开发阶段应用经常重启，导致redis已经被应用占用的连接无法释放。

// TODO 待解决方案，在系统关闭时，调用redisson.shutdown() 

