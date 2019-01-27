# Docker安装Zabix监控

## 一、zabbix部署
### A、部署agent的server端

1、启动一个mysql的容器

```shell
docker run --name mysql-server -t \
      -e MYSQL_DATABASE="zabbix" \
      -e MYSQL_USER="zabbix" \
      -e MYSQL_PASSWORD="zabbix_pwd" \
      -e MYSQL_ROOT_PASSWORD="root_pwd" \
      -d mysql:5.7
```

2、启动Java-gateway容器监控Java服务(可选，如果不选，则在第三步不要link)

```shell
docker run --name zabbix-java-gateway -t \
      -p 10052:10052 \
      -d zabbix/zabbix-java-gateway:latest 
```

3、启动zabbix-mysql容器使用link连接mysql与java-gateway

```shell
docker run --name zabbix-server-mysql -t \
      -e DB_SERVER_HOST="mysql-server" \
      -e MYSQL_DATABASE="zabbix" \
      -e MYSQL_USER="zabbix" \
      -e MYSQL_PASSWORD="zabbix_pwd" \
      -e MYSQL_ROOT_PASSWORD="root_pwd" \
      -e ZBX_JAVAGATEWAY="zabbix-java-gateway" \
      --link mysql-server:mysql \
      --link zabbix-java-gateway:zabbix-java-gateway \
      -p 10051:10051 \
      -d zabbix/zabbix-server-mysql:latest
```

4、启动zabbix web显示，使用link连接zabbix-mysql与mysql

```shell
docker run --name zabbix-web-nginx-mysql -t \
      -e DB_SERVER_HOST="mysql-server" \
      -e MYSQL_DATABASE="zabbix" \
      -e MYSQL_USER="zabbix" \
      -e MYSQL_PASSWORD="zabbix_pwd" \
      -e MYSQL_ROOT_PASSWORD="root_pwd" \
      --link mysql-server:mysql \
      --link zabbix-server-mysql:zabbix-server \
      -p 9001:80 \
      -d zabbix/zabbix-web-nginx-mysql:latest
```

### B、部署agent的client端

1、部署agent（ZBX_SERVER_HOST为zabbix-server-mysql所在容器IP）

```shell
docker run --name zabbix-agent \
      -e ZBX_HOSTNAME="zabbix server" \
      -e ZBX_SERVER_HOST="127.0.0.1" \   
      -e ZBX_METADATA="zabbix" \
      -p 10050:10050 \
      --privileged \
      -d zabbix/zabbix-agent:latest
```

### C、部署docker的UI（选择了其中一个）

1、portainer（可选）

```shell
docker run -d -p 9000:9000 \
    --restart=always \
    -v /var/run/docker.sock:/var/run/docker.sock \
    --name prtainer-test \
    portainer/portainer
```

2、DockerUI（可选）

```shell
docker pull uifd/ui-for-docker 

docker run -it -d --name docker-web \
	-p 9000:9000 \
	-v /var/run/docker.sock:/var/run/docker.sock docker.io/uifd/ui-for-docker
```

## 二、踩坑记录

### (一） agent访问权限不足

由于博主还部署了portainer，Docker的UI管理，所以劈里啪啦很开心的打开浏览器，输入了portainer的地址，确认以上部署ok（当然也可以通过docker命令查看）

![20190105172008811](F:\文件\桌面\markdown筆記\note\Docker\assets\20190105172008811.png)

全部启动ok，输入zabbix的地址，默认账户/密码为admin/zabbix。进入配置->主机我们可以看到以下的agent。

![20190105172026972](F:\文件\桌面\markdown筆記\note\Docker\assets\20190105172026972.png)

错误信息是什么呢，我们取出来一看，如下：

```console
Received empty response from Zabbix Agent at [172.18.0.7]. Assuming that agent dropped connection because of access permissions.
```

大致的意思是不能从172.18.0.7获取，因为权限不允许，意思就是，agent在，但是不允许server去获取信息。

我们查看我们的部署agent的配置，其中有一个ZBX_SERVER_HOST，根据官网资料我们知道这是zabbix server的IP。

```shell
[root@demo ~]# docker run --name zabbix-agent \
      -e ZBX_HOSTNAME="Zabbix server" \
            -e ZBX_SERVER_HOST="127.0.0.1" \
            -e ZBX_METADATA="zabbix" \
            -p 10050:10050 \
            --privileged \
           -d zabbix/zabbix-agent:latest
```

我们猜测可能这个IP配置不对，为了验证我们的猜想，我们获取agent的实例日志

```shell
docker logs -f 58cfd8406ea9
```

![20190105172040771](F:\文件\桌面\markdown筆記\note\Docker\assets\20190105172040771.png)

如上图，的确如此 ，因为我们设置服务器IP为127.0.0.1，所以agent只允许这个IP访问，然而由于Docker容器的存在，他会给每个容器分配一个IP地址，与宿主区分，如portainer的IP一栏，我们的zabbix server IP是 172.18.0.5，所以重新部署agent

```shell
docker run --name zabbix-agent \
      -e ZBX_HOSTNAME="Zabbix server" \
            -e ZBX_SERVER_HOST="172.18.0.5" \
            -e ZBX_METADATA="zabbix" \
            -p 10050:10050 \
            --privileged \
           -d zabbix/zabbix-agent:latest
```

### (二）“Lack of free swap space”

Zabbix初始设计是大型公司用于监控服务器集群的，但日常中也用于监控VPS或云主机。后者情况下Zabbix的很多配置和属性就没有经过优化，取决于监控的对象和用途，经常需要对一些Zabbix配置进行调整。主要使用Zabbix监控一些云主机和VPS，也会经常遇到一些问题，比如之前遇到的“Lack of free swap space”问题，今天写下来和大家分享。

部分云主机（例如DigitalOcean）和VPS（一代OpenVZ）都没有设置交换分区/虚拟内存，使用free -m命令将会显示SWAP三项都为0。

![20190105172049480](F:\文件\桌面\markdown筆記\note\Docker\assets\20190105172049480.png)

这种情况下，如果开启Zabbix监控，Zabbix将会报告系统缺少交换分区空间（“Lack of free swap space”）。这完全可以理解，因为按照正常的逻辑，一台物理服务器不可能不设置交换分区。显然，这样的设计没有考虑到云主机用户，但需要适当调整监控文件配置即可解决问题。

解决此问题的步骤如下：选择Configuration–>Templates（模板），在模板界面中选择Template OS Linux(你在用的模板)的Triggers（触发器），在触发器页面中打开Lack of free swap space on {HOST.NAME}项目，在新打开的触发器编辑页面中修改Expression（表达式）的内容，由原先的

```properties
{Template OS Linux:system.swap.size[,pfree].last(0)}<50
```

修改为

```properties
{Template OS Linux:system.swap.size[,pfree].last(0)}<50 and {Template OS Linux:system.swap.size[,free].last(0)}<>0
```

此处修改增加了“ and {Template OS Linux:system.swap.size[,free].last(0)}<>0”判断系统有交换空间，当系统无交换空间

即{Template OS Linux:system.swap.size[,free].last(0)}的值为0时将不会时表达式不成立就不会触发错误提示。

保存之后在下一个更新周期内Zabbix之前报告的“Lack of free swap space”问题就会被自动标记为Resolved（已解决）

