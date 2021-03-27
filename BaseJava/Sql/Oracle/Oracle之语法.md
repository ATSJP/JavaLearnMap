

## 

### 排名函数

#### ROW_NUMBER()

**定义**：ROW_NUMBER()函数作用就是将select查询到的数据进行排序，每一条数据加一个序号，他不能用做于学生成绩的排名，一般多用于分页查询， 

#### RANK() 

**定义**：RANK()函数，顾名思义排名函数，可以对某一个字段进行排名，这里为什么和ROW_NUMBER()不一样那，ROW_NUMBER()是排序，当存在相同成绩的学生时，ROW_NUMBER()会依次进行排序，他们序号不相同，而Rank()则不一样出现相同的，他们的排名是一样的。

#### DENSE_RANK() 

**定义**：DENSE_RANK()函数也是排名函数，和RANK()功能相似，也是对字段进行排名，那它和RANK()到底有什么不同那？看例子

DENSE_RANK()密集的排名他和RANK()区别在于，排名的连续性，DENSE_RANK()排名是连续的，RANK()是跳跃的排名，所以一般情况下用的排名函数就是RANK()。

#### NTILE()
定义：NTILE()函数是将有序分区中的行分发到指定数目的组中，各个组有编号，编号从1开始，就像我们说的’分区’一样 ，分为几个区，一个区会有多少个。

### 分区分组

#### Partition By

分组，可以联合排名函数使用：

```sql
ROW_NUMBER() OVER(PARTITION BY <cloumn> ORDER BY <cloumn> DESC)
```

#### Group By

分区



