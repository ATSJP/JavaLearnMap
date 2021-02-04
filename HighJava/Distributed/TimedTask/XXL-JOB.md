[TOC]



# XXL-JOB



> 参照文档：[官方文档](https://www.xuxueli.com/xxl-job)
>
> ![](https://maven-badges.herokuapp.com/maven-central/com.xuxueli/xxl-job/badge.svg)![](https://img.shields.io/github/release/xuxueli/xxl-job.svg) ![](https://img.shields.io/github/stars/xuxueli/xxl-job) ![](https://img.shields.io/docker/pulls/xuxueli/xxl-job-admin) ![](https://img.shields.io/badge/license-GPLv3-blue.svg)

## 介绍

​	XXL-JOB是一个分布式任务调度平台，其核心设计目标是开发迅速、学习简单、轻量级、易扩展。

## 架构图

![](https://www.xuxueli.com/doc/static/xxl-job/images/img_Qohm.png)

## 特性

- 动态：支持动态修改任务状态、启动/停止任务，以及终止运行中任务，即时生效；

- 调度中心HA（中心式）：调度采用中心式设计，“调度中心”自研调度组件并支持集群部署，可保证调度中心HA；

  > HA是Highly Available缩写，是双机集群系统简称，提高可用性集群，是保证业务连续性的有效解决方案，一般有两个或两个以上的节点，且分为活动节点及备用节点。

- 执行器HA（分布式）：任务分布式执行，任务”执行器”支持集群部署，可保证任务执行HA；

- 注册中心: 执行器会周期性自动注册任务, 调度中心将会自动发现注册的任务并触发执行。同时，也支持手动录入执行器地址；

- 弹性扩容缩容：一旦有新执行器机器上线或者下线，下次调度时将会重新分配任务；

- 触发策略：提供丰富的任务触发策略，包括：Cron触发、固定间隔触发、固定延时触发、API（事件）触发、人工触发、父子任务触发；

- 调度过期策略：调度中心错过调度时间的补偿处理策略，包括：忽略、立即补偿触发一次等；

- 阻塞处理策略：调度过于密集执行器来不及处理时的处理策略，策略包括：单机串行（默认）、丢弃后续调度、覆盖之前调度；

- 任务超时控制：支持自定义任务超时时间，任务运行超时将会主动中断任务；

- 任务失败重试：支持自定义任务失败重试次数，当任务失败时将会按照预设的失败重试次数主动进行重试；其中分片任务支持分片粒度的失败重试；

- 任务失败告警；默认提供邮件方式失败告警，同时预留扩展接口，可方便的扩展短信、钉钉等告警方式；

- 路由策略：执行器集群部署时提供丰富的路由策略，包括：第一个、最后一个、轮询、随机、一致性HASH、最不经常使用、最近最久未使用、故障转移、忙碌转移等；

- 分片广播任务：执行器集群部署时，任务路由策略选择”分片广播”情况下，一次任务调度将会广播触发集群中所有执行器执行一次任务，可根据分片参数开发分片任务；

- 动态分片：分片广播任务以执行器为维度进行分片，支持动态扩容执行器集群从而动态增加分片数量，协同进行业务处理；在进行大数据量业务操作时可显著提升任务处理能力和速度。

- 故障转移：任务路由策略选择”故障转移”情况下，如果执行器集群中某一台机器故障，将会自动Failover切换到一台正常的执行器发送调度请求。

- Rolling实时日志：支持在线查看调度结果，并且支持以Rolling方式实时查看执行器输出的完整的执行日志；

- GLUE：提供Web IDE，支持在线开发任务逻辑代码，动态发布，实时编译生效，省略部署上线的过程。支持30个版本的历史版本回溯。

- 脚本任务：支持以GLUE模式开发和运行脚本任务，包括Shell、Python、NodeJS、PHP、PowerShell等类型脚本;

- 命令行任务：原生提供通用命令行任务Handler（Bean任务，”CommandJobHandler”）；业务方只需要提供命令行即可；

- 任务依赖：支持配置子任务依赖，当父任务执行结束且执行成功后将会主动触发一次子任务的执行, 多个子任务用逗号分隔；

- 一致性：“调度中心”通过DB锁保证集群分布式调度的一致性, 一次任务调度只会触发一次执行；

- 自定义任务参数：支持在线配置调度任务入参，即时生效；

- 调度线程池：调度系统多线程触发调度运行，确保调度精确执行，不被堵塞；

- 邮件报警：任务失败时支持邮件报警，支持配置多邮件地址群发报警邮件；

- 推送maven中央仓库: 将会把最新稳定版推送到maven中央仓库, 方便用户接入和使用;

- 运行报表：支持实时查看运行数据，如任务数量、调度次数、执行器数量等；以及调度报表，如调度日期分布图，调度成功分布图等；

- 全异步：任务调度流程全异步化设计实现，如异步调度、异步运行、异步回调等，有效对密集调度进行流量削峰，理论上支持任意时长任务的运行；

- 跨语言：调度中心与执行器提供语言无关的 RESTful API 服务，第三方任意语言可据此对接调度中心或者实现执行器。除此之外，还提供了 “多任务模式”和“httpJobHandler”等其他跨语言方案；

- 线程池隔离：调度线程池进行隔离拆分，慢任务自动降级进入”Slow”线程池，避免耗尽调度线程，提高系统稳定性；

  ......

## 使用

项目地址：[Gitee](https://gitee.com/xuxueli0323/xxl-job)

### 调度中心



#### 部署

##### 单机部署

###### 初始化数据

导入SQL：

```shell
/xxl-job/doc/db/tables_xxl_job.sql
```

###### Docker部署

拉取镜像：

```shell
docker pull xuxueli/xxl-job-admin
```

运行：

```shell
docker run -e PARAMS="--spring.datasource.url=jdbc:mysql://127.0.0.1:3306/xxl_job?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&serverTimezone=Asia/Shanghai --spring.datasource.username=root --spring.datasource.password=1234" -p 8080:8080 --name xxl-job-admin -d xuxueli/xxl-job-admin:2.3.0
```

###### 源码部署

**Tips**：见官方文档。

启动成功后，访问：http://localhost:8080/xxl-job-admin (该地址执行器将会使用到，作为回调地址)

默认登录账号 “admin/123456”。 

##### 集群部署

调度中心支持集群部署，提升调度系统容灾和可用性。

调度中心集群部署时，几点要求和建议：

- DB配置保持一致；
- 集群机器时钟保持一致（单机集群忽视）；
- 建议：推荐通过nginx为调度中心集群做负载均衡，分配域名。调度中心访问、执行器回调配置、调用API服务等操作均通过该域名进行。

**Tips**：官方暂未提供集群部署示例。

### 执行器项目

#### 开发

##### BEAN模式（类形式）

Bean模式任务，支持基于类的开发方式，每个任务对应一个Java类。

- 优点：不限制项目环境，兼容性好。即使是无框架项目，如main方法直接启动的项目也可以提供支持，可以参考示例项目 “xxl-job-executor-sample-frameless”；
- 缺点：
  - 每个任务需要占用一个Java类，造成类的浪费；
  - 不支持自动扫描任务并注入到执行器容器，需要手动注入。

```java
1、开发一个继承自"com.xxl.job.core.handler.IJobHandler"的JobHandler类，实现其中任务方法。
2、手动通过如下方式注入到执行器容器。
​```
XxlJobExecutor.registJobHandler("demoJobHandler", new DemoJobHandler());
​```
```

##### BEAN模式（方法形式）

Bean模式任务，支持基于方法的开发方式，每个任务对应一个方法。

- 优点：
  - 每个任务只需要开发一个方法，并添加”[@XxlJob](https://github.com/XxlJob)”注解即可，更加方便、快速。
  - 支持自动扫描任务并注入到执行器容器。
- 缺点：要求Spring容器环境；

```java
// 可参考Sample示例执行器中的 "com.xxl.job.executor.service.jobhandler.SampleXxlJob" ，如下：
@XxlJob(value="demoJobHandler", init = "initDemoJobHandler", destroy = "destroyDemoJobHandler")
public void demoJobHandler() throws Exception {
    XxlJobHelper.log("XXL-JOB, Hello World.");
}

public void initDemoJobHandler(){}

public void destroyDemoJobHandler(){}
```

#### 部署

##### 单机部署

**Tips**：见官方文档。

##### 集群部署

执行器支持集群部署，提升调度系统可用性，同时提升任务处理能力。

执行器集群部署时，几点要求和建议：

- 执行器回调地址（xxl.job.admin.addresses）需要保持一致；执行器根据该配置进行执行器自动注册等操作。
- 同一个执行器集群内AppName（xxl.job.executor.appname）需要保持一致；调度中心根据该配置动态发现不同集群的在线执行器列表。

**Tips**：官方暂未提供集群部署示例。

### GLUE模式

简单来说，就是在线编写代码、配置，实时发布。

**Tips**：见官方文档。





