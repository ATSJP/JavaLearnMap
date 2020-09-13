## 什么是逃逸分析

在编译程序优化理论中，逃逸分析是一种确定指针动态范围的方法——分析在程序的哪些地方可以访问到指针。它涉及到指针分析和形状分析。

当一个变量(或对象)在子程序中被分配时，一个指向变量的指针可能逃逸到其它执行线程中，或是返回到调用者子程序。如果使用[尾递归优化](https://link.zhihu.com/?target=https%3A//zh.wikipedia.org/w/index.php%3Ftitle%3D%E5%B0%BE%E9%80%92%E5%BD%92%E4%BC%98%E5%8C%96%26action%3Dedit%26redlink%3D1)（通常在[函数编程语言](https://link.zhihu.com/?target=https%3A//zh.wikipedia.org/wiki/%E5%87%BD%E6%95%B8%E7%A8%8B%E5%BC%8F%E8%AA%9E%E8%A8%80)中是需要的），对象也可以看作逃逸到被调用的子程序中。如果一种语言支持第一类型的[延续性](https://link.zhihu.com/?target=https%3A//zh.wikipedia.org/wiki/%E5%BB%B6%E7%BA%8C%E6%80%A7)在Scheme和Standard ML of New Jersey中同样如此），部分[调用栈](https://link.zhihu.com/?target=https%3A//zh.wikipedia.org/wiki/%E8%B0%83%E7%94%A8%E6%A0%88)也可能发生逃逸。

如果一个子程序分配一个对象并返回一个该对象的指针，该对象可能在程序中被访问到的地方无法确定——这样指针就成功“逃逸”了。如果指针存储在全局变量或者其它数据结构中，因为全局变量是可以在当前子程序之外访问的，此时指针也发生了逃逸。

逃逸分析确定某个指针可以存储的所有地方，以及确定能否保证指针的生命周期只在当前进程或在其它线程中。

下面我们看看Java中的逃逸分析是怎样的？

Java的逃逸分析只发在JIT的即时编译中，为什么不在前期的静态编译中就进行呢，知乎上已经有过这样的提问。

[逃逸分析为何不能在编译期进行？www.zhihu.com![图标](https://zhstatic.zhihu.com/assets/zhihu/editor/zhihu-card-default.svg)](https://www.zhihu.com/question/27963717)

简单来说是可以的，但是Java的分离编译和动态加载使得前期的静态编译的逃逸分析比较困难或收益较少，所以目前Java的逃逸分析只发在JIT的即时编译中，因为收集到足够的运行数据JVM可以更好的判断对象是否发生了逃逸。关于JIT即时编译可参考[JVM系列之走进JIT](https://zhuanlan.zhihu.com/p/55751295)。

JVM判断新创建的对象是否逃逸的依据有：**一、对象被赋值给堆中对象的字段和类的静态变量。二、对象被传进了不确定的代码中去运行。**

如果满足了以上情况的任意一种，那这个对象JVM就会判定为逃逸。对于第一种情况，因为对象被放进堆中，则其它线程就可以对其进行访问，所以对象的使用情况，编译器就无法再进行追踪。第二种情况相当于JVM在解析普通的字节码的时候，如果没有发生JIT即时编译，编译器是不能事先完整知道这段代码会对对象做什么操作。保守一点，这个时候也只能把对象是当作是逃逸来处理。下面举几个例子

```java
public class EscapeTest {

    public static Object globalVariableObject;

    public Object instanceObject;

    public void globalVariableEscape(){
        globalVariableObject = new Object(); //静态变量,外部线程可见,发生逃逸
    }

    public void instanceObjectEscape(){
        instanceObject = new Object(); //赋值给堆中实例字段,外部线程可见,发生逃逸
    }
    
    public Object returnObjectEscape(){
        return new Object();  //返回实例,外部线程可见，发生逃逸
    }

    public void noEscape(){
        synchronized (new Object()){
            //仅创建线程可见,对象无逃逸
        }
        Object noEscape = new Object();  //仅创建线程可见,对象无逃逸
    }

}
```



## 基于逃逸分析的优化

当判断出对象不发生逃逸时，编译器可以使用逃逸分析的结果作一些代码优化

- 将堆分配转化为栈分配。如果某个对象在子程序中被分配，并且指向该对象的指针永远不会逃逸，该对象就可以在分配在栈上，而不是在堆上。在有垃圾收集的语言中，这种优化可以降低垃圾收集器运行的频率。
- 同步消除。如果发现某个对象只能从一个线程可访问，那么在这个对象上的操作可以不需要同步。
- 分离对象或标量替换。如果某个对象的访问方式不要求该对象是一个连续的内存结构，那么对象的部分（或全部）可以不存储在内存，而是存储在CPU寄存器中。



对于优化一将堆分配转化为栈分配，这个优化也很好理解。下面以代码例子说明：

虚拟机配置参数：-XX:+PrintGC -Xms5M -Xmn5M -XX:+DoEscapeAnalysis

-XX:+DoEscapeAnalysis表示开启逃逸分析，JDK8是默认开启的

-XX:+PrintGC 表示打印GC信息

-Xms5M -Xmn5M 设置JVM内存大小是5M

```java
    public static void main(String[] args){
        for(int i = 0; i < 5_000_000; i++){
            createObject();
        }
    }

    public static void createObject(){
        new Object();
    }
```

运行结果是没有GC。

把虚拟机参数改成 -XX:+PrintGC -Xms5M -Xmn5M -XX:-DoEscapeAnalysis。关闭逃逸分析得到结果的部分截图是，说明了进行了GC，并且次数还不少。

```java
[GC (Allocation Failure)  4096K->504K(5632K), 0.0012864 secs]
[GC (Allocation Failure)  4600K->456K(5632K), 0.0008329 secs]
[GC (Allocation Failure)  4552K->424K(5632K), 0.0006392 secs]
[GC (Allocation Failure)  4520K->440K(5632K), 0.0007061 secs]
[GC (Allocation Failure)  4536K->456K(5632K), 0.0009787 secs]
[GC (Allocation Failure)  4552K->440K(5632K), 0.0007206 secs]
[GC (Allocation Failure)  4536K->520K(5632K), 0.0009295 secs]
[GC (Allocation Failure)  4616K->512K(4608K), 0.0005874 secs]
```

这说明了JVM在逃逸分析之后，将对象分配在了方法createObject()方法栈上。**方法栈上的对象在方法执行完之后，栈桢弹出，对象就会自动回收。这样的话就不需要等内存满时再触发内存回收。这样的好处是**程序内存回收效率高，并且GC频率也会减少**，程序的性能就提高了。**

优化二 同步锁消除

**如果发现某个对象只能从一个线程可访问，那么在这个对象上的操作可以不需要同步**。

虚拟机配置参数：-XX:+PrintGC -Xms500M -Xmn500M -XX:+DoEscapeAnalysis。配置500M是保证不触发GC。

```java
public static void main(String[] args){
        long start = System.currentTimeMillis();
        for(int i = 0; i < 5_000_000; i++){
            createObject();
        }
        System.out.println("cost = " + (System.currentTimeMillis() - start) + "ms");
    }

    public static void createObject(){
        synchronized (new Object()){

        }
    }
```

运行结果

```java
cost = 6ms
```

把逃逸分析关掉：-XX:+PrintGC -Xms500M -Xmn500M -XX:-DoEscapeAnalysis

运行结果

```java
cost = 270ms
```

**说明了逃逸分析把锁消除了，并在性能上得到了很大的提升。这里说明一下Java的逃逸分析是方法级别的，因为JIT的即时编译是方法级别。**

优点三 分离对象或标量替换。

**这个简单来说就是把对象分解成一个个基本类型，并且内存分配不再是分配在堆上，而是分配在栈上。这样的好处有，一、减少内存使用，因为不用生成对象头。 二、程序内存回收效率高，并且GC频率也会减少，总的来说和上面优点一的效果差不多**。