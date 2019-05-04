# NoSql

## redis

### redis使用

一、常用命令

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

