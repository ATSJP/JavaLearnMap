# Grafana+Prometheus+Micrometer监控JVM

## 一、前言



## 二、环境



## 三、部署

### A、SpringBoot项目集成Micrometer

飞机票：[SpringBoot监控之道](https://github.com/ATSJP/note/blob/master/SpringBoot&SpringCloud/SpringBoot监控之道.md)

 配置好之后，假设对外暴露监控地址：http://127.0.0.1:9001/actuator/prometheus

### B、Prometheus部署

飞机票：[Prometheus之修炼篇](https://github.com/ATSJP/note/blob/master/OPS/Monitor/Monitor组件/Server/Prometheus.md)

进行Prometheus配置：

```yml
global:
	scrape_interval:     5s 

scrape_configs:
    - job_name: 'java'
    metrics_path: '/actuator/prometheus'
    static_configs:
    - targets: 
    - '127.0.0.1:9001'
```

如果Prometheus配置了热更新，则使用Postman或者其他Http模拟工具：

`Post -->  http://192.168.126.129:9090/-/reload `

否则，重启Prometheus，使得配置生效。

> 检查监控是否生效，访问Prometheus的 http://{Prometheus IP}:9090/targets ，找到上方配置的Job，查看其State即可

### C、Grafana部署

飞机票：[Grafana](https://github.com/ATSJP/note/blob/master/OPS/Monitor/Monitor组件/UI/Grafana.md)

 在Grafana配置Prometheus数据源之后，导入[JVM Micrometer]( https://grafana.com/grafana/dashboards/4701 ) 监控表即可。
​		<img src="https://grafana.com/api/dashboards/4701/images/5594/image" alt="JVM Micrometer" style="padding: 5px 20px"/>

## 四、高级使用

### A、自定义监控指标

#### 1、监控Api请求次数



#### 2、监控实时在线人数



 

