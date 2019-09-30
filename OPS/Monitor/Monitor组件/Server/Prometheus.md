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
  -v /root/monitor_home/prom-jvm-demo:/prometheus-config \
  prom/prometheus --web.enable-lifecycle \
  --config.file=/prometheus-config/prom-jmx.yml
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
        - /root/prometheus.yml:/etc/prometheus/prometheus.yml
  ```
  
## 二、管理API

Prometheus提供了一组管理API，以简化自动化和集成。

注意：{ip:port} 是普罗米修斯所在的IP和端口

### A、健康检查

```
GET {ip:port}/-/healthy
```

该端点始终返回200，应用于检查Prometheus的运行状况。

### B、准备检查

```
GET {ip:port}/-/ready
```

当Prometheus准备服务流量（即响应查询）时，此端点返回200。

### C、刷新

```
PUT  {ip:port}/-/reload
POST {ip:port}/-/reload
```

该端点触发Prometheus配置和规则文件的重新加载。默认情况下它是禁用的，可以通过该`--web.enable-lifecycle`标志启用。

> - docker，如下拼接命令接口
>
>   ```
>   docker run -d \
>   --name=prometheus \
>   -p 9090:9090 \
>   -v /root/monitor_home/prom-jvm-demo:/prometheus-config \
>   prom/prometheus --web.enable-lifecycle \
>   --config.file=/prometheus-config/prom-jmx.yml
>   ```
>
> - docker-compose
>
>   ```
>   services: 
>     prometheus:
>       image: prom/prometheus
>       ports:
>         - "9090:9090"
>       container_name: prometheus
>       volumes:
>         - /root/lemon_home/1.0/prometheus.yml:/etc/prometheus/prometheus.yml
>       command:
>          # 支持热更新
>          - '--web.enable-lifecycle'
>   ```

触发配置重新加载的另一种方法是将a发送`SIGHUP`给Prometheus进程。

### D、放弃

```
PUT  {ip:port}/-/quit
POST {ip:port}/-/quit
```

该端点触发Prometheus的正常关闭。默认情况下它是禁用的，可以通过该`--web.enable-lifecycle`标志启用。

触发正常关闭的另一种方法是将a发送`SIGTERM`给Prometheus进程。

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


