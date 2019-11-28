# NodeExporter

> 官方地址：[Github]( https://github.com/prometheus/node_exporter )

## 一、介绍

## 二、使用

正常部署主要有两种方式：
- Docker部署

  ```shell
  # 监控宿主
  docker run -d \
    --net="host" \
    --pid="host" \
    -v "/:/host:ro,rslave" \
    quay.io/prometheus/node-exporter \
    --path.rootfs=/host
  ```

- Docker-compose部署

  ```yaml
    version: "3"
  
    services: 
        # 监控当前容器
        node-exporter:
          image: prom/node-exporter:v0.18.0
          container_name: node-exporter
          ports:
            - "9101:9100"
  
        # 监控宿主,绑定的信息都是宿主的，所以可以访问本地的9100端口来确认是否启动成功，如需在外部访问，则需要配置iptable或关闭防火墙
        node-exporter1:
          image: prom/node-exporter:v0.18.0
          container_name: node-exporter1
          volumes:  
            - /:/host:ro,rslave
          pid: "host"
          network_mode: "host"
          command:
            ["--path.rootfs=/host"]
  
  ```

## 三、配置



## 四、坑

















































