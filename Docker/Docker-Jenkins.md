开门见山，如何在利用docker快速部署jenkins服务?下面详解

1：docker的基本按照与部署，前文已经详述，这里不多说。

2：pull一个jenkins镜像 docker pull jenkins

![img](https://images2018.cnblogs.com/blog/946454/201803/946454-20180314095836641-652109704.png)

3：查看已经安装的jenkins镜像 docker images

![img](https://images2018.cnblogs.com/blog/946454/201803/946454-20180314095930255-1359706390.png)

4：创建一个jenkins目录  mkdir /home/jenkins_home

![img](https://images2018.cnblogs.com/blog/946454/201803/946454-20180314100059965-977253506.png)

5：启动一个jenkins容器    docker run -d --name jenkins -p 8081:8080 -v /home/jenkins_home:/home/jenkins_home jenkins   

　  其中8081：8080，表示jenkins内部使用8080端口，服务器使用8081端口，然后将二者映射起来，之后在浏览器访问的时候实际上还是访问服务器的8081端口

 ![img](https://images2018.cnblogs.com/blog/946454/201803/946454-20180314100500459-704930502.png)

6：查看jenkins服务   docker ps | grep jenkins

![img](https://images2018.cnblogs.com/blog/946454/201803/946454-20180314100614650-1174907295.png)

7 ：启动服务端 。localhost:8081/jenkins

![img](https://images2018.cnblogs.com/blog/946454/201803/946454-20180314100737580-1645742770.png)

8：去容器内部找密码

进入容器：docker exec -it jenkins bash 

![img](https://images2018.cnblogs.com/blog/946454/201803/946454-20180314100848358-661546759.png)

执行：c  红框即为jenkins初始密码

![img](https://images2018.cnblogs.com/blog/946454/201803/946454-20180314101000785-2141159891.png)

9：输入密码之后，重启docker镜像  docker restart   {CONTAINER ID}

![img](https://images2018.cnblogs.com/blog/946454/201803/946454-20180314101122949-399037259.png)

10：再次启动服务端，jenkins服务已经起来了。后面就是配置插件的问题了  