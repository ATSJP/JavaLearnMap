### mysql 区分表名字大小写

修改mysql为不区分大小写设置：
[root@test-huanqiu ~]# mysqladmin -uroot -p shutdown               //以安全模式关闭数据库
[root@test-huanqiu ~]# cat /etc/my.cnf                                          //添加下面一行设置
.....
[mysqld]
lower_case_table_names=1
.....

[root@test-huanqiu ~]# /etc/init.d/mysql start                                 //启动mysql




show global variables like '%lower_case%';

# docker

/etc/mysql/mysql.conf.d/mysqld.cnf