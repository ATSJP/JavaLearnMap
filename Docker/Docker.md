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
2.-v /usr/local/centos:/usr/local/centos:ro  
      挂载宿主目录到容器
```



### 四、常用示例

#### 1、部署mysql

```shell
sudo docker run --name=mysql -it -p 3306:3306 -e MYSQL_ROOT_PASSWORD=1234 -d mysql:5.7 --lower_case_table_names=1

docker exec -it mysql bash


--restart=always 跟随docker启动
--privileged=true 容器root用户享有主机root用户权限
-v 映射主机路径到容器
-e MYSQL_ROOT_PASSWORD=root 设置root用户密码
-d 后台启动
--lower_case_table_names=1 设置表名参数名等忽略大小写
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

docker的-p命令  5000:32769  宿主端口->docker端口
```

#### 4、有时候获取镜像很慢

a.

```shell
curl -sSL https://get.daocloud.io/daotools/set_mirror.sh | sh -s http://2a22c3ad.m.daocloud.io

systemctl restart docker #重启docker
```

b.

vim /etc/docker/daemon.json

```shell
{
"registry-mirrors":["https://registry.docker-cn.com"]
}
```

```shell
systemctl daemon-reload
systemctl restart docker
```



### 六、扩展

#### 1、管理docker的一些UI及其他（https://blog.csdn.net/qq273681448/article/details/75007828/）

##### A、Portainer

```shell
docker run -d -p 9000:9000 -v /var/run/docker.sock:/var/run/docker.sock \
   -v /usr/local/portainer:/usr/local/portainer \
   --name portainer --restart=always \
   portainer/portainer
```

##### B、docker部署centos

```shell
docker run -itd --name centos7.2 --privileged -v /usr/local/centos:/usr/local/centos:ro centos7.2
```

##### C、redis

```shell
docker run -p 6379:6379 -d redis:latest redis-server
```

##### D、gitlab

```shell
# 1
docker run --detach \
--hostname gitlab.shijianpeng.top \
--publish 9443:443 --publish 9003:80 --publish 22:22 \
--name gitlab \
--restart always \
--volume /root/gitlab_home/config:/etc/gitlab \
--volume /root/gitlab_home/logs:/var/log/gitlab \
--volume /root/gitlab_home/data:/var/opt/gitlab \
gitlab/gitlab-ce:latest

# 2 
docker run --detach \
--publish 9443:443 --publish 9003:80 --publish 9022:22 \
--name gitlab \
--restart always \
--volume /root/gitlab_home/config:/etc/gitlab \
--volume /root/gitlab_home/logs:/var/log/gitlab \
--volume /root/gitlab_home/data:/var/opt/gitlab \
gitlab/gitlab-ce:latest
```

##### E、docker部署jenkins

```shell
镜像： jenkins/jenkins 
      jenkinsci/blueocean

docker run \
    -d \
    --name jenkins \
    -p 9002:8080 \
    -v /usr/local/jenkins:/usr/local/jenkins \
    jenkins/jenkins   

# 临时启动一个jenkins容器，停止后自动清除 --rm
docker run \
  --rm \
  --name jenkins \
  -u root \
  -p 9001:8080 \
  -v jenkins-data:/var/jenkins_home \
  -v /var/run/docker.sock:/var/run/docker.sock \
  -v "$HOME":/home \
  jenkinsci/blueocean
 
# 启动jenkins -d 后台运行 
docker run \
  -d \
  --name jenkins \
  -u root \
  -p 9001:8080 \
  -v jenkins-data:/var/jenkins_home \
  -v /var/run/docker.sock:/var/run/docker.sock \
  -v "$HOME":/home \
  jenkinsci/blueocean
```

##### F、docker部署node.js

```shell

```

##### G、yml

```yml
version: "2"
services:

    mysql:
      image: mysql/mysql-server:5.7.21
      ports:
        - "3306:3306"
      environment:
        MYSQL_ROOT_PASSWORD: "zxcv1234"
        MYSQL_ROOT_HOST: "%"
        TZ: Asia/Shanghai
      volumes:
        - "/home/mac/docker/volumes/mysql-5.7.21/datadiri:/var/lib/mysql"
      restart: always
      container_name: docker_mysql

    tomcat:
      image: dordoka/tomcat
      ports:
        - "9002:8080"
      environment:
         TZ: Asia/Shanghai
      volumes:
        - "./volumes/tomcat/webapps:/opt/tomcat/webapps"
        - "./volumes/tomcat/logs:/opt/tomcat/logs"
      restart: always
      container_name: docker_tomcat


    docker-ui:
      image: uifd/ui-for-docker
      ports:
        - "9000:9000"
      volumes:
        - "/var/run/docker.sock:/var/run/docker.sock"
      restart: always
      container_name: docker_ui

    nginx:
       image: daocloud.io/nginx
       ports:
         - "12000:80"
       environment:
         TZ: Asia/Shanghai
       volumes:
         - "./volumes/nginx/default.conf:/etc/nginx/conf.d/default.conf"
       restart: always
       container_name: docker_nginx

    jenkins:
       image: jenkins/jenkins:lts
       ports:
         - "12002:8080"
         - "12003:50000"
       environment:
         TZ: Asia/Shanghai
       volumes:
         - "./volumes/jenkins:/var/jenkins_home"
       restart: always
       container_name: docker_jenkins

    redis:
      image: redis:3.2
      ports:
        - "6379:6379"
      environment:
        TZ: Asia/Shanghai
      volumes:
        - "/etc/localtime:/etc/localtime"
      restart: always
      container_name: docker_redis

    zookeeper:
      image: wurstmeister/zookeeper
      ports:
        - "2181:2181"
      restart: always
      container_name: kafka_zookeeper_1

    kafka:
      image: wurstmeister/kafka
      volumes:
          - /etc/localtime:/etc/localtime
      ports:
        - "9092:9092"
      restart: always
      environment:
        KAFKA_ADVERTISED_HOST_NAME: 192.168.7.118
        KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      depends_on:
       - zookeeper
      container_name: kafka_1


    kafka-manager:
     image: sheepkiller/kafka-manager:latest
     ports:
      - "9001:9000"
     links:
      - zookeeper
      - kafka
     environment:
       ZK_HOSTS: zookeeper:2181
       APPLICATION_SECRET: letmein
       KM_ARGS: -Djava.net.preferIPv4Stack=true
     restart: always
     container_name: kafka_manager_1

    es:
      image: elasticsearch:5.6.4
      container_name: ezview_elasticsearch_1
      volumes:
         - "./volumes/es/data:/usr/share/elasticsearch/data"
         - "./volumes/es/config/es1.yml:/usr/share/elasticsearch/config/elasticsearch.yml"
      ports:
         - "9200:9200"
         - "9300:9300"
      restart: always
      container_name: es_1

```

##### F、lychee图床

```shell
docker run --name=lychee -it -d -p 9002:80 kdelfour/lychee-docker
```

