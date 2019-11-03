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
        # 监控容器本身
        node-exporter:
          image: prom/node-exporter:v0.18.0
          container_name: node-exporter
          restart: always
          ports:
            - "9101:9100"
          networks:
            lemon_net:
              ipv4_address: 172.100.0.18
              
        # 监控宿主，绑定宿主的9100端口，对外服务            
        node-exporter1:
          image: prom/node-exporter:v0.18.0
          container_name: node-exporter1
          restart: always
          volumes:  
            - /:/host:ro,rslave
          pid: "host"
          network_mode: "host"
          command:
            ["--path.rootfs=/host"]
  ```

## 三、配置



## 四、坑

















































