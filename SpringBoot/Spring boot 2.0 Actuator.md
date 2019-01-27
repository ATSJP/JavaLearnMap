# Spring boot 2.0 Actuator 的健康检查

> spring boot 框架是spring framework发展史上一次质的飞跃，用过都说好。它不仅仅是简化了繁琐的配置文件，提高了开发效率，整合了开发中常用的各种组件，优雅地处理了它们之间的版本兼容性问题，等等。除了以上这些优点还有本文将重点介绍的监控，Spring boot框架自带全方位的监控，这样，做spring boot应用的监控简直是太方便了。

## 一、 前言

**官方文档** ：https://docs.spring.io/spring-boot/docs/2.0.1.RELEASE/reference/htmlsingle/#production-ready-health

在当下流行的Service Mesh架构中，由于Spring boot框架的种种优点，它特别适合作为其中的应用开发框架。说到Service Mesh的微服务架构，主要特点是将服务开发和服务治理分离开来，然后再结合容器化的Paas平台，将它们融合起来，这依赖的都是互相之间默契的配合。也就是说各自都暴露出标准的接口，可以通过这些接口互相交织在一起。Service Mesh的架构设计中的要点之一，就是**全方位的监控**，因此一般我们选用的服务开发框架都需要有方便又强大的监控功能支持。在Spring boot应用中开启监控特别方便，监控面也很广，还支持灵活定制。

## 二、 Actuator的使用方法

在Spring boot应用中，要实现可监控的功能，依赖的是 `spring-boot-starter-actuator` 这个组件。它提供了很多监控和管理你的spring boot应用的HTTP或者JMX端点，并且你可以有选择地开启和关闭部分功能。当你的spring boot应用中引入下面的依赖之后，将自动的拥有审计、健康检查、Metrics监控功能。

```pom
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

具体的使用方法：

1. 引入上述的依赖jar；
2. 通过下面的配置启用所有的监控端点，默认情况下，这些端点是禁用的；

```yml
management:
  endpoints:
    web:
      exposure:
        include: "*"
```

“*”号代表启用所有的监控端点，可以单独启用，例如，`health`，`info`，`metrics`等。（http://localhost:9001/actuator/，可查看全部）

1. 通过`actuator/+端点名`就可以获取相应的信息。

一般的监控管理端点的配置信息，如下：

```yml
management:
  endpoints:
    web:
      exposure:
        include: "*"
  server:
    port: 10111
    servlet:
      context-path: /
    ssl:
      enabled: false
  endpoint:
    health:
      show-details: always
```

上述配置信息仅供参考，具体须参照官方文档，由于spring boot的版本更新比较快，配置方式可能有变化。

## 三、 健康检查

今天重点说一下Actuator监控管理中的健康检查功能，随时能掌握线上应用的健康状况是非常重要的，尤其是现在流行的容器云平台下的应用，它们的自动恢复和扩容都依赖健康检查功能。

当我们开启`health`的健康端点时，我们能够查到应用健康信息是一个汇总的信息，访问`http://127.0.0.1:10111/actuator/health`时，我们获取到的信息是`{"status":"UP"}`，status的值还有可能是 DOWN。

要想查看详细的应用健康信息需要配置`management.endpoint.health.show-details` 的值为`always`，配置之后我们再次访问`http://127.0.0.1:10111/actuator/health`，获取的信息如下：

```json
{
    "status": "UP",
    "details": {
        "diskSpace": {
            "status": "UP",
            "details": {
                "total": 250685575168,
                "free": 172252426240,
                "threshold": 10485760
            }
        },
        "redis": {
            "status": "UP",
            "details": {
                "version": "3.2.11"
            }
        },
        "db": {
            "status": "UP",
            "details": {
                "database": "Oracle",
                "hello": "Hello"
            }
        }
    }
}
```

从上面的应用的详细健康信息发现，健康信息包含磁盘空间、redis、DB，启用监控的这个spring boot应用确实是连接了redis和oracle DB，actuator就自动给监控起来了，确实是很方便、很有用。

经过测试发现，details中所有的监控项中的任何一个健康状态是`DOWN`，整体应用的健康状态也是`DOWN`。

> `management.endpoint.health.show-details`的值除了`always`之外还有`when-authorized`、`never`，默认值是`never`。

## 四、 健康检查的原理

Spring boot的健康信息都是从`ApplicationContext`中的各种[`HealthIndicator`](https://link.jianshu.com?t=https%3A%2F%2Fgithub.com%2Fspring-projects%2Fspring-boot%2Ftree%2Fv2.0.1.RELEASE%2Fspring-boot-project%2Fspring-boot-actuator%2Fsrc%2Fmain%2Fjava%2Forg%2Fspringframework%2Fboot%2Factuate%2Fhealth%2FHealthIndicator.java)
 Beans中收集到的，Spring boot框架中包含了大量的`HealthIndicators`的实现类，当然你也可以实现自己认为的健康状态。

默认情况下，最终的spring boot应用的状态是由`HealthAggregator`汇总而成的，汇总的算法是：

1. 设置状态码顺序：`setStatusOrder(Status.DOWN, Status.OUT_OF_SERVICE, Status.UP, Status.UNKNOWN);`。
2. 过滤掉不能识别的状态码。
3. 如果无任何状态码，整个spring boot应用的状态是 `UNKNOWN`。
4. 将所有收集到的状态码按照 1 中的顺序排序。
5. 返回有序状态码序列中的第一个状态码，作为整个spring boot应用的状态。

> 源代码请参见：`org.springframework.boot.actuate.health.OrderedHealthAggregator`。

Spring boot框架自带的 `HealthIndicators` 目前包括：

| Name                                                         | Description                                               |
| ------------------------------------------------------------ | --------------------------------------------------------- |
| [`CassandraHealthIndicator`](https://link.jianshu.com?t=https%3A%2F%2Fgithub.com%2Fspring-projects%2Fspring-boot%2Ftree%2Fv2.0.1.RELEASE%2Fspring-boot-project%2Fspring-boot-actuator%2Fsrc%2Fmain%2Fjava%2Forg%2Fspringframework%2Fboot%2Factuate%2Fcassandra%2FCassandraHealthIndicator.java) | Checks that a Cassandra database is up.                   |
| [`DiskSpaceHealthIndicator`](https://link.jianshu.com?t=https%3A%2F%2Fgithub.com%2Fspring-projects%2Fspring-boot%2Ftree%2Fv2.0.1.RELEASE%2Fspring-boot-project%2Fspring-boot-actuator%2Fsrc%2Fmain%2Fjava%2Forg%2Fspringframework%2Fboot%2Factuate%2Fsystem%2FDiskSpaceHealthIndicator.java) | Checks for low disk space.                                |
| [`DataSourceHealthIndicator`](https://link.jianshu.com?t=https%3A%2F%2Fgithub.com%2Fspring-projects%2Fspring-boot%2Ftree%2Fv2.0.1.RELEASE%2Fspring-boot-project%2Fspring-boot-actuator%2Fsrc%2Fmain%2Fjava%2Forg%2Fspringframework%2Fboot%2Factuate%2Fjdbc%2FDataSourceHealthIndicator.java) | Checks that a connection to `DataSource` can be obtained. |
| [`ElasticsearchHealthIndicator`](https://link.jianshu.com?t=https%3A%2F%2Fgithub.com%2Fspring-projects%2Fspring-boot%2Ftree%2Fv2.0.1.RELEASE%2Fspring-boot-project%2Fspring-boot-actuator%2Fsrc%2Fmain%2Fjava%2Forg%2Fspringframework%2Fboot%2Factuate%2Felasticsearch%2FElasticsearchHealthIndicator.java) | Checks that an Elasticsearch cluster is up.               |
| [`InfluxDbHealthIndicator`](https://link.jianshu.com?t=https%3A%2F%2Fgithub.com%2Fspring-projects%2Fspring-boot%2Ftree%2Fv2.0.1.RELEASE%2Fspring-boot-project%2Fspring-boot-actuator%2Fsrc%2Fmain%2Fjava%2Forg%2Fspringframework%2Fboot%2Factuate%2Finflux%2FInfluxDbHealthIndicator.java) | Checks that an InfluxDB server is up.                     |
| [`JmsHealthIndicator`](https://link.jianshu.com?t=https%3A%2F%2Fgithub.com%2Fspring-projects%2Fspring-boot%2Ftree%2Fv2.0.1.RELEASE%2Fspring-boot-project%2Fspring-boot-actuator%2Fsrc%2Fmain%2Fjava%2Forg%2Fspringframework%2Fboot%2Factuate%2Fjms%2FJmsHealthIndicator.java) | Checks that a JMS broker is up.                           |
| [`MailHealthIndicator`](https://link.jianshu.com?t=https%3A%2F%2Fgithub.com%2Fspring-projects%2Fspring-boot%2Ftree%2Fv2.0.1.RELEASE%2Fspring-boot-project%2Fspring-boot-actuator%2Fsrc%2Fmain%2Fjava%2Forg%2Fspringframework%2Fboot%2Factuate%2Fmail%2FMailHealthIndicator.java) | Checks that a mail server is up.                          |
| [`MongoHealthIndicator`](https://link.jianshu.com?t=https%3A%2F%2Fgithub.com%2Fspring-projects%2Fspring-boot%2Ftree%2Fv2.0.1.RELEASE%2Fspring-boot-project%2Fspring-boot-actuator%2Fsrc%2Fmain%2Fjava%2Forg%2Fspringframework%2Fboot%2Factuate%2Fmongo%2FMongoHealthIndicator.java) | Checks that a Mongo database is up.                       |
| [`Neo4jHealthIndicator`](https://link.jianshu.com?t=https%3A%2F%2Fgithub.com%2Fspring-projects%2Fspring-boot%2Ftree%2Fv2.0.1.RELEASE%2Fspring-boot-project%2Fspring-boot-actuator%2Fsrc%2Fmain%2Fjava%2Forg%2Fspringframework%2Fboot%2Factuate%2Fneo4j%2FNeo4jHealthIndicator.java) | Checks that a Neo4j server is up.                         |
| [`RabbitHealthIndicator`](https://link.jianshu.com?t=https%3A%2F%2Fgithub.com%2Fspring-projects%2Fspring-boot%2Ftree%2Fv2.0.1.RELEASE%2Fspring-boot-project%2Fspring-boot-actuator%2Fsrc%2Fmain%2Fjava%2Forg%2Fspringframework%2Fboot%2Factuate%2Famqp%2FRabbitHealthIndicator.java) | Checks that a Rabbit server is up.                        |
| [`RedisHealthIndicator`](https://link.jianshu.com?t=https%3A%2F%2Fgithub.com%2Fspring-projects%2Fspring-boot%2Ftree%2Fv2.0.1.RELEASE%2Fspring-boot-project%2Fspring-boot-actuator%2Fsrc%2Fmain%2Fjava%2Forg%2Fspringframework%2Fboot%2Factuate%2Fredis%2FRedisHealthIndicator.java) | Checks that a Redis server is up.                         |
| [`SolrHealthIndicator`](https://link.jianshu.com?t=https%3A%2F%2Fgithub.com%2Fspring-projects%2Fspring-boot%2Ftree%2Fv2.0.1.RELEASE%2Fspring-boot-project%2Fspring-boot-actuator%2Fsrc%2Fmain%2Fjava%2Forg%2Fspringframework%2Fboot%2Factuate%2Fsolr%2FSolrHealthIndicator.java) | Checks that a Solr server is up.                          |

> 你可以通过`management.health.defaults.enabled`这个配置项将它们全部禁用掉，也可以通过`management.health.xxxx.enabled`将其中任意一个禁用掉。

## 五、 自定义健康检查

有时候需要提供自定义的健康状态检查信息，你可以通过实现`HealthIndicator`的接口来实现，并将该实现类注册为spring bean。你需要实现其中的`health()`方法，并返回自定义的健康状态响应信息，该响应信息应该包括一个状态码和要展示详细信息。例如，下面就是一个接口`HealthIndicator`的实现类：

```java
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class MyHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        int errorCode = check(); // perform some specific health check
        if (errorCode != 0) {
            return Health.down().withDetail("Error Code", errorCode).build();
        }
        return Health.up().build();
    }

}
```

另外，除了Spring boot定义的几个状态类型，我们也可以自定义状态类型，用来表示一个新的系统状态。在这种情况下，你还需要实现接口 [`HealthAggregator`](https://link.jianshu.com?t=https%3A%2F%2Fgithub.com%2Fspring-projects%2Fspring-boot%2Ftree%2Fv2.0.1.RELEASE%2Fspring-boot-project%2Fspring-boot-actuator%2Fsrc%2Fmain%2Fjava%2Forg%2Fspringframework%2Fboot%2Factuate%2Fhealth%2FHealthAggregator.java) ，或者通过配置 `management.health.status.order` 来继续使用`HealthAggregator`的默认实现。

例如，在你自定义的健康检查`HealthIndicator`的实现类中，使用了自定义的状态类型`FATAL`，为了配置该状态类型的严重程度，你需要在application的配置文件中添加如下配置：

```properties
management.health.status.order=FATAL, DOWN, OUT_OF_SERVICE, UNKNOWN, UP
```

在做健康检查时，响应中的HTTP状态码反应了整体的健康状态，（例如，`UP` 对应 200, 而 `OUT_OF_SERVICE` 和 `DOWN` 对应 503）。同样，你也需要为自定义的状态类型设置对应的HTTP状态码，例如，下面的配置可以将 `FATAL` 映射为 503（服务不可用）：

```properties
management.health.status.http-mapping.FATAL=503
```

> 如果你需要更多的控制，你可以定义自己的 `HealthStatusHttpMapper` bean。

下面是内置健康状态类型对应的HTTP状态码列表：

| Status         | Mapping                                      |
| -------------- | -------------------------------------------- |
| DOWN           | SERVICE_UNAVAILABLE (503)                    |
| OUT_OF_SERVICE | SERVICE_UNAVAILABLE (503)                    |
| UP             | No mapping by default, so http status is 200 |
| UNKNOWN        | No mapping by default, so http status is 200 |

## 六、 问题

1、面对暴露的监控接口，如何做才更安全？

方案一、修改原始的访问路径(在Spring Cloud Eureka环境中)

2.0前版本修改

```properties
endpoints.info.path=/appInfo
endpoint.health.path=/checkHealth
eureka.instance.statusPageUrlPath=/${endpoints.info.path}
eureka.instance.healthCheckUrlPath=/${endpoint.health.path}
```

2.0版本以后

```yml
management:
    endpoints:
        web:
            basePath: /appInfo
```

方案二、Spring Cloud Security或者采用Shiro控制

方案三、监控另配端口，端口对内提供访问

