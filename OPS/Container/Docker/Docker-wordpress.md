[TOC]

#  Docker搭建WordPress

## 一 、建立mysql

移步 [docker-zabbix](https://github.com/ATSJP/note/blob/master/Docker/Dokcer-Zabbix.md)

## 二、搭建WordPress

1、拉取镜像

```shell
docker pull wordpress:latest
```

2、启动

```shell
docker run --name demo-wordpress \
	--link <mysql的name>:mysql \
	-p 8001:80 \
	-d wordpress
```

