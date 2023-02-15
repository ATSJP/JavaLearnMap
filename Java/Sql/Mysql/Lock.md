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

InnoDB实现标准行级锁，有两种类型的锁，共享锁(S)和排他锁(X)。

- 共享锁(S)允许持有此锁的事务可以读取一行。
- 排他锁(X)允许持有此锁的事务可以更新或者删除一行。

如果事务T1在行r上持有共享锁(S)，则来自某个不同事务T2的请求在行r上的锁将按如下方式处理：

- 可以立即授予T2的共享锁(S)请求。结果，T1和T2都持有r上的共享锁(S)。

- 不能立即授予T2对排他锁(X)的请求。

如果事务T1在行r上持有排他锁(X)，则无法立即授予来自某个不同事务T2对r上任一类型锁的请求。 相反，事务T2必须等待事务T1释放它对行r的锁定。

### Intention Locks

InnoDB支持多粒度锁定，允许行锁和表锁共存。诸如 LOCK TABLES ... WRITE 之类的语句在指定的表上获取排他锁(X)。为了使多粒度级别的锁定变得可行，InnoDB使用意向锁。意向锁是表级锁，指示事务稍后对表中的行需要哪种类型的锁（共享或独占）。意向锁有两种类型：

- 意向共享锁(IS)表示事务打算在表中的各个行上设置共享锁(S)。
- 意向排他锁(IX)表示事务打算在表中的各个行上设置排他锁(X)。

例如，SELECT ... FOR SHARE 设置一个意向共享锁(IS)，而 SELECT ... FOR UPDATE 设置一个意向排他锁(IX)。

意向锁定协议如下：

- 在事务可以获取表中行的共享锁(S)之前，它必须首先获取表上的意向共享锁(IS)或更强锁。

- 在事务可以获取表中一行的排他锁(X)之前，它必须首先获取表上的意向排他锁(IX)。

下表总结了表级锁类型兼容性。

|      | `X`      | `IX`       | `S`        | `IS`       |
| :--- | :------- | :--------- | :--------- | :--------- |
| `X`  | Conflict | Conflict   | Conflict   | Conflict   |
| `IX` | Conflict | Compatible | Conflict   | Compatible |
| `S`  | Conflict | Conflict   | Compatible | Compatible |
| `IS` | Conflict | Compatible | Compatible | Compatible |

如果请求事务与现有锁兼容，则将锁授予请求事务，但如果它与现有锁冲突，则不会授予该锁，进入事务等待，直到释放冲突的现有锁。如果锁定请求与现有锁定冲突并且由于会导致死锁而无法授予，则会发生错误。

意向锁不会阻塞除全表请求之外的任何内容（例如，LOCK TABLES ... WRITE）。意向锁的主要目的是表明有人正在锁定一行，或者将要锁定表中的一行。

意向锁的事务数据在 SHOW ENGINE INNODB STATUS 和 InnoDB 监视器输出中显示类似于以下内容：

```sql
TABLE LOCK table `test`.`t` trx id 10080 lock mode IX
```

### Record Lock

记录锁是索引记录上的锁。例如，SELECT c1 FROM t WHERE c1 = 10 FOR UPDATE；防止任何其他事务插入、更新或删除 t.c1 值为 10 的行。记录锁总是锁定索引记录，即使表没有定义索引。对于这种情况，InnoDB 创建一个隐藏的聚簇索引并使用该索引进行记录锁定。

记录锁的事务数据在 SHOW ENGINE INNODB STATUS 和 InnoDB 监视器输出中显示类似于以下内容：

```sql
RECORD LOCKS space id 58 page no 3 n bits 72 index `PRIMARY` of table `test`.`t`
trx id 10078 lock_mode X locks rec but not gap
Record lock, heap no 2 PHYSICAL RECORD: n_fields 3; compact format; info bits 0
 0: len 4; hex 8000000a; asc     ;;
 1: len 6; hex 00000000274f; asc     'O;;
 2: len 7; hex b60000019d0110; asc        ;;
```

### Gap Locks

间隙锁是在索引记录之间的间隙上的锁，或者是在第一条索引记录之前或最后一条索引记录之后的间隙上的锁。 例如，SELECT c1 FROM t WHERE c1 BETWEEN 10 and 20 FOR UPDATE； 防止其他事务将值 15 插入到列 t.c1 中，无论该列中是否已经存在任何此类值，因为该范围内所有现有值之间的间隙都已锁定。

间隙可能跨越单个索引值、多个索引值，甚至是空的。

间隙锁是性能和并发性之间权衡的一部分，并且用于某些事务隔离级别而不是其他事务隔离级别。

对于使用唯一索引锁定行以搜索唯一行的语句，不需要间隙锁定。（这不包括搜索条件只包括多列唯一索引的部分列的情况；这种情况下确实会发生间隙锁定。）例如，如果id列有一个唯一索引，下面的语句只使用 id 值为 100 的行的索引记录锁，其他会话是否在前面的间隙中插入行并不重要：

```sql
SELECT * FROM child WHERE id = 100;
```

如果 id 没有索引或具有非唯一索引，则该语句会锁定前面的间隙。

这里还值得注意的是，不同事务可以在间隙上持有冲突锁。例如，事务 A 可以在一个间隙上持有一个共享间隙锁（间隙 S 锁），而事务 B 在同一间隙上持有一个独占间隙锁（间隙 X 锁）。允许冲突间隙锁的原因是，如果从索引中清除记录，则必须合并不同事务在记录上持有的间隙锁。

InnoDB 中的间隙锁是“纯粹抑制性的”，这意味着它们的唯一目的是防止其他事务插入间隙。间隙锁可以共存。一个事务获取的间隙锁不会阻止另一个事务在同一间隙上获取间隙锁。共享和排他间隙锁之间没有区别。它们彼此不冲突，并且它们执行相同的功能。

可以明确禁用间隙锁定。如果您将事务隔离级别更改为 READ COMMITTED，就会发生这种情况。在这种情况下，间隙锁定在搜索和索引扫描中被禁用，并且仅用于外键约束检查和重复键检查。

使用 READ COMMITTED 隔离级别还有其他影响。在 MySQL 评估 WHERE 条件后，释放不匹配行的记录锁。对于 UPDATE 语句，InnoDB 执行“半一致”读取，这样它将最新提交的版本返回给 MySQL，以便 MySQL 可以确定该行是否匹配 UPDATE 的 WHERE 条件。

### Next-Key Lock的规则

下一个键锁是索引记录上的记录锁和索引记录之前的间隙上的间隙锁的组合。

InnoDB 执行行级锁定的方式是，当它搜索或扫描表索引时，它会在遇到的索引记录上设置共享或排他锁。 因此，行级锁实际上是索引记录锁。 索引记录上的下一个键锁也会影响该索引记录之前的“间隙”。 也就是说，下一个键锁是索引记录锁加上索引记录之前的间隙上的间隙锁。 如果一个会话在索引中的记录 R 上具有共享锁或排他锁，则另一个会话不能在索引顺序中紧接在 R 之前的间隙中插入新的索引记录。

假设一个索引包含值 10、11、13 和 20。该索引可能的 next-key 锁涵盖以下区间，其中圆括号表示排除区间端点，方括号表示包含端点：

```sql
(negative infinity, 10]
(10, 11]
(11, 13]
(13, 20]
(20, positive infinity)
```

对于最后一个时间间隔，next-key 锁锁定索引中最大值上方的间隙以及具有比索引中实际值更高的值的“supremum”伪记录。 supremum 不是真正的索引记录，因此，实际上，这个下一个键锁只锁定最大索引值之后的间隙。

默认情况下，`InnoDB` 在 `REPEATABLE READ` 事务隔离级别运行。在这种情况下，`InnoDB` 使用 next-key 锁进行搜索和索引扫描，以防止出现幻象行。

下一键锁的事务数据在 [`SHOW ENGINE INNODB STATUS`](https://dev.mysql.com/doc/refman/8.0/en/show-engine.html) 和 [InnoDB 监视器](https://dev.mysql.com/doc/refman/8.0/en/innodb-standard-monitor.html) 输出：

```sql
RECORD LOCKS space id 58 page no 3 n bits 72 index `PRIMARY` of table `test`.`t`
trx id 10080 lock_mode X
Record lock, heap no 1 PHYSICAL RECORD: n_fields 1; compact format; info bits 0
 0: len 8; hex 73757072656d756d; asc supremum;;

Record lock, heap no 2 PHYSICAL RECORD: n_fields 3; compact format; info bits 0
 0: len 4; hex 8000000a; asc     ;;
 1: len 6; hex 00000000274f; asc     'O;;
 2: len 7; hex b60000019d0110; asc        ;;
```

> 前提：因为间隙锁在InnoDB、可重复读隔离级别下才有效，所以接下来的描述，若没有特殊说明，默认是可重复读隔离级别。
>
> Next-Key Lock为Record Lock + Gap Lock
>
> 原则 1：加锁的基本单位是 next-key lock。next-key lock 是前开后闭区间。
> 原则 2：只有访问到的对象才会加锁。
> 优化 1：索引上的等值查询，
>         命中唯一索引，退化为行锁。
>         命中普通索引，左右两边的GAP Lock + Record Lock。
> 优化 2：
>         索引上的等值查询，未命中，所在的Net-Key Lock，退化为GAP Lock 。
>         索引在范围查询：
>         1.等值和范围分开判断。
>         2.索引在范围查询的时候 都会访问到所在区间不满足条件的第一个值为止。
>         3.如果使用了倒叙排序，按照倒叙排序后:
>             检索范围的右边多加一个GAP。
>             哪个方向还有命中的等值判断，再向同方向拓展外开里闭的区间。

### Insert Intention Locks

插入意图锁是一种在行插入之前由 INSERT 操作设置的间隙锁。 这个锁表示插入的意图，这样插入到同一个索引间隙中的多个事务如果没有插入到间隙中的相同位置，则不需要相互等待。 假设有值为 4 和 7 的索引记录。分别尝试插入值 5 和 6 的单独事务，在获得对插入行的排他锁之前，每个事务都使用插入意向锁锁定 4 和 7 之间的间隙， 但不要互相阻塞，因为这些行是不冲突的。

以下示例演示了一个事务在获取插入记录的独占锁之前获取插入意向锁。 该示例涉及两个客户端 A 和 B。

客户端A创建了一个包含两条索引记录（90和102）的表，然后启动一个事务，在ID大于100的索引记录上放置排他锁。排他锁包括记录102之前的间隙锁：

```sql
mysql> CREATE TABLE child (id int(11) NOT NULL, PRIMARY KEY(id)) ENGINE=InnoDB;
mysql> INSERT INTO child (id) values (90),(102);

mysql> START TRANSACTION;
mysql> SELECT * FROM child WHERE id > 100 FOR UPDATE;
+-----+
| id  |
+-----+
| 102 |
+-----+
```

客户端 B 开始事务以将记录插入间隙。 事务在等待获得独占锁时获取插入意向锁。

```sql
mysql> START TRANSACTION;
mysql> INSERT INTO child (id) VALUES (101);
```

插入意向锁的事务数据在 [`SHOW ENGINE INNODB STATUS`](https://dev.mysql.com/doc/refman/8.0/en/show-engine.html) 和 [InnoDB 监视器 ](https://dev.mysql.com/doc/refman/8.0/en/innodb-standard-monitor.html) 输出：

```sql
RECORD LOCKS space id 31 page no 3 n bits 72 index `PRIMARY` of table `test`.`child`
trx id 8731 lock_mode X locks gap before rec insert intention waiting
Record lock, heap no 3 PHYSICAL RECORD: n_fields 3; compact format; info bits 0
 0: len 4; hex 80000066; asc    f;;
 1: len 6; hex 000000002215; asc     " ;;
 2: len 7; hex 9000000172011c; asc     r  ;;...
```

### AUTO-INC Locks

AUTO-INC 锁是一种特殊的表级锁，由插入到具有 AUTO_INCREMENT 列的表中的事务获取。 在最简单的情况下，如果一个事务正在向表中插入值，则任何其他事务必须等待自己向该表中插入，以便第一个事务插入的行接收连续的主键值。

innodb_autoinc_lock_mode 变量控制用于自动增量锁定的算法。 它允许您选择如何在可预测的自动增量值序列和插入操作的最大并发性之间进行权衡。

### Predicate Locks for Spatial Indexes

InnoDB 支持包含空间数据的列的空间索引。

为了处理涉及 SPATIAL 索引的操作的锁定，next-key 锁定不能很好地支持 REPEATABLE READ 或 SERIALIZABLE 事务隔离级别。 多维数据没有绝对排序的概念，所以不清楚哪个是“下一个”键。

为了支持具有 SPATIAL 索引的表的隔离级别，InnoDB 使用谓词锁。 SPATIAL 索引包含最小边界矩形 (MBR) 值，因此 InnoDB 通过在用于查询的 MBR 值上设置谓词锁来强制对索引进行一致读取。 其他事务无法插入或修改与查询条件匹配的行。


