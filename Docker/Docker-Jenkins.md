## docker部署jenkins

1：docker的基本按照与部署，这里不多说。

2：pull一个jenkins镜像 

```shell
docker pull jenkins/jenkins
```

3：查看已经安装的jenkins镜像 

```shell
docker images
```



![img](https://images2018.cnblogs.com/blog/946454/201803/946454-20180314095930255-1359706390.png)

4：创建一个jenkins目录  

```shell
mkdir /root/jenkins_home
```

5：启动一个jenkins容器   

```shell
docker run -d --name jenkins -p 8081:8080 -p 50000:50000 -v /root/jenkins_home:/jenkins_home jenkins/jenkins
```

　  其中8081：8080，表示jenkins内部使用8080端口，服务器使用8081端口，然后将二者映射起来，之后在浏览器访问的时候实际上还是访问服务器的8081端口

6：查看jenkins服务   

```shell
docker ps | grep jenkins
```



![img](https://images2018.cnblogs.com/blog/946454/201803/946454-20180314100614650-1174907295.png)

7 ：启动服务端 。localhost:8081/jenkins

![img](https://images2018.cnblogs.com/blog/946454/201803/946454-20180314100737580-1645742770.png)

8：去容器内部找密码

```shell
docker exec -it jenkins bash 
```



![img](https://images2018.cnblogs.com/blog/946454/201803/946454-20180314100848358-661546759.png)

红框即为jenkins初始密码

```shell
cat /var/jenkins_home/secrets/initialAdminPassword
```



![img](https://images2018.cnblogs.com/blog/946454/201803/946454-20180314101000785-2141159891.png)

9：后面就是配置插件的问题了  ，直接按照推荐安装一波即可。