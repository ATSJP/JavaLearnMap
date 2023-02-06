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

### Intention Locks

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