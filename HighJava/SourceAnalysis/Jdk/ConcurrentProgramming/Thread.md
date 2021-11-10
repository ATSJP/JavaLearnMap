[TOC]

# Thread

## 线程状态

线程有

> 线程池有5种状态：RUNNING，SHUTDOWN，STOP，TIDYING，TERMINATED。
>
> - RUNNING：线程池处于运行状态，可以接受任务，执行任务，创建线程默认就是这个状态了
> - SHUTDOWN：调用showdown()函数，不会接受新任务，但是会慢慢处理完堆积的任务。
> - STOP：调用showdownnow()函数，不会接受新任务，不处理已有的任务，会中断现有的任务。
> - TIDYING：当线程池状态为showdown或者stop，任务数量为0，就会变为tidying。这个时候会调用钩子函数terminated（）。
> - TERMINATED：terminated()执行完成。
>
> 在线程池中，用了一个原子类来记录线程池的信息，用了int的高3位表示状态，后面的29位表示线程池中线程的个数。
>
> ```java
>     private final AtomicInteger ctl = new AtomicInteger(ctlOf(RUNNING, 0));
> ```

## 终止线程方法

### Stop

- 使用退出标志，说线程正常退出；

### 中断

#### 什么是中断？

Java提供的一种用于停止线程的机制——中断。
- 中断只是一种协作机制，Java没有给中断增加任何语法，中断的过程完全需要程序员自己实现。若要中断一个线程，你需要手动调用该线程的interrupted方法，该方法也仅仅是将线程对象的中断标识设成true；接着你需要自己写代码不断地检测当前线程的标识位；如果为true，表示别的线程要求这条线程中断，此时究竟该做什么需要你自己写代码实现。
- 每个线程对象中都有一个标识，用于表示线程是否被中断；该标识位为true表示中断，为false表示未中断；
- 通过调用线程对象的interrupt方法将该线程的标识位设为true；可以在别的线程中调用，也可以在自己的线程中调用。

#### 中断的相关方法

- public void interrupt() 
  将调用者线程的中断状态设为true。
- public boolean isInterrupted() 
  判断调用者线程的中断状态。
- public static boolean interrupted 
  只能通过Thread.interrupted()调用。 
  它会做两步操作：
  - 返回**当前线程**的中断状态；
  - 将当前线程的中断状态设为false；

#### 如何使用中断？

要使用中断，首先需要在可能会发生中断的线程中不断监听中断状态，一旦发生中断，就执行相应的中断处理代码。 
当需要中断线程时，调用该线程对象的interrupt函数即可。

1.设置中断监听

```java
Thread t1 = new Thread( new Runnable(){
    public void run(){
        // 若未发生中断，就正常执行任务
        while(!Thread.currentThread.isInterrupted()){
            // 正常任务代码……
        }

        // 中断的处理代码……
        doSomething();
    }
} ).start();
```

正常的任务代码被封装在while循环中，每次执行完一遍任务代码就检查一下中断状态；一旦发生中断，则跳过while循环，直接执行后面的中断处理代码。

2.触发中断

```text
t1.interrupt();
```

上述代码执行后会将t1对象的中断状态设为true，此时t1线程的正常任务代码执行完成后，进入下一次while循环前Thread.currentThread.isInterrupted()的结果为true，此时退出循环，执行循环后面的中断处理代码。

#### 如何处理中断？

上文都在介绍如何获取中断状态，那么当我们捕获到中断状态后，究竟如何处理呢？

- Java类库中提供的一些可能会发生阻塞的方法都会抛InterruptedException异常，如：BlockingQueue#put、BlockingQueue#take、Object#wait、Thread#sleep。
- 当你在某一条线程中调用这些方法时，这个方法可能会被阻塞很长时间，你可以在别的线程中调用当前线程对象的interrupt方法触发这些函数抛出InterruptedException异常。
- 当一个函数抛出InterruptedException异常时，表示这个方法阻塞的时间太久了，别人不想等它执行结束了。
- 当你的捕获到一个InterruptedException异常后，亦可以处理它，或者向上抛出。
- 抛出时要注意？？？：当你捕获到InterruptedException异常后，当前线程的中断状态已经被修改为false(表示线程未被中断)；此时你若能够处理中断，则不用理会该值；但如果你继续向上抛InterruptedException异常，你需要再次调用interrupt方法，将当前线程的中断状态设为true。
- **注意**：绝对不能“吞掉中断”！即捕获了InterruptedException而不作任何处理。这样违背了中断机制的规则，别人想让你线程中断，然而你自己不处理，也不将中断请求告诉调用者，调用者一直以为没有中断请求。

## 如何安全地停止线程？

stop函数停止线程过于暴力，它会立即停止线程，不给任何资源释放的余地，下面介绍两种安全停止线程的方法。

1.循环标记变量

自定义一个共享的[boolean类型变量](https://www.zhihu.com/search?q=boolean类型变量&search_source=Entity&hybrid_search_source=Entity&hybrid_search_extra={"sourceType"%3A"answer"%2C"sourceId"%3A252905837})，表示当前线程是否需要中断。

- 中断标识

```java
volatile boolean interrupted = false;
```

- [任务执行函数](https://www.zhihu.com/search?q=任务执行函数&search_source=Entity&hybrid_search_source=Entity&hybrid_search_extra={"sourceType"%3A"answer"%2C"sourceId"%3A252905837})

```java
Thread t1 = new Thread( new Runnable(){
    public void run(){
        while(!interrupted){
            // 正常任务代码……
        }
        // 中断处理代码……
        // 可以在这里进行资源的释放等操作……
    }
} );
```

- 中断函数

```java
Thread t2 = new Thread( new Runnable(){
    public void run(){
        interrupted = true;
    }
} );
```

2.循环中断状态

- 中断标识 
  由线程对象提供，无需自己定义。
- 任务执行函数

```java
Thread t1 = new Thread( new Runnable(){
    public void run(){
        while(!Thread.currentThread.isInterrupted()){
            // 正常任务代码……
        }
        // 中断处理代码……
        // 可以在这里进行资源的释放等操作……
    }
} );
```

- 中断函数

```java
t1.interrupt();
```

上述两种方法本质一样，都是通过循环查看一个[共享标记](https://www.zhihu.com/search?q=共享标记&search_source=Entity&hybrid_search_source=Entity&hybrid_search_extra={"sourceType"%3A"answer"%2C"sourceId"%3A252905837})为来判断线程是否需要中断，他们的区别在于：第一种方法的标识位是我们自己设定的，而第二种方法的标识位是Java提供的。除此之外，他们的实现方法是一样的。

上述两种方法之所以较为安全，是因为一条线程发出终止信号后，接收线程并不会立即停止，而是将本次循环的任务执行完，再跳出[循环停止线程](https://www.zhihu.com/search?q=循环停止线程&search_source=Entity&hybrid_search_source=Entity&hybrid_search_extra={"sourceType"%3A"answer"%2C"sourceId"%3A252905837})。此外，程序员又可以在跳出循环后添加额外的代码进行收尾工作。

## 线程池

### Java中的线程池是如何实现的？

- 线程中线程被抽象为静态内部类Worker，是基于AQS实现的存放在HashSet中；
- 要被执行的线程存放在BlockingQueue中；
- 基本思想就是从workQueue中取出要执行的任务，放在worker中处理；

### 如果线程池中的一个线程运行时出现了异常，会发生什么

如果提交任务的时候使用了submit，则返回的feature里会存有异常信息，但是如果数execute则会打印出异常栈。但是不会给其他线程造成影响。之后线程池会删除该线程，会新增加一个worker。

### 拒绝策略

- AbortPolicy直接抛出异常阻止线程运行；
- CallerRunsPolicy如果被丢弃的线程任务未关闭，则执行该线程；
- DiscardOldestPolicy移除队列最早线程尝试提交当前任务
- DiscardPolicy丢弃当前任务，不做处理

#### newFixedThreadPool （固定数目线程的线程池）

- 阻塞队列为无界队列LinkedBlockingQueue
- 适用于处理CPU密集型的任务，适用执行长期的任务

#### newCachedThreadPool（可缓存线程的线程池）

- 阻塞队列是SynchronousQueue
- 适用于并发执行大量短期的小任务

#### newSingleThreadExecutor（单线程的线程池）

- 阻塞队列是LinkedBlockingQueue
- 适用于串行执行任务的场景，一个任务一个任务地执行

#### newScheduledThreadPool（定时及周期执行的线程池）

- 阻塞队列是DelayedWorkQueue
- 周期性执行任务的场景，需要限制线程数量的场景

### 线程池原理

- 提交一个任务，线程池里存活的核心线程数小于corePoolSize时，线程池会创建一个核心线程去处理提交的任务
- 如果线程池核心线程数已满，即线程数已经等于corePoolSize，一个新提交的任务，会被放进任务队列workQueue排队等待执行。
- 当线程池里面存活的线程数已经等于corePoolSize了，并且任务队列workQueue也满，判断线程数是否达到maximumPoolSize，即最大线程数是否已满，如果没到达，创建非核心线程执行提交的任务。
- 如果当前的线程数达到了maximumPoolSize，还有新的任务过来的话，直接采用拒绝策略处理。

