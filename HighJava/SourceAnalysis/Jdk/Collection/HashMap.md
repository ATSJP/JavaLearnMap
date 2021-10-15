[TOC]



#  HashMap









## 特点

- 基于Map接口；
- 允许Key和Value都允许为null；
- 非同步；
- 不保证顺序和插入时相同；
- 不保证顺序时不变。

## 原理

### 流程图

```flow
st=>start: start

c1=>condition: table是否未初始化
c2=>condition: table[i]是否为null
c3=>condition: table[i]的hash、Key是否与当前加入节点的hash、key一样
c4=>condition: tablke[i]是否TreeNode类型
c5=>condition: 当前节点是否为null
c6=>condition: 当前链表的节点是否超过TREEIFY_THRESHOLD
c7=>condition: 当前节点的hash、key是否一样
c8=>condition: e是否为Null

o1=>operation: 进行resize
o2=>operation: 根据Hash结果找到table[i]
o3=>operation: newNode放入table[i]
o4=>operation: 将当前值复制给e
o5=>operation: 使用TreeNode方式put节点
o6=>operation: 使用链表方式put节点
o7=>operation: 取链表的第一个节点
o8=>operation: newNode放入p.next
o9=>operation: 对链表进行treeifyBin（即进行链表转树）
o10=>operation: 已找到当前节点等于目标节点，break
o11=>operation: 取链表下一节点
o12=>operation: 替换e的值并返回e的旧值

end=>end: end

st->c1
c1(yes)->o1->o2
c1(no)->o2
o2->c2
c2(yes)->o3
c2(no)->c3
c3(yes)->o4
c3(no)->c4
c4(yes)->o5
c4(no)->o6
o6->o7->c5
c5(yes)->o8->c6(yes)->o9
c5(no)->c7
c7(yes)->o10
c7(no)->o11->c5
o4->c8
o5->c8
o10->c8
o9->c8
c8(yes)->o12

c5->end
```



![1](HashMap.assets/640.jpg)



### Hash如何计算

```java
    static final int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }
```

### HashMap的长度为什么是2<sup>n</sup>

答案：为了能让 HashMap 存取高效，尽量较少碰撞，也就是要尽量把数据分配均匀。



看了上面一句话，大家可能并不能理解具体原因，下面就带着大伙根据源码解释下上面的这句话。首先，大家都知道HashMap内部是维护了一个Node数组，每次根据key查询value，都需要计算key的hash，然后在根据hash去计算Node数组的下标获取对应的value，先分析下HashMap现在是如何计算下标的。

#### 计算高效

Hash值的范围值-2147483648到2147483647，前后加起来大概40亿的映射空间，只要哈希函数映射得比较均匀松散，一般应用是很难出现碰撞的。但问题是一个40亿长度的数组，内存是放不下的。所以这个散列值是不能直接拿来用的。

那怎么设计呢？很多人可能想到**取余**（`hash % length`）这个方法，固然可以，但是不高效呀，所以通过翻阅HashMap源码，我们得知了Jdk维护者选择了`(n - 1) & hash`这个计算公式（PS：见下方`getNode`方法），n代表数组长度。

```java
		public V get(Object key) {
        Node<K,V> e;
        return (e = getNode(hash(key), key)) == null ? null : e.value;
    }

    final Node<K,V> getNode(int hash, Object key) {
        Node<K,V>[] tab; Node<K,V> first, e; int n; K k;
        if ((tab = table) != null && (n = tab.length) > 0 &&
            // 此处计算下标的公式为(n - 1) & hash
            (first = tab[(n - 1) & hash]) != null) {
            // 1、检查Hash是否相等 2、检查Object是否相等
            if (first.hash == hash && // always check first node
                ((k = first.key) == key || (key != null && key.equals(k))))
                return first;
            // 检查节点的下一个是否存在
            if ((e = first.next) != null) {
                // 检查节点是否为树
                if (first instanceof TreeNode)
                    return ((TreeNode<K,V>)first).getTreeNode(hash, key);
                do {
                    // 如果是链表，则按顺序遍历，直到找到
                    if (e.hash == hash &&
                        ((k = e.key) == key || (key != null && key.equals(k))))
                        return e;
                } while ((e = e.next) != null);
            }
        }
        return null;
    }
```

等等，大伙此时肯定想问，啊，这个公式就能说明HashMap的长度是2<sup>n</sup>了吗？是的，就是他，但是为什么呢，咱们继续往下看。

#### 降低碰撞

大伙都知道，HashMap高不高效，其中一个最重要的因素便是Hash算法要好。作为Jdk的维护者肯定也知道这一点，所以在设计HashMap的时候，将这一点完全托付给了Hash算法，即相信Hash算法能够设计好。而HashMap计算下标，则以不影响Hash算法结果的目的而设计，故采用了HashMap的长度尽量是2<sup>n</sup>。

对于HashMap计算下标的公式：`(n - 1) & hash`，我们假设一下：

n是奇数，则n-1为偶数。比如n=5，n-1转为二进制则为100；

n是偶数，且不是2的幂次方，则n-1为奇数。比如n=6，n-1转为二进制则为101；

n是偶数，且是2的幂次方，则n-1为奇数。比如n=4，n-1转为二进制则为11，假设n=8呢，n-1转为二进制则为111。

不知道大伙有没有发现规律，只要是2<sup>n</sup>-1，转为二进制，所有位置上均为1，1与任何数的结果都是任何数。哇，巧妙不，没有影响hash值，而且还按照预期要求，保留了自己需要的低位数据。

到此为止，基本上讲清楚了为啥HashMap的长度是2<sup>n</sup>了。

## 源码

### TreeNode.find

```java
/**
* 这个方法是TreeNode类的一个实例方法，调用该方法的也就是一个TreeNode对象，
* 该对象就是树上的某个节点，以该节点作为根节点，查找其所有子孙节点，
* 看看哪个节点能够匹配上给定的键对象
* h k的hash值
* k 要查找的对象
* kc k的Class对象，该Class应该是实现了Comparable<K>的，否则应该是null，参见：
*/
final TreeNode<K,V> find(int h, Object k, Class<?> kc) {
    TreeNode<K,V> p = this; // 把当前对象赋给p，表示当前节点
    do { // 循环
        int ph, dir; K pk; // 定义当前节点的hash值、方向（左右）、当前节点的键对象
        TreeNode<K,V> pl = p.left, pr = p.right, q; // 获取当前节点的左孩子、右孩子。定义一个对象q用来存储并返回找到的对象
        if ((ph = p.hash) > h) // 如果当前节点的hash值大于k得hash值h，那么后续就应该让k和左孩子节点进行下一轮比较
            p = pl; // p指向左孩子，紧接着就是下一轮循环了
        else if (ph < h) // 如果当前节点的hash值小于k得hash值h，那么后续就应该让k和右孩子节点进行下一轮比较
            p = pr; // p指向右孩子，紧接着就是下一轮循环了
        else if ((pk = p.key) == k || (k != null && k.equals(pk))) // 如果h和当前节点的hash值相同，并且当前节点的键对象pk和k相等（地址相同或者equals相同）
            return p; // 返回当前节点
 
        // 执行到这里说明 hash比对相同，但是pk和k不相等
        else if (pl == null) // 如果左孩子为空
            p = pr; // p指向右孩子，紧接着就是下一轮循环了
        else if (pr == null)
            p = pl; // p指向左孩子，紧接着就是下一轮循环了
 
        // 如果左右孩子都不为空，那么需要再进行一轮对比来确定到底该往哪个方向去深入对比
        // 这一轮的对比主要是想通过comparable方法来比较pk和k的大小     
        else if ((kc != null ||
                    (kc = comparableClassFor(k)) != null) &&
                    (dir = compareComparables(kc, k, pk)) != 0)
            p = (dir < 0) ? pl : pr; // dir小于0，p指向右孩子，否则指向右孩子。紧接着就是下一轮循环了
 
        // 执行到这里说明无法通过comparable比较  或者 比较之后还是相等
        // 从右孩子节点递归循环查找，如果找到了匹配的则返回    
        else if ((q = pr.find(h, k, kc)) != null) 
            return q;
        else // 如果从右孩子节点递归查找后仍未找到，那么从左孩子节点进行下一轮循环
            p = pl;
    } while (p != null); 
    return null; // 为找到匹配的节点返回null
}

/**
  * 将root移到链表的第一个节点
  * Ensures that the given root is the first node of its bin.
  */
static <K,V> void moveRootToFront(Node<K,V>[] tab, TreeNode<K,V> root) {
  int n;
  if (root != null && tab != null && (n = tab.length) > 0) {
    int index = (n - 1) & root.hash;
    TreeNode<K,V> first = (TreeNode<K,V>)tab[index];
    // 判断是否是同一个对象，即已经是第一个节点了
    if (root != first) {
      Node<K,V> rn;
      tab[index] = root;
      // 这里就是链表的节点移动操作
      // 取出root的上一节点
      TreeNode<K,V> rp = root.prev;
      // 取出root的下一节点，且将下一节点的prev指向root的上一节点
      if ((rn = root.next) != null)
        ((TreeNode<K,V>)rn).prev = rp;
      // root的上一节点的next指向root的下一节点
      if (rp != null)
        rp.next = rn;
      // 原本第一个节点的prev指向root
      if (first != null)
        first.prev = root;
      // root的next指向原本第一个节点，prev指向null
      root.next = first;
      root.prev = null;
    }
    // 断言检查是否已经符合要求
    assert checkInvariants(root);
  }
}
```



