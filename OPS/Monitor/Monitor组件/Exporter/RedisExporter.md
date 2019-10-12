# RedisExporter


目前Redis的Exporter存在以下几种：
- 



## 一、介绍

## 二、使用

正常部署主要有两种方式：
- Docker部署

  ```shell
  docker run -d \
  --name redis_exporter \
  -p 9121:9121 \
  oliver006/redis_exporter --redis.addr:redis://47.98.164.213:6379
  ```

- Docker-compose部署

  ```yaml
    version: "3"
  
    services: 
      redis-exporter:
        image: oliver006/redis_exporter
        container_name: redis-exporter
        restart: always
        ports:
          - "9121:9121"
        environment:
          - '--redis.addr:redis://47.98.164.213:6379'
          # - '--redis.password:'
  
  ```

## 三、配置



## 四、坑

















































