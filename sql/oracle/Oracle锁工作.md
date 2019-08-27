## 一、介绍

### 1、锁级别

| 锁代码 |  锁模式名称  | 锁模式缩写 |   锁模式别名   |    锁级别     | 描述                              |
| :----: | :----------: | :--------: | :------------: | :-----------: | --------------------------------- |
|   0    |     None     |    None    |      None      |               | none                              |
|   1    |     Null     |    Null    |      Null      |    表级锁     | 空                                |
|   2    |  Row-S(SS)   |     SS     |       RS       |    表级锁     | 共享表锁                          |
|   3    |  Row-X(SX)   |     SX     |       RX       |    表级锁     | 用于行的修改                      |
|   4    |    Share     |     S      |       S        |    表级锁     | 阻止其他DML操作                   |
|   5    | S/Row-X(SSX) |    SSX     |      SRX       |    表级锁     | 共享行专用(SRX)：阻止其他事务操作 |
|   6    |  Exclusive   |     X      |       X        | 表级锁/行级锁 | 专用(X)：独立访问使用             |
|        |              |            | R是行，S是共享 |               |                                   |

备注：**数字越大锁级别越高, 影响的操作越多**

### 2、锁分类

​      A、Oracle锁基本上可以分为二类

-  共享锁（share locks）也称读锁，S锁
-  排它锁（exclusive locks）也称写锁，X锁

​      B、按照锁的保护对象来分

- dml锁， data locks 数据锁，用来保护数据的完整性和一致性

  > dml锁又分为TM锁（表级锁，TM锁的种类有S,X,SR,SX,SRX五种），TX锁（主要为事务锁和行级锁），在执行数据库DML的时候（如：insert，update，delete，select for update等），将会先获取TM锁，然后才获取TX锁。
  >
  > （扩展：**为啥有了TX锁了，还需要获取TM锁？ **
  >
  > 当事务获得行锁后，此事务也将自动获得该行的表锁(共享锁),以防止其它事务进行DDL语句影响记录行的更新。事务也可以在进行过程中获得共享锁或排它锁，只有当事务显示使用LOCK TABLE语句显示的定义一个排它锁时，事务才会获得表上的排它锁,也可使用LOCK TABLE显示的定义一个表级的共享锁 ，所以行锁的时候我还的拿个表锁，免得其他人改了我的表结构或者删除了我的表。
  >
  > 此处这样设计还有一个好处：TM锁是表级锁，可以直接通过TM锁判断是否已经存在加锁对象，而不需要通过扫描表中每一行来判断是否有加锁行，这样对锁的判断效率明显提高。）


- ddl锁， dictionary locks 字典锁，用来保护数据对象的结构，如table，index的定义

- 内部锁和闩 internal locks and latchs 用来保护数据库内部结构，如SGA内存结构
### 3、锁兼容性
锁兼容性，顾名思义，两种不同的锁相互间是否兼容，这里的兼容，即我在A表加了锁A，那么B锁兼容A表，我就可以在A表继续加B锁，反之，无法加锁。

**表级锁兼容性关系表**: 

|              |  N   | SS(Row-S) | SX(Row-X) | S(Share) | SSX(S/Row-X) | X(Exclusive) |
| :----------: | :--: | :-------: | :-------: | :------: | :----------: | :----------: |
|      N       |  Y   |     Y     |     Y     |    Y     |      Y       |      Y       |
|  SS(Row-S)   |  Y   |     Y     |     Y     |    Y     |      Y       |      N       |
|  SX(Row-X)   |  Y   |     Y     |     Y     |    N     |      N       |      N       |
|   S(Share)   |  Y   |     Y     |     N     |    Y     |      N       |      N       |
| SSX(S/Row-X) |  Y   |     Y     |     N     |    N     |      N       |      N       |
| X(Exclusive) |  Y   |     N     |     N     |    N     |      N       |      N       |

### 4、举例

介绍了一堆理论和概念，不如来点实际的例子痛快，下面就给大家提供了一些常用的SQL锁级别。

**A、1级锁：**
Select，有时会在v$locked_object出现。

**B、2级锁即RS锁**
相应的sql有：Lock table xxx in  Row Share mode，

**C、3级锁即RX锁**
相应的sql有：Insert, Update, Delete, Select for update，Lock table xxx in Row Exclusive mode。（select for update当对话使用for update子串打开一个游标时，所有返回集中的数据行都将处于行级(Row-X)独占式锁定，其他对象只能查询这些数据行，不能进行update、delete或select for update操作。）

**D、4级锁即S锁**
相应的sql有：Create Index, Lock table xxx in Share mode

**E、5级锁即SRX锁**
相应的sql有：Lock table xxx in Share Row Exclusive mode

**F、6级锁即X锁**
相应的sql有：Alter table, Drop table, Drop Index, Truncate table, Lock table xxx in Exclusive mode


## 二、常用锁查询sql

```sql
--查询Oracle正在执行的sql语句及执行该语句的用户
SELECT b.sid oracleID,  
       b.username 登录Oracle用户名,  
       b.serial#,  
       spid 操作系统ID,  
       paddr,  
       sql_text 正在执行的SQL,  
       b.machine 计算机名  
FROM v$process a, v$session b, v$sqlarea c  
WHERE a.addr = b.paddr  
   AND b.sql_hash_value = c.hash_value  

--查看正在执行sql的发起者的发放程序
SELECT OSUSER 电脑登录身份,  
       PROGRAM 发起请求的程序,  
       USERNAME 登录系统的用户名,  
       SCHEMANAME,  
       B.Cpu_Time 花费cpu的时间,  
       STATUS,  
       B.SQL_TEXT 执行的sql  
FROM V$SESSION A  
LEFT JOIN V$SQL B ON A.SQL_ADDRESS = B.ADDRESS  
                   AND A.SQL_HASH_VALUE = B.HASH_VALUE  
ORDER BY b.cpu_time DESC 

--查出oracle当前的被锁对象
SELECT l.session_id sid,  
       l.locked_mode 锁模式,  
       l.oracle_username 登录用户,  
       l.os_user_name 登录机器用户名,  
       s.machine 机器名,  
       s.terminal 终端用户名,  
       o.object_name 被锁对象名,  
       s.logon_time 登录数据库时间  
FROM v$locked_object l, all_objects o, v$session s  
WHERE l.object_id = o.object_id  
   AND l.session_id = s.sid  

-- 表锁
select b.owner,b.object_name,a.session_id,a.locked_mode from v$locked_object a,dba_objects b where b.object_id = a.object_id;

-- 查看哪个session引起的表被锁住
select b.username,b.sid,b.serial#,logon_time from v$locked_object a,v$session b where a.session_id = b.sid order by b.logon_time; 

-- 清除锁表的session
alter system kill SESSION '405,47249';
```