[TOC]



# Spring之事务

## 一、事务的那些概念

### 1、事务执行正确四要素

​		**ACID** 

- **原子性（Atomicity）**：即事务是不可分割的最小工作单元，事务内的操作要么全做，要么全不做，不能只做一部分； 

- **一致性（Consistency）**：在事务执行前数据库的数据处于正确的状态，而事务执行完成后数据库的数据还是处于正确的状态，即数据完整性约束没有被破坏；比如我们做银行转账的相关业务，A转账给B，要求A转的钱B一定要收到。如果A转了钱而B没有收到，那么数据库数据的一致性就得不到保障，在做高并发业务时要注意合理的设计。 

- **隔离性（Isolation）**：并发事务执行之间无影响，在一个事务内部的操作对其他事务是不产生影响，这需要事务隔离级别来指定隔离性； 

- **持久性（Durability）**：事务一旦执行成功，它对数据库的数据的改变必须是永久的，不会因各种异常导致数据不一致或丢失。 

### 2、事务执行存在问题及隔离级别

​		事务在执行过程中，以下四种情况是不可避免的问题。

- **丢失更新**：两个事务同时更新一行数据，最后一个事务的更新会覆盖掉第一个事务的更新，从而导致第一个事务更新的数据丢失，后果比较严重。一般是由于没加锁的原因造成的。 

- **脏读（Dirty reads）**：一个事务A读取到了另一个事务B还没有提交的数据，并在此基础上进行操作。如果B事务rollback，那么A事务所读取到的数据就是不正确的，会带来问题。 

- **不可重复读（Non-repeatable reads）**：在同一事务范围内读取两次相同的数据，所返回的结果不同。比如事务B第一次读数据后，事务A更新数据并commit，那么事务B第二次读取的数据就与第一次是不一样的。 

- **幻读（Phantom reads）**：一个事务A读取到了另一个事务B新提交的数据。比如，事务A对一个表中所有行的数据按照某规则进行修改（整表操作），同时，事务B向表中插入了一行原始数据，那么后面事务A再对表进行操作时，会发现表中居然还有一行数据没有被修改，就像发生了幻觉，飘飘欲仙一样。 

  

  注意：不可重复读和幻读的区别是，不可重复读对应的表的操作是更改(UPDATE)，而幻读对应的表的操作是插入(INSERT)，两种的应对策略不一样。对于不可重复读，只需要采用行级锁防止该记录被更新即可，而对于幻读必须加个表级锁，防止在表中插入数据。有关锁的问题，下面会讨论。

为了处理这几种问题，SQL定义了下面的4个等级的事务隔离级别：

- **未提交读（READ UNCOMMITTED ）**：最低隔离级别，一个事务能读取到别的事务未提交的更新数据，很不安全，可能出现丢失更新、脏读、不可重复读、幻读； 

- **提交读（READ COMMITTED）**：一个事务能读取到别的事务提交的更新数据，不能看到未提交的更新数据，不会出现丢失更新、脏读，但可能出现不可重复读、幻读； 

- **可重复读（REPEATABLE READ）**：保证同一事务中先后执行的多次查询将返回同一结果，不受其他事务影响，不可能出现丢失更新、脏读、不可重复读，但可能出现幻读； 

- **序列化（SERIALIZABLE）**：最高隔离级别，不允许事务并发执行，而必须串行化执行，最安全，不可能出现更新、脏读、不可重复读、幻读，但是效率最低。 

  隔离级别越高，数据库事务并发执行性能越差，能处理的操作越少。所以一般地，推荐使用REPEATABLE READ级别保证数据的读一致性。对于幻读的问题，可以通过加锁来防止。 

## 二、Spring之事务


### 1、事务传播性

- **PROPAGATION_REQUIRED**：A如果有事务，B将使用该事务；如果A没有事务，B将创建一个新的事务

- **PROPAGATION_SUPPORTS**：A如果有事务，B将使用该事务；如果A没有事务，B将以非事务执行

- **PROPAGATION_MANDATORY**：A如果有事务，B将使用该事务；如果A没有事务，B将抛异常

- **PROPAGATION_REQUIRES_NEW**：A如果有事务，将A的事务挂起，B创建一个新的事务；如果A没有事务，B创建一个新的事务

- **PROPAGATION_NOT_SUPPORTED**：A如果有事务，将A的事务挂起，B将以非事务执行；如果A没有事务，B将以非事务执行

- **PROPAGATION_NEVER**：A如果有事务，B将抛异常；A如果没有事务，B将以非事务执行

- **PROPAGATION_NESTED**：A和B底层采用保存点机制，形成嵌套事务
  
  
  
  注意：
  
  1、事务传播性是Spring提供的，而不是数据库提供。
  
  2、事务是和线程绑定的，也就是事务的传播针对是单线程，在不同的线程，事务是隔离的。

> **Tips**: PROPAGATION_REQUIRES_NEW 和 PROPAGATION_NESTED 有什么区别呢？
>
> **PROPAGATION_REQUIRES_NEW**：A的事务和B的事务是完全独立的，当执行到B时，B发现A有事务，则挂起，等B执行完（无论是commit还是rollback），A继续执行。
> **PROPAGATION_NESTED**： 不会创建新的事务，当发现A有事务后，B作为A的子事务（新建一个保存点，但不产生新的事务），当B执行失败，B会rollback（回退到保存点，不会产生），并抛出异常到A，A发现B异常了，可以选择回滚，或者捕捉异常不会滚A的操作，继续执行。
>
> **场景举例：**
>
> 针对`PROPAGATION_NESTED`的特性（不产生新的事务），在下面这个场景下，很好的提现了其优势。我们有3个Service，目的：ServiceB和ServiceC两者优先B执行，B失败了执行C，且B的数据要求回滚掉，希望尽可能少开事务。
>
> ```java
> ServiceA:
> 
> @Transactional(rollbackFor = RuntimeException.class)
> void methodA() {  
>     try {  
>         ServiceB.methodB();
>     } catch (SomeException) {  
>         // 执行其他业务  
>         ServiceC.methodC()
>     }  
> }
> 
> ServiceB:
> 
> @Transactional(propagation = Propagation.NESTED, rollbackFor = RuntimeException.class)
> void methodB() {  
>    // ...
> }  
> ```
>
> 如上设计，可以在B异常后Rollback掉B的脏数据，继续执行C，TryCatch起到一个分支执行的作用。

### 2、是否只读

如果将事务设置为只读，表示这个事务只读取数据但不更新数据, 这样可以帮助数据库引擎优化事务。

### 3、事务超时

事务超时就是事务的一个定时器，在特定时间内事务如果没有执行完毕，那么就会自动回滚，而不是一直等待其结束。在 TransactionDefinition 中以 int 的值来表示超时时间，默认值是-1，其单位是秒。

### 3、回滚规则

回滚规则定义了哪些异常会导致事务回滚而哪些不会。默认情况下，事务只有遇到运行期异常时才会回滚。


## 结合数据库思考一些场景

### 隔离级别

代码如下：

```java
  class UserService {
    private UserDAO userDao;
    private UserManager userManager;
  
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = RuntimeException.class)
    public void doSomething(String userMobile) {
      UserEntity userEntity = userDao.selectByUserMobile(userMobile);
      logger.info("isExists:{}", userEntity != null);
      Long userId = userManager.insertByUserMobile(userMobile);
      userEntity = userDao.selectByUserMobile(userMobile);
      logger.info("isExists:{}", userEntity != null);
      userEntity = userDao.selectByPrimaryKey(userId);
      logger.info("isExists:{}", userEntity != null);
    }
  }
  
  class UserManager {
    private UserDAO userDao;

    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.DEFAULT, rollbackFor = RuntimeException.class)
    public Long insertByUserMobile(String userMobile) {
      UserEntity userEntity = new UserEntity();
      userEntity.setUserMobile(userMobile);
      userDao.insert(userEntity);
      return userEntity.getId();
    }
	
  }
```

当使用Mysql数据库，配置RR（Repeatable Read，可重复读）隔离级别，使用Innodb，猜猜结果是什么，如果是Oracle（Oracle仅支持Read Committed、Serializable，默认是RC）呢？

Mysql、隔离级别RR、Innodb：
```text
isExists:false
isExists:false
isExists:false
```

如果代码在稍微调整一下呢？

代码如下：

```java
  class UserService {
    private UserDAO userDao;
    private UserManager userManager;

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = RuntimeException.class)
    public void doSomething(String userMobile) {
      /// UserEntity userEntity = userDao.selectByUserMobile(userMobile);
      /// logger.info("isExists:{}", userEntity != null);
      Long userId = userManager.insertByUserMobile(userMobile);
      UserEntity userEntity = userDao.selectByUserMobile(userMobile);
      logger.info("isExists:{}", userEntity != null);
      userEntity = userDao.selectByPrimaryKey(userId);
      logger.info("isExists:{}", userEntity != null);
    }
  }
  
  class UserManager {
    private UserDAO userDao;

    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.DEFAULT, rollbackFor = RuntimeException.class)
    public Long insertByUserMobile(String userMobile) {
      UserEntity userEntity = new UserEntity();
      userEntity.setUserMobile(userMobile);
      userDao.insert(userEntity);
      return userEntity.getId();
    }
	
  }
```

Mysql、隔离级别RR、Innodb：
```text
isExists:true
isExists:true
```

**总结**：SQL标准规范说明了，RR、RC的要求，即RR要求一个事务里，多次读结果是一样的（可能会出现幻读），RC仅要求读的内容是别的事务已提交的内容。



故这么解释，可以搞得清楚结果，但是往深了去，针对Mysql 可以延深到RR级别下，InnoDB，如何实现的，主要会涉及到MVCC、Read View等。具体详细内容，见我的这篇[MVCC](../Sql/Mysql/MVCC.md)。
