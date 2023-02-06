[TOC]



# Shardingsphere

> [官方文档](https://shardingsphere.apache.org/document/current/cn/overview/)
>
> ![](https://img.shields.io/github/release/apache/shardingsphere.svg?style=social&label=Release) ![](https://img.shields.io/github/stars/apache/shardingsphere.svg?style=social&label=Star) ![](https://img.shields.io/github/forks/apache/shardingsphere.svg?style=social&label=Fork) ![](https://img.shields.io/github/watchers/apache/shardingsphere.svg?style=social&label=Watch)
>
> ![](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg) ![](https://badges.gitter.im/shardingsphere/shardingsphere.svg) ![](https://img.shields.io/github/release/apache/shardingsphere.svg) ![](https://api.travis-ci.org/apache/shardingsphere.svg?branch=master&status=created) ![](https://codecov.io/gh/apache/shardingsphere/branch/master/graph/badge.svg) ![](https://api.codacy.com/project/badge/Grade/278600ed40ad48e988ab485b439abbcd) ![](https://img.shields.io/badge/OpenTracing--1.0-enabled-blue.svg) ![](https://img.shields.io/badge/Skywalking%20Tracing-enable-brightgreen.svg)

## 简介

Apache ShardingSphere 是一套开源的分布式数据库解决方案组成的生态圈，它由 JDBC、Proxy 和 Sidecar（规划中）这 3 款既能够独立部署，又支持混合部署配合使用的产品组成。 它们均提供标准化的数据水平扩展、分布式事务和分布式治理等功能，可适用于如 Java 同构、异构语言、云原生等各种多样化的应用场景。

Apache ShardingSphere 旨在充分合理地在分布式的场景下利用关系型数据库的计算和存储能力，而并非实现一个全新的关系型数据库。 关系型数据库当今依然占有巨大市场份额，是企业核心系统的基石，未来也难于撼动，我们更加注重在原有基础上提供增量，而非颠覆。

Apache ShardingSphere 5.x 版本开始致力于可插拔架构，项目的功能组件能够灵活的以可插拔的方式进行扩展。 目前，数据分片、读写分离、数据加密、影子库压测等功能，以及对 MySQL、PostgreSQL、SQLServer、Oracle 等 SQL 与协议的支持，均通过插件的方式织入项目。 开发者能够像使用积木一样定制属于自己的独特系统。Apache ShardingSphere 目前已提供数十个 SPI 作为系统的扩展点，而且仍在不断增加中。

## 功能

### 数据分片

- 分库 & 分表
- 读写分离
- 分片策略定制化
- 无中心化分布式主键

### 分布式事务

- 标准化事务接口
- XA 强一致事务
- 柔性事务

### 数据库治理

- 分布式治理
- 弹性伸缩
- 可视化链路追踪
- 数据加密