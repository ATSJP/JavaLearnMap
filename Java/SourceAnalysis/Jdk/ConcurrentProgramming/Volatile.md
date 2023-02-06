[TOC]



## Volatile

### 前言

先看两个方法，不同的地方在于volatile的修饰：

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
  
  
  
  **补充**：
  
  volatile 只能保证对单次读/写的原子性。例如：i++ 这种操作不能保证原子性，因为i++可拆解为i=i+1，细分为：第一步，计算i + 1 ，第二步，把i+1的结果赋值给i，在执行第二步的时候，可能i的值已经发生了变化。

### 原理

#### 保证变量的内存可见性

##### 为什么

首先，为什么会有内存可见性问题？计算机中CPU和内存是大家所熟知的东西，CPU的速度远大于内存，所以为了提供CPU的利用率，引入了高速缓存。高速缓存的速度通常在CPU和内存之间，弥补着互相的缺点。

如下图，对于每一个CPU，在工作的时候，会将运算时所需要的数据从内存中复制一份到高速缓存中，后续的运算操作仅仅与高速缓存交互，当CPU运算完毕的时候，再将高速缓存的值写回内存中。这样虽然提高了CPU的效率，但是在CPU运算过程中，从内存复制过来的数据可能已经发生了变化，不再是原本复制时候的值，即数据一致性问题，所以我们将此问题归类为内存可见性问题。

![JMM](Volatile.assets/JMM.jpg)

补充：

我们在来看下这个问题：i++ 这种操作不能保证原子性。第一步，CPU将i的值从内存中复制一份到高速缓存中，然后计算i+1并赋值给i，最后将i的值写回内存中。当存在多个CPU进行操作的时候，由于每个CPU都要复制i的值到自己的高速缓存中，这样复制的过来的值，可能在计算的时候，i已经被其他CPU重新写完值，但是CPU并不知道，依然使用自己的高速缓存里的值进行运算，那么最终运行的结果一定是错误的。

##### 如何做

为了分析变量的内存可见性是如何被保证的，接下来通过Java转汇编来查找到Lock指令，并了解其作用。

**JAVA转汇编**

> 飞机票：[Java如何反汇编](../Java2Assembly.md)

示例代码：

```java
public class VolatileDemo {
	private static volatile boolean isVolatileOver = false;

	/**
	 * -Xcomp -XX:+UnlockDiagnosticVMOptions -XX:+PrintAssembly -XX:+LogCompilation -XX:LogFile=jit.log
	 * 
	 * @param args args
	 */
	public static void main(String[] args) {
		for (int i = 0; i < 1_000_000; i++) {
			test();
		}
	}

	public static void test() {
		int i = 1;
		i++;
		System.out.println(i);
		isVolatileOver = true;
		int j = 1;
		j++;
		System.out.println(j);
	}

}
```

Java转汇编：

![JITWatch](Volatile.assets/JITWatch.jpg)

从汇编的结果来看，被Volatile修复的变量在被写操作时，将会多出Lock前缀的指令（即图中 lock addl $0x0,(%rsp)），Lock指令主要时锁住总线，对于Lock指令有以下作用：

1. 将当前处理器缓存行的数据写回系统内存；
2. 这个写回内存的操作会使得其他CPU里缓存了该内存地址的数据无效

> 针对第二点，由于写回内存操作本身并不会导致其他CPU里缓存的数据失效。所以，在多处理器下，为了保证各个处理器的缓存是一致的，就会实现[缓存一致性协议（MESI协议）](./MESI.md)，**每个处理器通过嗅探在总线上传播的数据来检查自己缓存的值是不是过期**了。因此，经过分析我们可以得出如下结论：
>
> 1. Lock前缀的指令会引起处理器缓存写回内存；
> 2. 一个处理器的缓存回写到内存会导致其他处理器的缓存失效；
> 3. 当处理器发现本地缓存失效后，就会从内存中重读该变量数据，即可以获取当前最新值。

#### 禁止指令重排序

##### 什么是重排序

为了提高性能，在遵守 `as-if-serial` 语义（即不管怎么重排序，单线程下程序的执行结果不能被改变。编译器，runtime 和处理器都必须遵守。）的情况下，编译器和处理器常常会对指令做重排序。

一般重排序可以分为如下三种类型：

- 编译器优化重排序。编译器在不改变单线程程序语义的前提下，可以重新安排语句的执行顺序。
- 指令级并行重排序。现代处理器采用了指令级并行技术来将多条指令重叠执行。如果不存在数据依赖性，处理器可以改变语句对应机器指令的执行顺序。
- 内存系统重排序。由于处理器使用缓存和读 / 写缓冲区，这使得加载和存储操作看上去可能是在乱序执行。

> 数据依赖性：如果两个操作访问同一个变量，且这两个操作中有一个为写操作，此时这两个操作之间就存在数据依赖性。这里所说的数据依赖性仅针对单个处理器中执行的指令序列和单个线程中执行的操作，不同处理器之间和不同线程之间的数据依赖性不被编译器和处理器考虑。

![重排序顺序](Volatile.assets/重排序顺序.png)

##### 如何禁止

**内存屏障指令**：为了实现 volatile 内存语义（即内存可见性），JMM 会限制特定类型的编译器和处理器重排序。为此，JMM 针对编译器制定了 volatile 重排序规则表，如下所示：

| 是否重排序 | 第二次操作 | 第二次操作 | 第二次操作 |
| :--------: | :--------: | :--------: | :--------: |
| 第一次操作 | 普通读/写  | Volatile读 | Volatile写 |
| 普通读/写  |    YES     |    YES     |     NO     |
| Volatile读 |     NO     |     NO     |     NO     |
| Volatile写 |    YES     |     NO     |     NO     |

使用 volatile 修饰变量时，根据 volatile 重排序规则表，Java 编译器在生成字节码时，会在指令序列中插入内存屏障指令来禁止特定类型的处理器重排序。

`内存屏障`是一组处理器指令，它的作用是禁止指令重排序和解决内存可见性的问题。

JMM 把内存屏障指令分为下列四类：

![img](Volatile.assets/v2-a992d2e50fc353edb9027683bf62ee2b_720w.jpg)

> StoreLoad 屏障是一个全能型的屏障，它同时具有其他三个屏障的效果。所以执行该屏障开销会很大，因为它使处理器要把缓存中的数据全部刷新到内存中。

下面我们来看看 volatile 读 / 写时是如何插入内存屏障的，见下图：

![img](Volatile.assets/v2-fdead3feef1123c1c225826559def686_720w.jpg)

从上图，我们可以知道 volatile 读 / 写插入内存屏障规则：

- 在每个 volatile 读操作的后面插入 LoadLoad 屏障和 LoadStore 屏障。
- 在每个 volatile 写操作的前后分别插入一个 StoreStore 屏障和一个 StoreLoad 屏障。

也就是说，编译器不会对 volatile 读与 volatile 读后面的任意内存操作重排序；编译器不会对 volatile 写与 volatile 写前面的任意内存操作重排序。

##### Volatile与happens-before

##### Volatile内存语义

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

