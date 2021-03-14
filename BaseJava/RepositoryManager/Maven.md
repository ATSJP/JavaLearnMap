[TOC]

# Maven

## 一、介绍

详细介绍：[点此进入](https://blog.csdn.net/qq_37604508/article/details/82814130)

## 二、使用



## 三、常见问题
**问题一、maven编译内存不足**

```text
ERROR: Maven JVM terminated unexpectedly with exit code 137
```
原因：maven运行时内存不足

解决方案：

检查机器内存是否已经占满，如果机器内存足够，尝试增大Maven运行时内存解决:

找到$MAVEN_HOME/bin/mvn(window则是mvn.bat)，找个编辑器打开，在其中加入下面这句：（具体堆的大小视虚拟机内存可以自由调整）

```shell
MAVEN_OPTS="$MAVEN_OPTS -Xms256m -Xmx512m"
```