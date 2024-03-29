[TOC]



## Synchronized 关键字

> Synchronized关键字在需要原子性、可见性和有序性这三种特性的时候都可以作为其中一种解决方案。

### 简介

Synchronized是**内置锁**，是Java语言特性提供的内置锁，其获得锁和释放锁是隐式的（进入代码块就是获得锁，走出代码就是释放锁，`java.util.concurrent.locks `包中的锁是**显示锁**，需要进行lock和unlock）。

### 使用

#### 正确使用

| 修饰目标 |                | 锁                         |
| :------- | -------------- | -------------------------- |
| 方法     | 实例方法       | 当前实例对象(即方法调用者) |
|          | 静态方法       | 类对象                     |
| 代码块   | this           | 当前实例对象(即方法调用者) |
|          | class对象      | 类对象                     |
|          | 任意Object对象 | 任意实例对象               |

如下:

```java
public class SynchronizedTest {
 
    // 实例方法
    public synchronized void instanceMethod() {
       
    }

    // 静态方法
    public synchronized static void staticMethod() {
        
    }

    public void thisMethod() {
        // this对象
        synchronized (this) {
            
        }
    }

    public void classMethod() {
        // class对象
        synchronized (SynchronizedTest.class) {
            
        }
    }

    public void anyObject() {
        // 任意对象
        synchronized ("anything") {
            
        }
    }
}
```

#### 错误使用

Synchronized不能被继承；

不能使用Synchronized关键字修饰接口方法；

构造方法也不能用Synchronized。

### 特点

1. 可重入性
2. 当代码段执行结束或出现异常后会自动释放对监视器的锁定
3. 是非公平锁，在等待获取锁的过程中不可被中断
4. Synchronized的内存语义
5. 互斥性，被Synchronized修饰的方法同时只能由一个线程执行

**证明**

1、如何证明可重入性

何为可重入锁？

答：当一个线程再次请求自己持有对象锁的临界资源时，这种情况属于重入锁。

在Java中Synchronized是基于原子性的内部锁机制，是可重入的，因此在一个线程调用Synchronized方法的同时在其方法体内部调用该对象另一个Synchronized方法，也就是说一个线程得到一个对象锁后再次请求该对象锁，是允许的，这就是Synchronized的可重入性。

如下示例：

```java
public class SynchronizedReentrantTest implements Runnable {
    static SynchronizedReentrantTest instance = new SynchronizedReentrantTest();
    static int i = 0;
    static int j = 0;

    @Override
    public void run() {
        for (int j = 0; j < 1000000; j++) {
            // this,当前实例对象锁
            synchronized (this) {
                i++;
                // 允许,synchronized的可重入性
                increase();
            }
        }
    }

    public synchronized void increase() {
        j++;
    }

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(instance);
        Thread t2 = new Thread(instance);
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        System.out.println(i);
    }
}
```

需要特别注意另外一种情况，当子类继承父类时，**子类也是可以通过可重入锁调用父类的同步方法**。

2、如何理解Synchronized是非公平锁，在等待获取锁的过程中不可被中断这句话

>何为公平锁？
>
>答案：公平锁是指多个线程按照申请锁的顺序来获取锁，类似排队打饭，先来后到。
>
>何为非公平锁？
>
>答案：非公平锁是指多个线程获取锁的顺序并不是按照申请锁的顺序，有可能后申请的线程比先申请的线程优先获取锁在高并发的情况下，有可能会造成优先级反转或者饥饿现象(饥饿现象是指一个线程长时间获取不到锁，一直处理等待状态)

Synchronized是非公平锁，其实对于Synchronized来说，如果一个线程在等待锁，那么结果只有两种，要么它获得这把锁继续执行，要么它就保存等待，**即使调用中断线程的方法，也不会生效。**

示例：

```java
public class SynchronizedBlocked implements Runnable {

    public synchronized void f() {
        System.out.println("Trying to call f()");
        while (true) {  // 从未释放锁
            // 当前线程由执行状态，变成为就绪状态，让出cpu时间，在下一个线程执行时候，此线程有可能被执行，也有可能没有被执行。
            Thread.yield();
        }
    }

    /**
     * 在构造器中创建新线程并启动获取对象锁
     */
    public SynchronizedBlocked() {
        //  Lock acquired by this thread
        new Thread(this::f).start();
    }

    public void run() {
        while (true) {
            // 中断判断
            if (Thread.interrupted()) {
                System.out.println("中断线程!!");
                break;
            } else {
                System.out.println("wait!!");
                f();
                System.out.println("end!!");
            }
        }
    }

    public static void main(String args[]) throws InterruptedException {
        SynchronizedBlocked sync = new SynchronizedBlocked();
        Thread t = new Thread(sync);
        // 启动后调用f()方法,无法获取当前实例锁处于等待状态
        t.start();
        TimeUnit.SECONDS.sleep(1);
        // 中断线程,无法生效
        t.interrupt();
    }
}
```

控制台打印：(打印Wait后一直在运行)

````text
Trying to call f()
wait!!
````

### 原理

**面试版总结**：Synchronized底层实际上是通过Monitor(管程)实现的，锁的信息存在对象头的MarkWord中。

**理解性总结**：Synchronized底层实际上是通过Monitor(管程)实现的（Java转汇编后，又是通过`ACC_SYNCHRONIZED`标识符和`monitorenter` 、`monitorexit`指令来获取Monitor的），锁的信息存在对象头的MarkWord中(其中对象头分两部分数据，一部分用于存储对象自身的运行时数据，官方叫做"Mark Word"，另一部分是类型指针，即对象指向它的类元数据的指针，虚拟机通过这个指针来确定这个对象是哪个类的实例，如果是数组对象的话，还会有一个额外的部分用于存储数组长度)。

在概念不太清晰的情况下，看到上述总结可能会有点模糊，何为Monitor管程？`ACC_SYNCHRONIZED`标识符和`monitorenter` 、`monitorexit`指令又是啥？居然还有MarkWord，这是何方妖怪？下面我们一一来看。

#### Java转汇编

首先第一个要搞明白的问题是：`ACC_SYNCHRONIZED`标识符和`monitorenter` 、`monitorexit`指令是啥，和Synchronized有关系吗？

来上示例，助于理解。

**同步代码块-示例1**：

```java
public class SyncSourceAnalysis {
    synchronized void hello() {

    }

    public static void main(String[] args) {
        String anything = "anything";
        synchronized (anything) {
            System.out.println("hello word");
        }
    }
}
```

使用`javap -c SyncSourceAnalysis.class`反编译生成汇编代码，从下面源码可以看见`monitorenter`、`monitorexit`指令，`monitorexit`存在两条是因为第一条是正常程序退出，第二条是异常情况。

```asm
Compiled from "SyncSourceAnalysis.java"
public class com.lemon.web.user.SyncSourceAnalysis {
  public com.lemon.web.user.SyncSourceAnalysis();
    Code:
       0: aload_0
       1: invokespecial #1                  // Method java/lang/Object."<init>":()V
       4: return

  synchronized void hello();
    Code:
       0: return

  public static void main(java.lang.String[]);
    Code:
       0: ldc           #2                  // String anything
       2: astore_1
       3: aload_1
       4: dup
       5: astore_2
       6: monitorenter
       7: getstatic     #3                  // Field java/lang/System.out:Ljava/io/PrintStream;
      10: ldc           #4                  // String hello word
      12: invokevirtual #5                  // Method java/io/PrintStream.println:(Ljava/lang/String;)V
      15: aload_2
      16: monitorexit
      17: goto          25
      20: astore_3
      21: aload_2
      22: monitorexit
      23: aload_3
      24: athrow
      25: return
    Exception table:
       from    to  target type
           7    17    20   any
          20    23    20   any
}
```

1. `monitorenter`：每个对象维护着一个监视器锁（Monitor）。当Monitor被占用时就会处于锁定状态，线程执行`monitorenter`指令时尝试获取Monitor的所有权，过程如下：

   > 1. 如果Monitor的进入数为0，则该线程进入Monitor，然后将进入数设置为1，该线程即为Monitor的所有者；
   > 2. 如果线程已经占有该Monitor，只是重新进入，则进入Monitor的进入数加1；
   > 3. 如果其他线程已经占用了Monitor，则该线程进入阻塞状态，直到Monitor的进入数为0，再重新尝试获取Monitor的所有权；

2. `monitorexit`：执行`monitorexit`的线程必须是`objectref`所对应的Monitor的所有者。指令执行时，Monitor的进入数减1，如果减1后进入数为0，那线程退出Monitor，不再是这个Monitor的所有者。其他被这个Monitor阻塞的线程可以尝试去获取这个 Monitor 的所有权。

总的来说，可以把执行`monitorenter`指令理解为加锁，执行`monitorexit`理解为释放锁。 每个对象维护着一个记录着被锁次数的计数器。未被锁定的对象的该计数器为0，当一个线程获得锁（执行`monitorenter`）后，该计数器自增变为 1 ，当同一个线程再次获得该对象的锁的时候，计数器再次自增。当同一个线程释放锁（执行`monitorexit`指令）的时候，计数器再自减。当计数器为0的时候，锁将被释放，其他线程便可以获得锁。

**同步方法-示例2**：

```java
public class SyncSourceAnalysis {
    public synchronized void method() {
        System.out.println("Hello World!");
    }
}
```

使用`javap -v SyncSourceAnalysis.class`反编译生成汇编代码，从汇编中可以见到方法进入多了一个`flag:ACC_SYNCHRONIZED`

```asm
Classfile /E:/SyncSourceAnalysis.class
  Last modified 2020-8-30; size 544 bytes
  MD5 checksum 22ebca5bb24ff26b4f77d38408ad2bd4
  Compiled from "SyncSourceAnalysis.java"
public class com.lemon.web.user.SyncSourceAnalysis
  minor version: 0
  major version: 52
  flags: ACC_PUBLIC, ACC_SUPER
Constant pool:
   #1 = Methodref          #6.#17         // java/lang/Object."<init>":()V
   #2 = Fieldref           #18.#19        // java/lang/System.out:Ljava/io/PrintStream;
   #3 = String             #20            // Hello World!
   #4 = Methodref          #21.#22        // java/io/PrintStream.println:(Ljava/lang/String;)V
   #5 = Class              #23            // com/lemon/web/user/SyncSourceAnalysis
   #6 = Class              #24            // java/lang/Object
   #7 = Utf8               <init>
   #8 = Utf8               ()V
   #9 = Utf8               Code
  #10 = Utf8               LineNumberTable
  #11 = Utf8               LocalVariableTable
  #12 = Utf8               this
  #13 = Utf8               Lcom/lemon/web/user/SyncSourceAnalysis;
  #14 = Utf8               method
  #15 = Utf8               SourceFile
  #16 = Utf8               SyncSourceAnalysis.java
  #17 = NameAndType        #7:#8          // "<init>":()V
  #18 = Class              #25            // java/lang/System
  #19 = NameAndType        #26:#27        // out:Ljava/io/PrintStream;
  #20 = Utf8               Hello World!
  #21 = Class              #28            // java/io/PrintStream
  #22 = NameAndType        #29:#30        // println:(Ljava/lang/String;)V
  #23 = Utf8               com/lemon/web/user/SyncSourceAnalysis
  #24 = Utf8               java/lang/Object
  #25 = Utf8               java/lang/System
  #26 = Utf8               out
  #27 = Utf8               Ljava/io/PrintStream;
  #28 = Utf8               java/io/PrintStream
  #29 = Utf8               println
  #30 = Utf8               (Ljava/lang/String;)V
{
  public com.lemon.web.user.SyncSourceAnalysis();
    descriptor: ()V
    flags: ACC_PUBLIC
    Code:
      stack=1, locals=1, args_size=1
         0: aload_0
         1: invokespecial #1                  // Method java/lang/Object."<init>":()V
         4: return
      LineNumberTable:
        line 8: 0
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            0       5     0  this   Lcom/lemon/web/user/SyncSourceAnalysis;

  public synchronized void method();
    descriptor: ()V
    flags: ACC_PUBLIC, ACC_SYNCHRONIZED
    Code:
      stack=2, locals=1, args_size=1
         0: getstatic     #2                  // Field java/lang/System.out:Ljava/io/PrintStream;
         3: ldc           #3                  // String Hello World!
         5: invokevirtual #4                  // Method java/io/PrintStream.println:(Ljava/lang/String;)V
         8: return
      LineNumberTable:
        line 10: 0
        line 11: 8
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            0       9     0  this   Lcom/lemon/web/user/SyncSourceAnalysis;
}
SourceFile: "SyncSourceAnalysis.java"
```

从编译的结果来看，方法的同步并没有通过指令 `monitorenter` 和 `monitorexit` 来完成（理论上其实也可以通过这两条指令来实现），不过相对于普通方法，其常量池中多了 `ACC_SYNCHRONIZED` 标示符。

> JVM就是根据该标示符来实现方法的同步的：
>
> 方法级的同步是隐式的。同步方法的常量池中会有一个`ACC_SYNCHRONIZED`标志。当某个线程要访问某个方法的时候，会检查是否有`ACC_SYNCHRONIZED`，如果有设置，则需要先获得监视器锁（Monitor），然后开始执行方法，方法执行之后再释放监视器锁（Monitor）。这时如果其他线程来请求执行方法，会因为无法获得监视器锁（Monitor）而被阻断住。值得注意的是，如果在方法执行过程中，发生了异常，并且方法内部并没有处理该异常，那么在异常被抛到方法外面之前监视器锁（Monitor）会被自动释放。

两种同步方式本质上没有区别，只是方法的同步是一种隐式的方式来实现，无需通过字节码来完成。两个指令的执行是JVM通过调用操作系统的互斥原语`mutex`来实现，被阻塞的线程会被挂起、等待重新调度，会导致“用户态和内核态”两个态之间来回切换，对性能有较大影响。

PS：通过上面两段描述，我们应该能很清楚的看出Synchronized和`ACC_SYNCHRONIZED`标识符及`monitorenter` 、`monitorexit`指令的关系，Java层级，他还是Synchronized实现锁，汇编层级，他就转化成了`ACC_SYNCHRONIZED`标识符及`monitorenter` 、`monitorexit`指令。

**小结**：

- 同步方法通过`ACC_SYNCHRONIZED`标记符隐式的对方法进行加锁。当线程要执行的方法被标注上`ACC_SYNCHRONIZED`时，需要先获得锁才能执行该方法。
- 同步代码块通过`monitorenter`和`monitorexit`执行来进行加锁。当线程执行到`monitorenter`的时候要先获得所锁，才能执行后面的方法。当线程执行到`monitorexit`的时候则要释放锁。每个对象自身维护这一个被加锁次数的计数器，当计数器数字为0时表示可以被任意线程获得锁。当计数器不为0时，只有获得锁的线程才能再次获得锁。即可重入锁。

PS：**Synchronized的语义底层是通过一个Monitor的对象来完成，其实`wait/notify`等方法也依赖于Monitor对象，这就是为什么只有在同步的块或者方法中才能调用`wait/notify`等方法，否则会抛出`java.lang.IllegalMonitorStateException`的异常的原因。**

#### MarkWord

在HotSpot虚拟机中，对象在内存中存储的布局如下图：

![img](Synchronized.assets/2062729-9a78f7ea7671a031.png)

> 引用《深入理解JAVA虚拟机》原文：
>
> 在 HotSpot虚拟机中，对象在内存中存储的布局可以分为3 块区域：对象头（Header）、实例数据（Instance Data）和对齐填充（Padding）。
>
> HotSpot虚拟机的对象头包括两部分信息，第一部分用于存储对象自身的运行时数据，如哈希码（HashCode)、GC 分代年龄、锁状态标志、线程持有的锁、偏向线程 ID、偏向时间戳等，这部分数据的长度在 32 位和 64 位的虚拟机（未开启压缩指针）中分别为 32bit 和64bit，官方称它为“Mark Word”。
>
> 对象需要存储的运行时数据很多，其实已经超出了 32 位、64 位Bitmap 结构所能记录的限度，但是对象头信息是与对象自身定义的数据无关的额外存储成本，考虑到虚拟机的空间效率，Mark Word 被设计成一个非固定的数据结构以便在极小的空间内存储尽量多的信息，它会根据对象的状态复用自己的存储空间。
>
> 例如，在 32 位的HotSpot 虚拟机中，如果对象处于未被锁定的状态下，那么 Mark Word 的 32bit 空间中的 25bit用于存储对象哈希码，4bit 用于存储对象分代年龄，2bit 用于存储锁标志位，1bit 固定为 0,而在其他状态（轻量级锁定、重量级锁定、GC 标记、可偏向）下对象的存储内容见表 13-1。
>
> ![img](Synchronized.assets/2062729-36035cd1936bd2c6.png)



#### Monitor

[飞机票](Monitor.md)

### 锁升级 &锁膨胀

锁主要存在四种状态，依次是**：无锁状态、偏向锁状态、轻量级锁状态、重量级锁状态**，锁可以从偏向锁升级到轻量级锁，再升级的重量级锁。但是锁的升级是单向的，也就是说只能从低到高升级，不会出现锁的降级。

在 JDK 1.6 中默认是开启偏向锁和轻量级锁的，可以通过-XX:-UseBiasedLocking来禁用偏向锁

#### 偏向锁

偏向锁是在单线程执行代码块时使用的机制，如果在多线程并发的环境下（即线程A尚未执行完同步代码块，线程B发起了申请锁的申请），则一定会转化为轻量级锁或者重量级锁。

具体处理过程：当一个线程访问同步块并获取锁时，会在对象头和栈帧中的锁记录里存储锁偏向的线程ID，以后该线程进入和退出同步块时不需要花费CAS操作来争夺锁资源，只需要检查是否为偏向锁、锁标识为以及ThreadID即可，处理流程如下：

1. 检测Mark Word是否为可偏向状态，即是否为偏向锁1，锁标识位为01；
2. 若为可偏向状态，则测试线程ID是否为当前线程ID，如果是，则执行步骤（5），否则执行步骤（3）；
3. 如果测试线程ID不为当前线程ID，则通过CAS操作竞争锁，竞争成功，则将Mark Word的线程ID替换为当前线程ID，否则执行线程（4）；
4. 通过CAS竞争锁失败，证明当前存在多线程竞争情况，当到达全局安全点，获得偏向锁的线程被挂起，偏向锁升级为轻量级锁，然后被阻塞在安全点的线程继续往下执行同步代码块；
5. 执行同步代码块；

偏向锁的释放采用了 一种只有竞争才会释放锁的机制，线程是不会主动去释放偏向锁，需要等待其他线程来竞争。偏向锁的撤销需要 等待全局安全点（这个时间点是上没有正在执行的代码）。其步骤如下：

1. 暂停拥有偏向锁的线程；
2. 判断锁对象是否还处于被锁定状态，否，则恢复到无锁状态（01），以允许其余线程竞争。是，则挂起持有锁的当前线程，并将指向当前线程的锁记录地址的指针放入对象头Mark Word，升级为轻量级锁状态（00），然后恢复持有锁的当前线程，进入轻量级锁的竞争模式；

#### 轻量锁

引入轻量级锁的主要目的是：在没有多线程竞争的前提下，减少传统的重量级锁使用操作系统互斥量产生的性能消耗。当关闭偏向锁功能或者多个线程竞争偏向锁导致偏向锁升级为轻量级锁，则会尝试获取轻量级锁，其步骤如下：

1. 在线程进入同步块时，如果同步对象锁状态为无锁状态（锁标志位为“01”状态，是否为偏向锁为“0”），虚拟机首先将在当前线程的栈帧中建立一个名为锁记录（Lock Record）的空间，用于存储锁对象目前的Mark Word的拷贝，官方称之为 Displaced Mark Word。此时线程堆栈与对象头的状态如下图所示：

    

   ![img](https://upload-images.jianshu.io/upload_images/2062729-7e332020d9f252db.png)

   轻量级锁CAS操作之前线程堆栈与对象的状态

2. 拷贝对象头中的Mark Word复制到锁记录（Lock Record）中；

3. 拷贝成功后，虚拟机将使用CAS操作尝试将对象Mark Word中的Lock Word更新为指向当前线程Lock Record的指针，并将Lock record里的owner指针指向object mark word。如果更新成功，则执行步骤（4），否则执行步骤（5）；

4. 如果这个更新动作成功了，那么当前线程就拥有了该对象的锁，并且对象Mark Word的锁标志位设置为“00”，即表示此对象处于轻量级锁定状态，此时线程堆栈与对象头的状态如下图所示：

    

   ![img](https://upload-images.jianshu.io/upload_images/2062729-5e935668eddb8fcd.png)

   轻量级锁CAS操作之后线程堆栈与对象的状态

5. 如果这个更新操作失败了，虚拟机首先会检查对象Mark Word中的Lock Word是否指向当前线程的栈帧，如果是，就说明当前线程已经拥有了这个对象的锁，那就可以直接进入同步块继续执行。否则说明多个线程竞争锁，进入自旋执行（3），若自旋结束时仍未获得锁，轻量级锁就要膨胀为重量级锁，锁标志的状态值变为“10”，Mark Word中存储的就是指向重量级锁（互斥量）的指针，当前线程以及后面等待锁的线程也要进入阻塞状态。

轻量级锁的释放也是通过CAS操作来进行的，主要步骤如下： 

1. 通过CAS操作尝试把线程中复制的Displaced Mark Word对象替换当前的Mark Word；
2. 如果替换成功，整个同步过程就完成了，恢复到无锁状态（01）；
3. 如果替换失败，说明有其他线程尝试过获取该锁（此时锁已膨胀），那就要在释放锁的同时，唤醒被挂起的线程；

#### 重量级锁

Synchronized是通过对象内部的一个叫做 监视器锁（Monitor）来实现的。但是监视器锁本质又是依赖于底层的操作系统的Mutex Lock来实现的。而操作系统实现线程之间的切换这就需要从用户态转换到核心态，这个成本非常高，状态之间的转换需要相对比较长的时间，这就是为什么Synchronized效率低的原因。因此，这种依赖于操作系统Mutex Lock所实现的锁我们称之为 “重量级锁”。



附一张流程图：

![图片](Synchronized.assets/640.jpg)

### 锁的优劣

各种锁并不是相互代替的，而是在不同场景下的不同选择，绝对不是说重量级锁就是不合适的。每种锁是只能升级，不能降级，即由偏向锁->轻量级锁->重量级锁，而这个过程就是开销逐渐加大的过程。

1. 如果是单线程使用，那偏向锁毫无疑问代价最小，并且它就能解决问题，连CAS都不用做，仅仅在内存中比较下对象头就可以了；
2. 如果出现了其他线程竞争，则偏向锁就会升级为轻量级锁；
3. 如果其他线程通过一定次数的CAS尝试没有成功，则进入重量级锁；



![img](Synchronized.assets/2062729-c9740ab79f4d178c.png)





## Synchronized与内存模型

### 内存可见性

> 内存可见性是指当一个线程修改了某个变量的值，其它线程总是能知道这个变量变化。也就是说，如果线程 A 修改了共享变量 V 的值，那么线程 B 在使用 V 的值时，能立即读到 V 的最新值。

对于Synchronized：

当一个线程进入 Synchronized 代码块后，线程获取到锁，会清空本地内存，然后从主内存中拷贝共享变量的最新值到本地内存作为副本，执行代码，又将修改后的副本值刷新到主内存中，最后线程释放锁。



