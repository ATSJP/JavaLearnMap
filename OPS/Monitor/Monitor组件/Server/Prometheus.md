[TOC]


# Prometheus之修炼篇

> 官方文档：[https://prometheus.io](https://prometheus.io/)
>
> 中文文档：
>
> - 非官方：https://songjiayang.gitbooks.io/prometheus/content/ 

## 一、入门
### A、配置

新建配置文件prometheus.yml：

```yml
scrape_configs:
  - job_name: 'test'
    # 拉取时间间隔
    scrape_interval: 30s
    # 拉取超时时间
    # scrape_timeout: 60s
    static_configs:
    - targets:
      - '127.0.0.1:9090' 
```
### B、部署

- 源码包部署

  各自参考官网部署方式即可。

- docker部署

  ```shell
  docker run -d \
             --name=prometheus \
             -p 9090:9090 \
             -v /root/monitor_home/prometheus.yml:/config/prometheus.yml \
             prom/prometheus --config.file=/config/prometheus.yml
  ```
- docker部署（保存监控数据）

  ```shell
  docker run -d \
             --name=prometheus \
             -p 9090:9090 \
             -u $(id -u):$(id -g) \
             -v /root/monitor_home/prometheus.yml:/config/prometheus.yml \
             -v /root/monitor_home/data:/prometheus \
             prom/prometheus --config.file=/config/prometheus.yml
  ```
  
- docker-compose部署

  ```yml
  version: "3"
  services: 
    # 监控
    prometheus:
      image: prom/prometheus
      ports:
        - "9090:9090"  
      restart: always 
      container_name: prometheus
      volumes:  
        - /root/monitor_home/prometheus.yml:/etc/prometheus/prometheus.yml
  ```
  
- docker-compose部署（保存监控数据）
  
  
  ```yml
  version: "3"
  services: 
    # 监控
    prometheus:
      image: prom/prometheus
      ports:
        - "9090:9090"  
      restart: always 
      container_name: prometheus
      volumes:  
        - /root/prometheus.yml:/etc/prometheus/prometheus.yml
        - /root/monitor_home:/prometheus
      # 以root运行一个容器，不是一个好的解决方案      
      user: 'root'
  ```
  
## 二、使用

### A、管理API

Prometheus提供了一组管理API，以简化自动化和集成。

注意：{ip:port} 是普罗米修斯所在的IP和端口

#### 1、健康检查

```
GET {ip:port}/-/healthy
```

该端点始终返回200，应用于检查Prometheus的运行状况。

#### 2、准备检查

```
GET {ip:port}/-/ready
```

当Prometheus准备服务流量（即响应查询）时，此端点返回200。

#### 3、刷新

```
PUT  {ip:port}/-/reload
POST {ip:port}/-/reload
```

该端点触发Prometheus配置和规则文件的重新加载。默认情况下它是禁用的，可以通过该`--web.enable-lifecycle`标志启用。

> - docker，如下拼接命令接口
>
> ```shell
> docker run -d \
>           --name=prometheus \
>           -p 9090:9090 \
>           -v /root/monitor_home/prometheus.yml:/prometheus-config/prometheus.yml \
>           prom/prometheus --web.enable-lifecycle \
>           --config.file=/prometheus-config/prometheus.yml
> ```
>
> - docker-compose
>
> ```yml
> services: 
>  prometheus:
>    image: prom/prometheus
>    ports:
>         - "9090:9090"
>    container_name: prometheus
>    volumes:
>         - /root/monitor_home/prometheus.yml:/prometheus-config/prometheus.yml
>    command:
>       [ "--config.file=/prometheus-config/prometheus.yml",
>         "--web.enable-lifecycle"
>       ]
> ```

触发配置重新加载的另一种方法是将a发送`SIGHUP`给Prometheus进程。

#### 4、放弃

```
PUT  {ip:port}/-/quit
POST {ip:port}/-/quit
```

该端点触发Prometheus的正常关闭。默认情况下它是禁用的，可以通过该`--web.enable-lifecycle`标志启用。

触发正常关闭的另一种方法是将a发送`SIGTERM`给Prometheus进程。

### B、储存

以下是经常使用到的储存配置：

- `--storage.tsdb.path`: Prometheus监控的数据存放点. 默认`data/`.
- `--storage.tsdb.retention.time`: 数据保存最长时间. 默认 `15d`. 此配置会覆盖掉`storage.tsdb.retention`(`storage.tsdb.retention`是过时配置) .
- `--storage.tsdb.retention.size`: [EXPERIMENTAL] This determines the maximum number of bytes that storage blocks can use (note that this does not include the WAL size, which can be substantial). The oldest data will be removed first. Defaults to `0` or disabled. This flag is experimental and can be changed in future releases. Units supported: KB, MB, GB, PB. Ex: "512MB"
- `--storage.tsdb.wal-compression`: 配置是否打开WAL的压缩功能。你可以期望WAL的大小减少一半，而只增加很少的cpu负载。请注意，如果您启用了这个标记，并随后将Prometheus降级到2.11.0以下的版本，您将需要删除WAL，因为它将无法读取。.

## N、附件

```properties
global:
  # 默认情况下抓取目标的频率.
  [ scrape_interval: <duration> | default = 1m ]

  # 抓取超时时间.
  [ scrape_timeout: <duration> | default = 10s ]

  # 评估规则的频率.
  [ evaluation_interval: <duration> | default = 1m ]

  # 与外部系统通信时添加到任何时间序列或警报的标签
  #（联合，远程存储，Alertma# nager）.
  external_labels:
    [ <labelname>: <labelvalue> ... ]

# 规则文件指定了一个globs列表. 
# 从所有匹配的文件中读取规则和警报.
rule_files:
  [ - <filepath_glob> ... ]

# 抓取配置列表.
scrape_configs:
  [ - <scrape_config> ... ]

# 警报指定与Alertmanager相关的设置.
alerting:
  alert_relabel_configs:
    [ - <relabel_config> ... ]
  alertmanagers:
    [ - <alertmanager_config> ... ]

# 与远程写入功能相关的设置.
remote_write:
  [ - <remote_write> ... ]

# 与远程读取功能相关的设置.
remote_read:
  [ - <remote_read> ... ]
```


