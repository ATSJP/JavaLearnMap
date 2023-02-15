[TOC]







# Lock

## InnoDB Locking

> 官方文档：https://dev.mysql.com/doc/refman/8.0/en/innodb-locking.html

This section describes lock types used by `InnoDB`.

- [Shared and Exclusive Locks](https://dev.mysql.com/doc/refman/8.0/en/innodb-locking.html#innodb-shared-exclusive-locks)
- [Intention Locks](https://dev.mysql.com/doc/refman/8.0/en/innodb-locking.html#innodb-intention-locks)
- [Record Locks](https://dev.mysql.com/doc/refman/8.0/en/innodb-locking.html#innodb-record-locks)
- [Gap Locks](https://dev.mysql.com/doc/refman/8.0/en/innodb-locking.html#innodb-gap-locks)
- [Next-Key Locks](https://dev.mysql.com/doc/refman/8.0/en/innodb-locking.html#innodb-next-key-locks)
- [Insert Intention Locks](https://dev.mysql.com/doc/refman/8.0/en/innodb-locking.html#innodb-insert-intention-locks)
- [AUTO-INC Locks](https://dev.mysql.com/doc/refman/8.0/en/innodb-locking.html#innodb-auto-inc-locks)
- [Predicate Locks for Spatial Indexes](https://dev.mysql.com/doc/refman/8.0/en/innodb-locking.html#innodb-predicate-locks)

### Shared and Exclusive Locks

InnoDB实现标准行级锁，有两种类型的锁，共享锁(S)和独占(X)锁。

- 共享锁(S)允许持有此锁的事务可以读取一行。
- 独占(X)锁允许持有此锁的事务可以更新或者删除一行。

如果事务T1在行r上持有共享(S)锁，则来自某个不同事务T2的请求在行r上的锁将按如下方式处理：

- 可以立即授予T2的S锁请求。结果，T1和T2都持有r上的S锁。

- 不能立即授予T2对X锁的请求。

如果事务T1在行r上持有独占(X)锁，则无法立即授予来自某个不同事务T2对r上任一类型锁的请求。 相反，事务T2必须等待事务T1释放它对行r的锁定。

### Intention Locks

InnoDB支持多粒度锁定，允许行锁和表锁共存。诸如LOCK TABLES...WRITE之类的语句在指定的表上获取排他锁（X锁）。为了使多粒度级别的锁定变得可行，InnoDB使用意向锁。意向锁是表级锁，指示事务稍后对表中的行需要哪种类型的锁（共享或独占）。意向锁有两种类型：

- 意向共享锁(IS)表示事务打算在表中的各个行上设置共享锁。
- 意向排他锁(IX)表示事务打算在表中的各个行上设置排他锁。

例如，SELECT ... FOR SHARE 设置一个 IS 锁，而 SELECT ... FOR UPDATE 设置一个IX锁。

意向锁定协议如下：

- 在事务可以获取表中行的共享锁之前，它必须首先获取表上的IS锁或更强锁。

- 在事务可以获取表中一行的排他锁之前，它必须首先获取表上的IX锁。

下表总结了表级锁类型兼容性。

|      | `X`      | `IX`       | `S`        | `IS`       |
| :--- | :------- | :--------- | :--------- | :--------- |
| `X`  | Conflict | Conflict   | Conflict   | Conflict   |
| `IX` | Conflict | Compatible | Conflict   | Compatible |
| `S`  | Conflict | Conflict   | Compatible | Compatible |
| `IS` | Conflict | Compatible | Compatible | Compatible |

如果请求事务与现有锁兼容，则将锁授予请求事务，但如果它与现有锁冲突，则不会授予该锁。 事务等待，直到释放冲突的现有锁。 如果锁定请求与现有锁定冲突并且由于会导致死锁而无法授予，则会发生错误。

意向锁不会阻塞除完整表请求之外的任何内容（例如，LOCK TABLES ... WRITE）。 意向锁的主要目的是表明有人正在锁定一行，或者将要锁定表中的一行。

意向锁的事务数据在 SHOW ENGINE INNODB STATUS 和 InnoDB 监视器输出中显示类似于以下内容：

```sql
TABLE LOCK table `test`.`t` trx id 10080 lock mode IX
```

### Record Lock

### Gap Locks

### Next-Key Lock的规则
前提：因为间隙锁在InnoDB、可重复读隔离级别下才有效，所以接下来的描述，若没有特殊说明，默认是可重复读隔离级别。

Next-Key Lock为Record Lock + Gap Lock

原则 1：加锁的基本单位是 next-key lock。next-key lock 是前开后闭区间。
原则 2：只有访问到的对象才会加锁。
优化 1：索引上的等值查询，
        命中唯一索引，退化为行锁。
        命中普通索引，左右两边的GAP Lock + Record Lock。
优化 2：
        索引上的等值查询，未命中，所在的Net-Key Lock，退化为GAP Lock 。
        索引在范围查询：
        1.等值和范围分开判断。
        2.索引在范围查询的时候 都会访问到所在区间不满足条件的第一个值为止。
        3.如果使用了倒叙排序，按照倒叙排序后:
            检索范围的右边多加一个GAP。
            哪个方向还有命中的等值判断，再向同方向拓展外开里闭的区间。

### Insert Intention Locks

### AUTO-INC Locks

### Predicate Locks for Spatial Indexes





### 锁兼容

Table-level lock type compatibility is summarized in the following matrix.

|      | `X`      | `IX`       | `S`        | `IS`       |
| :--- | :------- | :--------- | :--------- | :--------- |
| `X`  | Conflict | Conflict   | Conflict   | Conflict   |
| `IX` | Conflict | Compatible | Conflict   | Compatible |
| `S`  | Conflict | Conflict   | Compatible | Compatible |
| `IS` | Conflict | Compatible | Compatible | Compatible |