## Docker

### 一、介绍

```shell
# 
sudo yum remove docker \
                  docker-client \
                  docker-client-latest \
                  docker-common \
                  docker-latest \
                  docker-latest-logrotate \
                  docker-logrotate \
                  docker-selinux \
                  docker-engine-selinux \
                  docker-engine
#                 
sudo yum install -y yum-utils device-mapper-persistent-data lvm2
# 
sudo yum-config-manager --add-repo http://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo
# 
sudo yum makecache fast
# 
sudo yum -y install docker-ce
# 启动
sudo systemctl start docker
```




### 二、安装

#### 1、pip安装docker-compose

```shell
sudo pip install -U docker-compose

chmod +x /usr/local/bin/docker-compose # 赋予安装目录 执行权限

docker-compose up -d
```

### 三、使用

#### 1、cp文件

```shell
 docker cp <实例名>:实例的文件 <宿主的文件路径>
```

#### 2、实例相关

#### 3、启动、停止实例

```shell
docker start/stop <实例id>
```

#### 4、删除实例

```shell
docker rm <实例id>
```

**5、参数**

```shell
1. --restart
       no -  容器退出时，不重启容器；
       on-failure - 只有在非0状态退出时才从新启动容器；
       always - 无论退出状态是如何，都重启容器；
       
```



### 四、常用示例

#### 1、部署mysql

```shell
sudo docker run --name=mysql -it -p 3306:3306 -e MYSQL_ROOT_PASSWORD=emc123123 -d mysql

docker exec -it mysql bash
```

#### 2、删除所有的镜像和实例(container)
```shell
#!/bin/bash # Delete all containers 
docker rm $(docker ps -a -q)
# Delete all images
docker rmi $(docker images -q)
```

#### 3、查看容器日志

```shell
docker logs -f -t –since=”2018-09-10” –tail=10 f9e29e8455a5

–since : 此参数指定了输出日志开始日期，即只输出指定日期之后的日志。

-f : 查看实时日志

-t : 查看日志产生的日期

-tail=10 : 查看最后的10条日志。
```

#### 4、进入容器

```shell
docker exec -it <容器> /bin/bash
```

#### 5、如何修改容器端口

```shell
1. docker ps 查看 Container 的 id

2. docker stop {container_id}

3.找到 /var/lib/docker/containers/{container_id}/hostconfig.json 修改

4. sudo service docker restart  重启docker

5. docker start {container_id} 重新启动 container

```

#### 6、容器运行时，添加/删除端口映射规则

```shell
a, 获取容器ip  
    docker inspect $container_name | grep IPAddress
b. 添加转发规则  
    iptables -t nat -A DOCKER -p tcp --dport $host_port -j DNAT --to-destination $docker_ip:$docker_port  
```


```shell
a. 获取规则编号 
    iptables -t nat -nL --line-number 
b. 根据编号删除规则 
    iptables -t nat -D DOCKER $num
```

### 五、坑记录 

#### 1、部分image无法删除
```shell
REPOSITORY                                             TAG                 IMAGE ID            CREATED             SIZE
index-dev.qiniu.io/cs-kirk/nginx                       latest              e9d3f7300f03        6 months ago        124.8 MB
index-dev.qiniu.io/index.qbox.me/qcos-test/msfngix     latest              e9d3f7300f03        6 months ago        124.8 MB
index-dev.qiniu.io/index.qiniu.com/qcos-test/msfngix   latest              e9d3f7300f03        6 months ago        124.8 MB
```

#### 2、删除容器报错

```shell
mashaofang:Documents shaofangma$ docker rmi e9d3f7300f03
Error response from daemon: conflict: unable to delete e9d3f7300f03 (must be forced) - image is referenced in one or more repositories
```

##### 解决方法

##### A、删除tag

docker rmi index-dev.qiniu.io/cs-kirk/nginx:latest


##### B、强行删除

docker rmi -f e9d3f7300f03        


#### 3、-p参数
```shell
PORTS
0.0.0.0:32769->5000/tcp

docker的-p命令  5000:32769  docker端口->宿主端口
```

#### 4、有时候获取镜像很慢
```shell
curl -sSL https://get.daocloud.io/daotools/set_mirror.sh | sh -s http://2a22c3ad.m.daocloud.io

systemctl restart docker #重启docker
```

### 六、扩展

#### 1、管理docker的一些UI（https://blog.csdn.net/qq273681448/article/details/75007828/）

##### A、Portainer
```shell
docker run -d -p 9000:9000 portainer/portainer
```