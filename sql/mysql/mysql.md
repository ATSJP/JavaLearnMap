# Mysql

## 一、简介

## 二、使用

#### A、用户及权限

```sql
# 创建用户
create user 'user01'@'127.0.0.1' identified by '666666';

# 赋予全部权限
grant all privileges on *.* to 'user01'@'127.0.0.1' identified by '666666';

# 赋于指定权限
grant select,insert on <database>.<table> to '<user>'@'<ip>' identified by '<password>';
```

#### B、远程

```sql
select host from user  where user ='root'

update user set host = '%' where user = 'root';

flush privileges;
```

## 三、常见问题

### A、mysql 区分表名字大小写

**问题具体描述**：linux下mysql区分表名大小写，window下表明不区分大小写

>  可以通过show global variables like '%lower_case%'; 查看该值设置

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







