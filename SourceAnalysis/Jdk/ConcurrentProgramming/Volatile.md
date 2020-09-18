## Volatile

### 前言

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

那这是为什么呢？带着这个疑问，继续往下看。

### 介绍

　Java语言提供了一种稍弱的同步机制，即volatile变量，用来确保将变量的更新操作通知到其他线程。当把变量声明为volatile类型后，编译器与运行时都会注意到这个变量是共享的，因此不会将该变量上的操作与其他内存操作一起重排序。volatile变量不会被缓存在寄存器或者对其他处理器不可见的地方，因此在读取volatile类型的变量时总会返回最新写入的值。

简单来说：线程在试图读取一个volatile变量时，会从主内存区中读取最新的值。

#### 具体作用

- 保证变量的内存可见性

  内存可见性是指当一个线程修改了某个变量的值，新值对于其他线程来说是可以立即得知的。

- 禁止指令重排序

  为了提高性能，在遵守一定的规则下（即不管怎么重排序，单线程下程序的执行结果不能被改变。编译器，Runtime 和处理器都必须遵守）的情况下，编译器和处理器常常会对指令做重排序。



### 原理





使用 volatile 修饰共享变量后，每个线程要操作变量时会从主内存中将变量拷贝到本地内存作为副本，当线程操作变量副本并写回主内存后，会通过 **CPU 总线嗅探机制**告知其他线程该变量副本已经失效，需要重新从主内存中读取。

volatile 保证了不同线程对共享变量操作的可见性，也就是说一个线程修改了 volatile 修饰的变量，当修改后的变量写回主内存时，其他线程能立即看到最新值。







































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

