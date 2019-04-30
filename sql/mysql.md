# Mysql

## 问题：

### 一、mysql 区分表名字大小写

**问题具体描述**：linux下mysql区分表名大小写，window下表明不区分大小写

​		         可以通过show global variables like '%lower_case%'; 查看该值设置

**解决**：

非Docker部署

1、停止mysql

```shell
mysqladmin -u root -p shutdown      
```

2、查找文件 

```
sudo find / -name my.cnf
```

3、修改my.cnf,增加 lower_case_table_names=1

```
[mysqld]
lower_case_table_names=1
```

4、启动mysql

```shell
/etc/init.d/mysql start       
```

Docker部署

mysql.cnf的路径 /etc/mysql/mysql.conf.d/mysqld.cnf

1、进入容器

```shell
docker exec -it mysql /bin/bash
```

2、修改文件

```shell
vim  /etc/mysql/mysql.conf.d/mysqld.cnf
```

2、修改mysqld.cnf,增加 lower_case_table_names=1

```shell
[mysqld]
lower_case_table_names=1
```

3、退出容器重启

```shell
docker restart mysql
```







