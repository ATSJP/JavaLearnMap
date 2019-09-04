## docker部署jenkins

### 1.docker

[安装部署](https://github.com/atsjp/note/tree/master/Docker/Docker.md)

### 2.pull一个jenkins镜像 

```shell
docker pull jenkinsci/blueocean
```

### 3.创建一个jenkins目录 

```shell
mkdir /root/jenkins_home
```

### 5.启动一个jenkins容器   

```shell
docker run \
  -d \
  --name jenkins \
  -u root \
  -p 9001:8080 -p 50000:50000 \
  -v jenkins-data:/var/jenkins_home \
  -v /var/run/docker.sock:/var/run/docker.sock \
  -v /root/jenkins_home:/root/jenkins_home \
  jenkinsci/blueocean
```
> 备用命令：
>
> ```shell
> 镜像： jenkins/jenkins 
>       jenkinsci/blueocean
>       
> # 启动jenkins -d 后台运行 
> docker run \
>   -d \
>   --name jenkins \
>   -u root \
>   -p 9001:8080 -p 50000:50000 \
>   -v jenkins-data:/var/jenkins_home \
>   -v /var/run/docker.sock:/var/run/docker.sock \
>   -v /root/jenkins_home:/root/jenkins_home \
>   jenkins
>   
> #  --rm 临时启动一个jenkins容器，停止后自动清除
> docker run \
>   --rm \
>   --name jenkins \
>   -u root \
>   -p 9001:8080 -p 50000:50000 \
>   -v jenkins-data:/var/jenkins_home \
>   -v /var/run/docker.sock:/var/run/docker.sock \
>   -v /root/jenkins_home:/root/jenkins_home \
>   jenkinsci/blueocean
> 
> # 启动jenkins -d 后台运行 
> docker run \
>   -d \
>   --name jenkins \
>   -u root \
>   -p 9001:8080 -p 50000:50000 \
>   -v jenkins-data:/var/jenkins_home \
>   -v /var/run/docker.sock:/var/run/docker.sock \
>   -v /root/jenkins_home:/root/jenkins_home \
>   jenkinsci/blueocean
> ```

### 6.查看jenkins服务 

```shell
docker ps | grep jenkins
```

### 7.浏览器访问

localhost:9001/

![1567576863615](assets/1567576863615.png)

### 8.去容器内部找密码

```shell
docker exec -it jenkins /bin/bash 
cat /var/jenkins_home/secrets/initialAdminPassword
```

<img src="assets/1567502904219.png" alt="1567502904219" style="zoom: 200%;" />

### 9.后面就是配置插件的问题了  ，直接按照推荐安装一波即可。如果有失败的也不用慌，可以尝试重试，或者下一步，后续在继续安装（官方的镜像经常安装插件失败，可能和插件源有关）

![1567502846263](assets/1567502846263.png)