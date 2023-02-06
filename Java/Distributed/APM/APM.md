# Apm

## 概念

https://storage.googleapis.com/pub-tools-public-publication-data/pdf/36356.pdf

## 平台
### 对比

|                            | Pinpoint                                 | Skywalking                                                   | Zipkin                                               | Cat                                 |
| -------------------------- | ---------------------------------------- | ------------------------------------------------------------ | ---------------------------------------------------- | ----------------------------------- |
| 项目发起人                 | 韩国，naver团队                          | 吴晟（中国，目前在华为）                                     | Twitter                                              | 携程、大众点评团队                  |
| 系统架构图                 |                                          | ![](https://camo.githubusercontent.com/143779cb51ec9557528e9059d1386d6cbc905fb46c8c20603f2f4dc0fb2b8ab1/68747470733a2f2f736b7977616c6b696e672e6170616368652e6f72672f696d616765732f536b7957616c6b696e675f4172636869746563747572655f32303231303432342e706e673f743d3230323130343234) | ![](https://zipkin.io/public/img/architecture-1.png) |                                     |
| 定位                       | 分布式追踪系统、APM                      | 分布式追踪系统、APM                                          | 分布式追踪系统                                       | 实时应用监控平台                    |
| Github 地址                | https://github.com/pinpoint-apm/pinpoint | https://github.com/apache/skywalking                         | https://github.com/openzipkin/zipkin                 | https://github.com/dianping/cat     |
| Github Star(截止2022-1-19) | 12k                                      | 19k                                                          | 15.1k                                                | 16.3k                               |
| 社区                       | 非Apache+一般                            | Apache                                                       |                                                      |                                     |
| 兼容OpenTracing            | 否                                       | 是                                                           | 是                                                   | 否                                  |
| 支持语言                   | Java、Php                                | Java、Php、C#、NodeJs、.NET Core、Go、C++ 、LUA agent especially for Nginx、OpenResty and Apache APISIX等 |                                                      | Java, C/C++, Node.js, Python, Go 等 |
| 埋点方式                   | Java探针、字节码增强                     | Java探针、字节码增强                                         | Http拦截器                                           | 代码埋点                            |
| 埋点侵入性                 | 低                                       | 低                                                           | 中                                                   | 高                                  |
| 粒度                       | 方法                                     | 方法                                                         | 接口                                                 | 代码                                |
| 协议                       | Thrift                                   | gRpc                                                         | 消息队列、Http                                       | Netty                               |
| 存储                       | Hbase、Mysql                             | ES、H2、Mysql、TiDB、Sharding-Sphere                         | Mysql、ES、Cassandra                                 | 本地文件、HDFS、Mysql               |
| UI丰富度                   | 很高                                     | 一般                                                         |                                                      |                                     |
| 扩展性                     | 低                                       | 高                                                           |                                                      |                                     |
| TraceId查询                | 不支持                                   | 支持                                                         | 支持                                                 | 不支持                              |
| 跟踪粒度                   | 细                                       | 一般                                                         |                                                      |                                     |
| 过滤追踪                   | filter配置                               | Agent.config+Apm-trace-ignore-plugin                         |                                                      |                                     |
| 性能损耗                   | 高                                       | 低                                                           | 中                                                   | 低                                  |
| 组件                       | Collector+Web+Agent+DB                   | OAP+Web+Agent+DB+Zk                                          |                                                      |                                     |



