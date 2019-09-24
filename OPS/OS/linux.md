# Linux端口相关

## Linux如何查看端口占用，并杀掉该进程

一、查看端口占用情况

```shell
   netstat -apn|grep 11305

   显示情况：

   tcp 0 0 10.65.42.27:80 178.21.133.39：66771 ESTABLISHED 9003 / lighttpd
```

二、查看进程号详细信息

```shell
   9003即为进程号，找到进程号以后，使用如下命令查看详细信息:

   ps -aux|grep 进程号;

   bae 9003 0.0 0.2 133724 22848？ SI Mar 27 10:21 /kindee/eas/java
```

三、杀掉该进程

```shell
    kill -9 进程号;
```
## Linux命令–日志文件里面查找关键字

1、查看日志 前 n行：

```shell
cat 文件名 | head -n 数量

cat log.log | head -n 200　　# 查看log.log前200行
```

2、查看日志 尾 n行：

```shell
cat 文件名 | tail -n 数量

cat log.log | tail -n 200　　# 查看log.log后200行
```

3、根据 关键词 查看日志 并返回关键词所在行：

方法一：

```shell
cat 文件名 | grep “关键词”

cat log.log | grep “train”　　# 返回log.log中包含train的所有行
```

方法二：

```shell
grep -i “关键词” 文件名 （与方法一效果相同，写法不同）

grep -i “train” log.log　　# 返回log.log中包含train的所有行
```

## LINUX命令-YUM&APT-GET

Apt-get

1、搜索软件包

```shell
apt-cache search openjdk
```

