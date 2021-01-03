[TOC]



## Docker之compose

> [官方文档](https://docs.docker.com/compose/install/)

### 一、介绍



### 二、安装及更新

#### A、安装

##### 1、官方包安装

```shell
# 若是想要不同的版本,替换 1.24.1 即可
sudo curl -L "https://github.com/docker/compose/releases/download/1.24.1/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose

sudo chmod +x /usr/local/bin/docker-compose

# 验证安装结果
docker-compose version
```

##### 2、pip安装docker-compose

```shell
sudo pip install -U docker-compose

chmod +x /usr/local/bin/docker-compose # 赋予安装目录 执行权限

docker-compose up -d
```

#### B、更新



#### C、卸载

1、官方包安装卸载方式

```shell
sudo rm /usr/local/bin/docker-compose
```

2、pip安装卸载方式

```shell
pip uninstall docker-compose
```



### 三、使用

##### 1、删除容器及其卷

```shell
docker-compose down --volumes
```

##### 2、停止容器

```
docker-compose stop
```



### 四、常用示例



### 五、坑记录