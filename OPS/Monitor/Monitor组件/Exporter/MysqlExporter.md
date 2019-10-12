# MysqlExporter


目前Mysql的Exporter存在以下几种：
- <a href="https://github.com/awaragi/prometheus-mssql-exporter">MSSQL server exporter</a>
- <a href="https://github.com/rluisr/mysqlrouter_exporter">MySQL router exporter</a>
- <a href="https://github.com/prometheus/mysqld_exporter">MySQL server exporter</a> (<strong>official</strong>)

本文将从第三种，也就是官方维护的版本使用开始介绍， 其余非官方版本，各位可以直接从对应Github上去获取使用方法。

## 一、介绍

## 二、使用

基本要求，存在以下权限的Mysql用户：

```sql
CREATE USER 'exporter'@'*' IDENTIFIED BY '1234' WITH MAX_USER_CONNECTIONS 3;
GRANT PROCESS, REPLICATION CLIENT, SELECT ON *.* TO 'exporter'@'*';
```

正常部署主要有两种方式：
- Docker部署

  ```shell
  docker run -d \
    -p 9104:9104 \
    -e DATA_SOURCE_NAME="exporter:1234@(my-mysql-network:3306)/" \
    prom/mysqld-exporter
  ```

- Docker-compose部署

  ```yaml
    version: "3"
  
    services: 
      mysqld-exporter:
        image: prom/mysqld-exporter
        container_name: mysqld-exporter
        restart: always
        ports:
          - "9104:9104"
        environment:
          - DATA_SOURCE_NAME=exporter:1234(my-mysql-network:3306)/
  ```

## 三、配置



## 四、坑

















































