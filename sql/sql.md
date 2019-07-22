## sql

### 关联查询

```sql
    内连接：
    SELECT * from t_customer c INNER JOIN t_linkman l on c.cid=l.clid

    左连接：
    SELECT * from t_customer c LEFT JOIN t_linkman l on c.cid=l.clid

    右连接：
    SELECT * from t_customer c RIGHT JOIN t_linkman l on c.cid=l.clid

    完全连接：
    SELECT * from t_customer c FULL JOIN t_linkman l on c.cid=l.clid

    内连接更新：
    UPDATE t_customer INNER JOIN t_linkman on t_customer.cid = t_linkman.clid SET t_customer.custLevel='99'
```




