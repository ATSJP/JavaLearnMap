[TOC]

# Mysql

## 一、简介

1、事务

未提交读（READ UNCOMMITTED ）：最低隔离级别，一个事务能读取到别的事务未提交的更新数据，很不安全，可能出现丢失更新、脏读、不可重复读、幻读； 

提交读（READ COMMITTED）：一个事务能读取到别的事务提交的更新数据，不能看到未提交的更新数据，不会出现丢失更新、脏读，但可能出现不可重复读、幻读； 

可重复读（REPEATABLE READ）：保证同一事务中先后执行的多次查询将返回同一结果，不受其他事务影响，不可能出现丢失更新、脏读、不可重复读，但可能出现幻读； 

序列化（SERIALIZABLE）：最高隔离级别，不允许事务并发执行，而必须串行化执行，最安全，不可能出现更新、脏读、不可重复读、幻读，但是效率最低。 

MySQL支持这四种事务等级，默认事务隔离级别是REPEATABLE READ。

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







