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
        // key为null，hash为0，否则，取Object的hashCode，然后取hashCode的高位，与自己异或
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }
```

1、为什么要右移?

   尽可能的防止Hash碰撞，本身具有高随机性的HashCode，在高低位异或一下，一定程度上能增强随机性。

2、为什么要用异或？
   A、效率高

   B、对比效率高的操作还有位与( & )、位或( | )。

​      假设是位与，则0-0=0，0-1=0，1-0=0，1-1=1，可知75%的概率位0，25%概率位1，概率不均。

​      假设是位或，则0-0=0，0-1=1，1-0=1，1-1=1，可知75%的概率位1，25%概率位0，概率不均。

​      假设是异或，则0-0=1，0-1=0，1-0=0，1-1=1，可知50%的概率位1，50%概率位0，概率均等。

### HashMap的长度为什么是2<sup>n</sup>

```java
    /**
     * The maximum capacity, used if a higher value is implicitly specified
     * by either of the constructors with arguments.
     * MUST be a power of two <= 1<<30.
     */
    static final int MAXIMUM_CAPACITY = 1 << 30;
```

答案：为了能让 HashMap 存取高效，尽量较少碰撞，也就是要尽量把数据分配均匀。

看了上面一句话，大家可能并不能理解具体原因，下面就带着大伙根据源码解释下上面的这句话。首先，大家都知道HashMap内部是维护了一个Node数组，每次根据key查询value，都需要计算key的hash，然后在根据hash去计算Node数组的下标获取对应的value，先分析下HashMap现在是如何计算下标的。

#### 计算高效

Hash值的范围值-2147483648到2147483647，前后加起来大概40亿的映射空间，只要哈希函数映射得比较均匀松散，一般应用是很难出现碰撞的。但问题是一个40亿长度的数组，内存是放不下的。所以这个散列值是不能直接拿来用的。

那怎么设计呢？很多人可能想到**取余**（`hash % length`）这个方法，固然可以，但是不高效呀。所以通过翻阅HashMap源码，我们得知了Jdk维护者选择了`(n - 1) & hash`这个计算公式（PS：见下方`getNode`方法），n代表数组长度。

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



