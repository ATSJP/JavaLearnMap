## Volatile

### 前言

>  **补充概念：Java 内存模型中的可见性、原子性和有序性。**
>
> **可见性：**
>
> 　　可见性是一种复杂的属性，因为可见性中的错误总是会违背我们的直觉。通常，我们无法确保执行读操作的线程能适时地看到其他线程写入的值，有时甚至是根本不可能的事情。为了确保多个线程之间对内存写入操作的可见性，必须使用同步机制。
>
> 　　**可见性，是指线程之间的可见性，一个线程修改的状态对另一个线程是可见的。**也就是一个线程修改的结果。另一个线程马上就能看到。比如：用volatile修饰的变量，就会具有可见性。volatile修饰的变量不允许线程内部缓存和重排序，即直接修改内存。所以对其他线程是可见的。但是这里需要注意一个问题，volatile只能让被他修饰内容具有可见性，但不能保证它具有原子性。比如 volatile int a = 0；之后有一个操作 a++；这个变量a具有可见性，但是a++ 依然是一个非原子操作，也就是这个操作同样存在线程安全问题。
>
> 　　在 Java 中 volatile、synchronized 和 final 实现可见性。
>
> **原子性：**
>
> 　　**原子是世界上的最小单位，具有不可分割性。**比如 a=0；（a非long和double类型） 这个操作是不可分割的，那么我们说这个操作时原子操作。再比如：a++； 这个操作实际是a = a + 1；是可分割的，所以他不是一个原子操作。非原子操作都会存在线程安全问题，需要我们使用同步技术（sychronized）来让它变成一个原子操作。一个操作是原子操作，那么我们称它具有原子性。java的concurrent包下提供了一些原子类，我们可以通过阅读API来了解这些原子类的用法。比如：AtomicInteger、AtomicLong、AtomicReference等。
>
> 　　在 Java 中 synchronized 和在 lock、unlock 中操作保证原子性。
>
> **有序性：**
>
> 　　Java 语言提供了 volatile 和 synchronized 两个关键字来保证线程之间操作的有序性，volatile 是因为其本身包含“禁止指令重排序”的语义，synchronized 是由“一个变量在同一个时刻只允许一条线程对其进行 lock 操作”这条规则获得的，此规则决定了持有同一个对象锁的两个同步块只能串行执行。

先看两个方法， 不同的地方在于volatile的修饰：

```java
  private static boolean isSimpleOver = false;
  private static volatile boolean isVolatileOver = false;

  public static void simpleDemo() throws InterruptedException {
    new Thread(() -> {
      while (!isSimpleOver) {
      }
    }).start();
    TimeUnit.SECONDS.sleep(2);
    isSimpleOver = true;
    System.out.println("isSimpleOver:" + isSimpleOver);
  }

  public static void volatileDemo() throws InterruptedException {
    new Thread(() -> {
      while (!isVolatileOver) {
      }
    }).start();
    TimeUnit.SECONDS.sleep(2);
    isVolatileOver = true;
    System.out.println("isVolatileOver:" + isVolatileOver);
  }
```

 对于simpleDemo方法，判断变量isSimpleOver无volatile修饰，2秒后打印以下语句，主线程无法停止：

```text
isSimpleOver:true
```

对于volatileDemo方法，判断变量isVolatileOver有volatile修饰，2秒后打印以下语句，主线程停止：

```text
isVolatileOver:true
```

### 介绍

　Java语言提供了一种稍弱的同步机制，即volatile变量，用来确保将变量的更新操作通知到其他线程。当把变量声明为volatile类型后，编译器与运行时都会注意到这个变量是共享的，因此不会将该变量上的操作与其他内存操作一起重排序。volatile变量不会被缓存在寄存器或者对其他处理器不可见的地方，因此在读取volatile类型的变量时总会返回最新写入的值。

### 原理



### 拓展

#### happens-before原则

**Java内存模型具备一些先天的“有序性”**，即不需要通过任何手段就能够得到保证的有序性，这个通常也称为 **happens-before 原则**。如果两个操作的执行次序无法从happens-before原则推导出来，那么它们就不能保证它们的有序性，虚拟机可以随意地对它们进行重排序。

- **程序次序规则**：一个线程内，按照代码顺序，书写在前面的操作先行发生于书写在后面的操作
- **锁定规则**：一个unLock操作先行发生于后面对同一个锁额lock操作
- **volatile变量规则**：对一个变量的写操作先行发生于后面对这个变量的读操作
- **传递规则**：如果操作A先行发生于操作B，而操作B又先行发生于操作C，则可以得出操作A先行发生于操作C
- 线程启动规则：Thread对象的start()方法先行发生于此线程的每个一个动作
- 线程中断规则：对线程interrupt()方法的调用先行发生于被中断线程的代码检测到中断事件的发生
- 线程终结规则：线程中所有的操作都先行发生于线程的终止检测，我们可以通过Thread.join()方法结束、Thread.isAlive()的返回值手段检测到线程已经终止执行
- 对象终结规则：一个对象的初始化完成先行发生于他的finalize()方法的开始

