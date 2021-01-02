[TOC]



##  Docker之volume

> docker如何能让多个容器共享一个数据呢，又如何在容器被删除后，还能保存住容器的数据呢？
>
> 所以docker引入了卷的概念：容器中管理数据主要有两种方式：1. 数据卷 2. 数据卷容器

### 一、介绍

#### 1、卷（volume）

 “卷”是容器上的一个或多个“目录”，此类目录可绕过联合文件系统**，**与宿主机上的某个目录“绑定（关联）”，类似于挂载一样，宿主机的/data/web目录与容器中的/container/data/web目录绑定关系，然后容器中的进程向这个目录中写数据时，是直接写在宿主机的目录上的，绕过容器文件系统与宿主机的文件系统建立关联关系，使得可以在宿主机和容器内共享数据库内容，让容器直接访问宿主机中的内容，也可以宿主机向容器供集内容，两者是同步的。

在宿主机上能够被共享的目录(可以是文件)就被称为volume。

数据卷是一个可供容器使用的特殊目录，它绕过文件系统，可以提供很多有用的特性：

- 数据卷可以在容器之间共享和重用

- 对数据卷的修改会立马生效

- 对数据卷的更新，不会影响镜像

- 卷会一直存在，直到没有容器使用


#### 2、卷容器

**数据卷容器其实是一个普通的容器，专门用来提供数据卷供其它容器挂载。**

如果用户需要在容器之间共享一些持续更新的数据，最简单的方式是使用数据卷容器。

### 二、使用

#### 1、卷

```shell
docker run --name testweb -d -p 92:80 -v testwebvloume:/usr/share/nginx/html/ nginx:v3
# 利用nginx：v3镜像创建了一个名为testweb的容器，对外暴露的端口号是92，将/usr/share/nginx/html目录与数据卷testwebvloume 映射。
docker volume create volume_name #表示创建一个数据卷。
docker volume ls # 列出数据卷列表
docker volume rm volume_name # 删除指定数据卷
docker volume inspect volume_name # 查看数据卷的详细信息
例如：docker volume inspect testwebvloume
[
    {
        "CreatedAt": "2019-07-26T11:55:06+08:00",
        "Driver": "local",
        "Labels": null,
        "Mountpoint": "/var/lib/docker/volumes/testwebvloume/_data",# 表示数据卷的挂载点也就是挂载位置
        "Name": "testwebvloume",
        "Options": null,
        "Scope": "local"
    }
]
# 使用docker volume --help 帮助查看命令使用指南
Usage:  docker volume COMMAND
Manage volumes
Commands:
  create      Create a volume
  inspect     Display detailed information on one or more volumes
  ls          List volumes
  prune       Remove all unused local volumes
  rm          Remove one or more volumes
```

#### 2、数据卷容器

```shell
 docker run -v commmon:/usr/share/nginx/html/ --name commvolume nginx:v3
 #创建一个名为commvolume的容器，他的数据目录挂载到common中
 docker run -d -p 99:80 --name commweb --volumes-from  commvolume  nginx:v3
 #创建一个容器名为commweb，它的数据卷来自于commvolume 容器。
```