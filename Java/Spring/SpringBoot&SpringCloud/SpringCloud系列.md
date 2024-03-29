[TOC]



# SpringCloud系列


> 以下内容，均在搭建 [lemon](https://github.com/ATSJP/lemon) 项目时，收集和写下的一些内容，如有错误欢迎指正>>>[@SJP](mailto:shijianpeng2010@163.com)

## 实际中应用及推荐文章

推荐文章：https://blog.csdn.net/zrl0506/article/details/80165477

![1548589316026](./SpringCloud系列.assets/20170918114736747.png)

推荐文章：https://blog.csdn.net/qq_37170583/article/details/80704904

## SpringCloud和SpringBoot对应版本

官方文档：http://spring.io/projects/spring-cloud（一切始于官方文档）

**Table1**

| SpringCloud | SpringBoot |
| ----------- | ---------- |
| Greenwich   | 2.1.X      |
| Finchley    | 2.0.X      |
| Edgware     | 1.5.x      |
| Dalston     | 1.5.x      |

**Table2**

| Component                 | Edgware.SR5    | Finchley.SR2  | Finchley.BUILD-SNAPSHOT |
| ------------------------- | -------------- | ------------- | ----------------------- |
| spring-cloud-aws          | 1.2.3.RELEASE  | 2.0.1.RELEASE | 2.0.1.BUILD-SNAPSHOT    |
| spring-cloud-bus          | 1.3.3.RELEASE  | 2.0.0.RELEASE | 2.0.1.BUILD-SNAPSHOT    |
| spring-cloud-cli          | 1.4.1.RELEASE  | 2.0.0.RELEASE | 2.0.1.BUILD-SNAPSHOT    |
| spring-cloud-commons      | 1.3.5.RELEASE  | 2.0.2.RELEASE | 2.0.2.BUILD-SNAPSHOT    |
| spring-cloud-contract     | 1.2.6.RELEASE  | 2.0.2.RELEASE | 2.0.2.BUILD-SNAPSHOT    |
| spring-cloud-config       | 1.4.5.RELEASE  | 2.0.2.RELEASE | 2.0.2.BUILD-SNAPSHOT    |
| spring-cloud-netflix      | 1.4.6.RELEASE  | 2.0.2.RELEASE | 2.0.2.BUILD-SNAPSHOT    |
| spring-cloud-security     | 1.2.3.RELEASE  | 2.0.1.RELEASE | 2.0.1.BUILD-SNAPSHOT    |
| spring-cloud-cloudfoundry | 1.1.2.RELEASE  | 2.0.1.RELEASE | 2.0.1.BUILD-SNAPSHOT    |
| spring-cloud-consul       | 1.3.5.RELEASE  | 2.0.1.RELEASE | 2.0.2.BUILD-SNAPSHOT    |
| spring-cloud-sleuth       | 1.3.5.RELEASE  | 2.0.2.RELEASE | 2.0.2.BUILD-SNAPSHOT    |
| spring-cloud-stream       | Ditmars.SR4    | Elmhurst.SR1  | Elmhurst.BUILD-SNAPSHOT |
| spring-cloud-zookeeper    | 1.2.2.RELEASE  | 2.0.0.RELEASE | 2.0.1.BUILD-SNAPSHOT    |
| spring-boot               | 1.5.16.RELEASE | 2.0.6.RELEASE | 2.0.7.BUILD-SNAPSHOT    |
| spring-cloud-task         | 1.2.3.RELEASE  | 2.0.0.RELEASE | 2.0.1.BUILD-SNAPSHOT    |
| spring-cloud-vault        | 1.1.2.RELEASE  | 2.0.2.RELEASE | 2.0.2.BUILD-SNAPSHOT    |
| spring-cloud-gateway      | 1.0.2.RELEASE  | 2.0.2.RELEASE | 2.0.2.BUILD-SNAPSHOT    |
| spring-cloud-openfeign    |                | 2.0.2.RELEASE | 2.0.2.BUILD-SNAPSHOT    |
| spring-cloud-function     | 1.0.1.RELEASE  | 1.0.0.RELEASE | 1.0.1.BUILD-SNAPSHOT    |

注意：

SpringCloud 版本为 Edgware 及以下，eureka包改为：

```pom
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-eureka-server</artifactId>
</dependency>
```

SpringCloud 版本为 Edgware 以上，eureka包改为netflix：

```pom
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
</dependency>
```

## SpringCloudEureka

### 入门案例（一） Server与Client

### 入门案例（二）Server、Porvider与Consumer

#### 项目整体结构：

![1548589316026](./SpringCloud系列.assets/1548589316026.png)

#### eureka-server入门

##### 一、导入jar

```pom
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <artifactId>lemon-soa</artifactId>
    <packaging>jar</packaging>

    <name>spring-cloud-eureka-server</name>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>1.5.9.RELEASE</version>
        <relativePath/>
    </parent>

    <properties>
        <spring-cloud.version>Dalston.SR4</spring-cloud.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-eureka-server</artifactId>
        </dependency>
    </dependencies>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

</project>

```

##### 二、建立启动类

```java
package com.lemon.eureka.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @author sjp
 */
@EnableEurekaServer
@SpringBootApplication
public class EurekaServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(EurekaServerApplication.class, args);
	}

}
```

##### 三、配置yml

```yml
server:
    port: 9001

eureka:
    instance:
        hostname: eureka-service
    client:
        # 不注册自己
        register-with-eureka: false
        # 获取服务
        fetch-registry: false
        # 注册中心地址
        service-url:
            defaultZone: http://localhost:${server.port}/eureka/

```

##### 四、访问

http://localhost:9001/

![1548158001871](./SpringCloud系列.assets/1548158001871.png)

#### eureka-api 入门

**Tips**:此部分属于provider和consumer公用部分，所以单独作为一个模块，打包成jar供provider和consumer使用

##### 模块结构

![1548340246457](./SpringCloud系列.assets/1548340246457.png)

##### 一、导入jar

```pom
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <artifactId>eureka-api</artifactId>

</project>

```

##### 二、建立相关类

```java
package com.lemon.soa.api;

public interface VideoService {
   /**
    * 获取视频信息
    * @param videoId 视频id
    * @return 视频信息
    */
   double getVideo(long videoId);
}
```

#### eureka-provider入门

**Tips**: Eureka本身只区分server和client(client有provider和consumer)，通过不同的配置来告知client，本身是provider还是consumer.

##### 模块结构

![1548340282069](./SpringCloud系列.assets/1548340282069.png)

##### 一、导入jar

```pom
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <artifactId>eureka-provider</artifactId>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>1.5.9.RELEASE</version>
        <relativePath/>
    </parent>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
        <java.version>1.8</java.version>
        <spring-cloud.version>Dalston.SR4</spring-cloud.version>
    </properties>

    <dependencies>

        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-eureka</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>

        <!--内部依赖-->
        <dependency>
            <groupId>com.lemon</groupId>
            <artifactId>eureka-api</artifactId>
            <version>0.0.1-SNAPSHOT</version>
        </dependency>

    </dependencies>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

</project>

```

##### 二、建立相关类

启动类：

```java
package com.lemon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class EurekaProviderApplication {

	public static void main(String[] args) {
		SpringApplication.run(EurekaProviderApplication.class, args);
	}

}
```

服务接口实现：

```java
package com.lemon.api.impl;

import org.springframework.stereotype.Service;

import com.lemon.soa.api.VideoService;

/**
 * @author sjp
 * @date 2019/1/24
 **/
@Service
public class VideoServiceImpl implements VideoService {
   @Override
   public double getVideo(long videoId) {
      return Math.random();
   }
}
```

暴露服务：

```java
package com.lemon.controller;

import com.lemon.soa.api.VideoService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class VideoController {

   @Resource
   private VideoService videoService;

   @RequestMapping(value = "/{videoId}", method = RequestMethod.GET)
   public Double getVideo(@PathVariable long videoId) {
      return videoService.getVideo(videoId);
   }
}
```

##### 三、配置yml

```yml
server:
    port: 9002

spring:
    application:
        name: eureka-provider

eureka:
    instance:
        #使用ip进行注册
        prefer-ip-address: true
    client:
        serviceUrl:
            defaultZone: http://localhost:9001/eureka/
```

##### 四、访问

http://localhost:9001/

![1548331878651](./SpringCloud系列.assets/1548331878651.png)



#### eureka-consumer入门

##### 模块结构

![1548340368984](./SpringCloud系列.assets/1548340368984.png)

##### 一、导入jar

```pom
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <artifactId>eureka-consumer</artifactId>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>1.5.12.RELEASE</version>
    </parent>

    <properties>
        <spring-cloud.version>Edgware.SR3</spring-cloud.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-eureka</artifactId>
        </dependency>
    </dependencies>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

</project>

```

##### 二、建立相关类

启动类：

```java
package com.lemon.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * @author sjp
 * @date 2019/1/24
 **/
@EnableDiscoveryClient
@SpringBootApplication
public class EurekaConsumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(EurekaConsumerApplication.class, args);
	}

	/**
	 * 启用负载均衡，默认算法是轮询
	 */
	@LoadBalanced
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}
```

调用服务：

```java
package com.lemon.consumer.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

/**
 * @author sjp
 * @date 2019/1/24
 **/
@RestController
public class ConsumerController {

   @Resource
   private RestTemplate restTemplate;

   @RequestMapping("/")
   public Double index() {
        return restTemplate.getForObject("http://eureka-provider/1", Double.class);
   }
}
```

##### 三、配置yml

```yml
server:
    port: 9003

spring:
    application:
        name: eureka-consumer
eureka:
    instance:
        # 使用IP注册
        prefer-ip-address: true
    #注册地址
    client:
        service-url:
            defaultZone: http://localhost:9001/eureka/
```

##### 四、访问

http://localhost:9001/

![1548339679969](./SpringCloud系列.assets/1548339679969.png)





### 熟悉掌握

#### eureka-server配置详解

| 参数名                        | 默认值 | 备注                                                         |
| ----------------------------- | ------ | ------------------------------------------------------------ |
| enable-self-preservation      | true   | 自我保护模式，当出现出现网络分区、eureka在短时间内丢失过多客户端时，会进入自我保护模式，即一个服务长时间没有发送心跳，eureka  也不会将其删除，默认为true |
| eviction-interval-timer-in-ms | 60000  | eureka server清理无效节点的时间间隔，默认60000毫秒，即60秒   |
| a-s-g-cache-expiry-timeout-ms | 6000  | 缓存ASG信息的到期时间，单位为毫秒，默认为10 * 60 * 1000 |
| a-s-g-query-timeout-ms | 300 | 查询AWS上ASG（自动缩放组）信息的超时值，单位为毫秒，默认为300 |
| a-s-g-update-interval-ms | 5 * 60 * 1000  | 从AWS上更新ASG信息的时间间隔，单位为毫秒 |
| a-w-s-access-id |   | 获取aws访问的id，主要用于弹性ip绑定，此配置是用于aws上的 |
| a-w-s-secret-key |  | 获取aws私有秘钥，主要用于弹性ip绑定，此配置是用于aws上的 |
| batch-replication | false | 表示集群节点之间的复制是否为了网络效率而进行批处理 |
| binding-strategy |   | 获取配置绑定EIP或Route53的策略 |
| delta-retention-timer-interval-in-ms | 30 * 1000 | 清理任务程序被唤醒的时间间隔，清理过期的增量信息，单位为毫秒 |
| disable-delta | false  | 增量信息是否可以提供给客户端看，默认为false |
| disable-delta-for-remote-regions | false  | 增量信息是否可以提供给客户端或一些远程地区 |
| disable-transparent-fallback-to-other-region | false | 如果在远程区域本地没有实例运行，对于应用程序回退的旧行为是否被禁用 |
| e-i-p-bind-rebind-retries | 3  | 获取服务器尝试绑定到候选的EIP的次数|
| e-i-p-binding-retry-interval-ms-when-unbound | 1 * 60 * 1000 | 服务器检查ip绑定的时间间隔，单位为毫秒 |
| e-i-p-binding-retry-interval-ms | 5 * 60 * 1000 | 与上面的是同一作用，仅仅是稳定状态检查 |
| enable-replicated-request-compression | false | 复制的数据在发送请求时是否被压缩 |
| g-zip-content-from-remote-region | true | eureka服务器中获取的内容是否在远程地区被压缩，默认为true |
| json-codec-name |   | 如果没有设置默认的编解码器将使用全JSON编解码器，获取的是编码器的类名称 |
| list-auto-scaling-groups-role-name | ListAutoScalingGroups | 用来描述从AWS第三账户的自动缩放组中的角色名称 |
| log-identity-headers | true | Eureka服务器是否应该登录clientAuthHeaders |
| max-elements-in-peer-replication-pool | 10000 | 复制池备份复制事件的最大数量 |
| max-elements-in-status-replication-pool: | 10000 | 可允许的状态复制池备份复制事件的最大数量 |
| max-idle-thread-age-in-minutes-for-peer-replication | 10 | 状态复制线程可以保持存活的空闲时间 |
| min-threads-for-status-replication | 1 | 被用于状态复制的线程的最小数目 |
| max-idle-thread-in-minutes-age-for-status-replication | 15 | 复制线程可以保持存活的空闲时间,默认为15分钟 |
| max-threads-for-peer-replication | 20 | 获取将被用于复制线程的最大数目 |
| max-time-for-replication | 30000 | 尝试在丢弃复制事件之前进行复制的时间，默认为30000毫秒 |
| min-threads-for-peer-replication | 5 | 获取将被用于复制线程的最小数目 |
| number-of-replication-retries | 5 | 获取集群里服务器尝试复制数据的次数 |
| peer-eureka-nodes-update-interval-ms | 10 * 60 * 1000 | 集群里eureka节点的变化信息更新的时间间隔，单位为毫秒 |
| peer-eureka-status-refresh-time-interval-ms | 30 * 1000 | 服务器节点的状态信息被更新的时间间隔，单位为毫秒 |
| peer-node-connect-timeout-ms | 200 | 连接对等节点服务器复制的超时的时间，单位为毫秒 |
| peer-node-read-timeout-ms | 200 | 读取对等节点服务器复制的超时的时间，单位为毫秒 |
| peer-node-total-connections | 1000 | 获取对等节点上http连接的总数 |
| peer-node-connection-idle-timeout-seconds | 30 | http连接被清理之后服务器的空闲时间，默认为30秒 |
| peer-node-total-connections-per-host | 500  | 获取特定的对等节点上http连接的总数 |
| prime-aws-replica-connections | true | 对集群中服务器节点的连接是否应该准备 |
| rate-limiter-enabled |   | 限流是否应启用或禁用，默认为false |
| rate-limiter-burst-size | | 速率限制的burst size ，默认为10，这里用的是令牌桶算法 |
| rate-limiter-full-fetch-average-rate | 100 | 速率限制器用的是令牌桶算法，此配置指定平均执行请求速率，默认为100 |
| rate-limiter-privileged-clients |  | 认证的客户端列表，这里是除了标准的eureka Java客户端。 |
| rate-limiter-registry-fetch-average-rate | 500 | 速率限制器用的是令牌桶算法，此配置指定平均执行注册请求速率，默认为500 |
| rate-limiter-throttle-standard-clients | false | 是否对标准客户端进行限流，默认false |
| registry-sync-retries | 5 | 当eureka服务器启动时尝试去获取集群里其他服务器上的注册信息的次数，默认为5 |
| registry-sync-retry-wait-ms | 30 * 1000 | 当eureka服务器启动时获取其他服务器的注册信息失败时，会再次尝试获取，期间需要等待的时间，默认为30 * 1000毫秒 |
| remote-region-app-whitelist |  | 必须通过远程区域中检索的应用程序的列表 |
| remote-region-connect-timeout-ms | 1000 | 连接到对等远程地eureka节点的超时时间，默认为1000毫秒 |
| remote-region-connection-idle-timeout-seconds | 30 | http连接被清理之后远程地区服务器的空闲时间，默认为30秒 |
| remote-region-fetch-thread-pool-size | 20 | 用于执行远程区域注册表请求的线程池的大小，默认为20 |
| remote-region-read-timeout-ms | 1000 | 获取从远程地区eureka节点读取信息的超时时间，默认为1000毫秒 |
| remote-region-registry-fetch-interval | 30 | 从远程区域取出该注册表的信息的时间间隔，默认为30秒 |
| remote-region-total-connections | 1000 | 获取远程地区对等节点上http连接的总数，默认为1000 |
| remote-region-total-connections-per-host | 500 | 获取远程地区特定的对等节点上http连接的总数，默认为500 |
| remote-region-trust-store |  | 用来合格请求远程区域注册表的信任存储文件，默认为空 |
| remote-region-trust-store-password |  | 获取偏远地区信任存储文件的密码，默认为“changeit” |
| remote-region-urls |  | 远程地区的URL列表 |
| remote-region-urls-with-name |  | 针对远程地区发现的网址域名的map |
| renewal-percent-threshold | 0.85 | 阈值因子，默认是0.85，如果阈值比最小值大，则自我保护模式开启 |
| renewal-threshold-update-interval-ms | 15 * 60 * 1000  | 阈值更新的时间间隔，单位为毫秒 |
| response-cache-auto-expiration-in-seconds | 180 | 当注册表信息被改变时，则其被保存在缓存中不失效的时间，默认为180秒 |
| response-cache-update-interval-ms | 30 * 1000 | 客户端的有效负载缓存应该更新的时间间隔，默认为30 * 1000毫秒 |
| retention-time-in-m-s-in-delta-queue | 3 * 60 * 1000 | 客户端保持增量信息缓存的时间，从而保证不会丢失这些信息，单位为毫秒 |
| route53-bind-rebind-retries | 3 | 服务器尝试绑定到候选Route53域的次数 |
| route53-binding-retry-interval-ms | 5 * 60 * 1000  | 服务器应该检查是否和Route53域绑定的时间间隔，默认为5 * 60 * 1000毫秒 |
| route53-domain-t-t-l | 301 | 用于建立route53域的ttl，默认为301 |
| sync-when-timestamp-differs | true | 当时间变化实例是否跟着同步，默认为true |
| use-read-only-response-cache | true | 目前采用的是二级缓存策略，一个是读写高速缓存过期策略，另一个没有过期只有只读缓存，默认为true，表示只读缓存 |
| wait-time-in-ms-when-sync-empty | 1000 * 60 * 5  | 在Eureka服务器获取不到集群里对等服务器上的实例时，需要等待的时间，单位为毫秒，默认为1000 * 60 * 5 |
| xml-codec-name |  | 如果没有设置默认的编解码器将使用xml编解码器，获取的是编码器的类名称 |



## SpringCloudFegin

官方文档：https://cloud.spring.io/spring-cloud-static/spring-cloud-openfeign/2.0.2.RELEASE/single/spring-cloud-openfeign.html

##### 一、导入jar

Springboot 2.0.0 以下

```
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-feign</artifactId>
</dependency>
```

Springboot 2.0.0 及以上

```pom
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```

##### 二、建立相关类

启动类(加上注解)：

```java
/**
  * 如果为了把eureka共有接口抽成单独模块，需注明扫描包，才可以加载jar包中的@FeignClient
  * @EnableFeignClients(basePackages = { "com.lemon.soa.api" })
  */
@EnableFeignClients
```

修改类：

```java
package com.lemon.soa.api;

// 其中eureka-provider是提供服务者的应用名，即eureka-provider的应用名
@FeignClient(name = "eureka-provider")
public interface VideoService {
   /**
    * 获取视频信息
    * @param videoId 视频id
    * @return 视频信息
    */
   @GetMapping(value = "/{videoId}")
   double getVideo(long videoId);
}
```

调用服务：

```java
package com.lemon.consumer.controller;

import com.lemon.soa.api.dto.CategoryDTO;
import com.lemon.soa.api.provider.CategoryProvider;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author sjp
 * @date 2019/1/24
 **/
@RestController
public class ConsumerController {
 
	/**
	 * feign 方式
	 */
	@Resource
	private CategoryProvider categoryProvider;

	@GetMapping(value = "/category")
	public List<CategoryDTO> getCategoryTree() {
		return categoryProvider.getCategoryTree();
	}

}

```

##### 三、配置yml

```yml
server:
    port: 9003

spring:
    application:
        name: eureka-consumer
eureka:
    instance:
        # 使用IP注册
        prefer-ip-address: true
    #注册地址
    client:
        service-url:
            defaultZone: http://localhost:9001/eureka/
```

##### 四、访问

http://localhost:9001/

![1548339679969](./SpringCloud系列.assets/1548339679969.png)

坑：

问题一：

有些公共的组件抽出来其他模块的maven依赖，此时要在使用的项目中加载此jar包的spring component以及feign组件，仅仅依靠@ComponentScan是不够的，还需要在@EnableFeignClients(basePackages = {"com.xixicat"})中标注basekPackages。

问题二：

使用@FeignClient注解，要求name不重复，否则Bean会被覆盖，或者无法启动。这是Feign的一个缺点吧。

推荐修复方案：

```yml
spring:

    # 允许重复名称的bean定义，为了解决Fegin重复命名无法启动的问题
    main:
        allow-bean-definition-overriding: true
```

其他修复方案(手动初始化Feign代理的类)：

```java
package com.lemon.consumer.controller;

import com.lemon.soa.api.dto.CategoryDTO;
import com.lemon.soa.api.dto.VideoDTO;
import com.lemon.soa.api.provider.CategoryProvider;
import com.lemon.soa.api.provider.VideoProvider;
import feign.Client;
import feign.Contract;
import feign.Feign;
import feign.Logger;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.slf4j.Slf4jLogger;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * FeginMutiNameController Fegin的一个缺陷，feginName不可以重复定义（除set allow-bean-definition-overriding: true的第二种方案）
 * @author sjp
 * @date 2019/12/15
 */
@RestController
@Import(FeignClientsConfiguration.class)
public class FeginMutiNameController {

	private VideoProvider		videoProvider;
	private CategoryProvider	categoryProvider;

	public FeginMutiNameController(Decoder decoder, Encoder encoder, Client client, Contract contract) {
		this.videoProvider = Feign.builder().client(client).encoder(encoder).decoder(decoder).contract(contract)
				// 默认是Logger.NoOpLogger
				.logger(new Slf4jLogger(VideoProvider.class))
				// 默认是Logger.Level.NONE
				.logLevel(Logger.Level.FULL).target(VideoProvider.class, "http://provider");

		this.categoryProvider = Feign.builder().client(client).encoder(encoder).decoder(decoder).contract(contract)
				// 默认是Logger.NoOpLogger
				.logger(new Slf4jLogger(CategoryProvider.class))
				// 默认是Logger.Level.NONE
				.logLevel(Logger.Level.FULL).target(CategoryProvider.class, "http://provider");
	}

	@GetMapping(value = "/test1")
	public List<CategoryDTO> getCategoryTree() {
		return categoryProvider.getCategoryTree();
	}

	@GetMapping(value = "/test2/{id}")
	public VideoDTO index(@PathVariable(value = "id") Long id) {
		return videoProvider.getVideo(id);
	}

}
```



## SpringCloud使用Config作为配置中心

### 一、介绍

​	服务端：配置中心

​	客户端：接受配置的应用

### 二、配置

#### 1、Server端配置

##### A、pom

```pom
  <properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <java.version>1.8</java.version>
  </properties>

  <parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-parent</artifactId>
    <version>1.5.13.RELEASE</version>
  </parent>

  <dependencies>
    <dependency>
      <groupId>org.springframework.cloud</groupId>
      <artifactId>spring-cloud-config-server</artifactId>
    </dependency>
  </dependencies>

  <!-- 添加spring-boot的maven插件 -->
  <build>
    <plugins>
      <plugin>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-maven-plugin</artifactId>
      </plugin>
    </plugins>
  </build>
```

##### B、yml

```yml
server:
    port: 9000
spring:
    application:
        name: lemon-config-server
    cloud:
        config:
            server:
                git:
                	# SpringCloud Config 的配置是放在git上的
                    uri: https://github.com/ATSJP/lemon-config
                    # 指定搜索根路径下的所有目录，若有多个路径使用逗号隔开
                    search-paths: /**  
```

##### C、Application

```java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * @author sjp
 */
@EnableConfigServer
@SpringBootApplication
public class LemonConfigApplication {

	public static void main(String[] args) {
		SpringApplication.run(LemonConfigApplication.class, args);
	}

}
```

全部配置好后，启动应用，访问 http://localhost:9000/admin/dev （http://localhost:9000/admin-dev.json，http://localhost:9000/admin-dev.properties）

![1561601935481](./SpringCloud系列.assets/1561601935481.png)

注：配置文件的访问规则如下：

```
/{name}-{profiles}.yml
/{label}/{name}-{profiles}.yml

name : 文件名，一般以服务名来命名
profiles : 一般作为环境标识
lable : 分支（branch），指定访问某分支下的配置文件

例如：http://localhost:9000/dv_lemon_2019_07/provider-dev.yml
```

有一点值得注意的是，如果有两个前缀名相同文件，例如一个order.yml，一个order-dev.yml。那么在访问相同前缀的文件时，config-server会对这两个文件进行一个合并。例如order.yml有一段配置是order-dev.yml没有的，理应访问order-dev.yml的时候是没有那段配置的，但访问的结果却是它俩合并之后的内容，即order-dev.yml会拥有order.yml里所配置的内容。

#### 2、Client端配置

##### A、pom

```pom
  <properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <java.version>1.8</java.version>
  </properties>

  <parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-parent</artifactId>
    <version>1.5.13.RELEASE</version>
  </parent>

  <dependencies>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <!-- 实现Config的客户端配置 -->
    <dependency>
      <groupId>org.springframework.cloud</groupId>
      <artifactId>spring-cloud-starter-config</artifactId>
    </dependency>
    <!-- 实现通过端点refresh手动刷新 -->
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
  </dependencies>
  
  <!-- 添加spring-boot的maven插件 -->
  <build>
    <plugins>
      <plugin>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-maven-plugin</artifactId>
      </plugin>
    </plugins>
  </build>
```

##### B、yml

1) bootstrap.yml

```yml
# 对应着config server所获取配置文件的{application}和URL
spring:
    application:
        name: admin
    cloud:
        config:
            uri: http://localhost:9000/
            # 对应着文件后面的后缀{profile}
            profile: dev
            # 分支
            label: master
```

2) application.yml

```yml
version-config: ${version}
```

##### C、Application

```java
@SpringBootApplication
public class LemonAdminApplication {

	public static void main(String[] args) {
		SpringApplication.run(LemonAdminApplication.class, args);
	}

}
```

##### D、Controller

```java
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RestController
public class LoginController {

    @Value("${version-config}")
    private String version;

    @GetMapping("/test")
    public String getVersion() {
        return this.version;
    }
    
}
```

### 三、踩坑

