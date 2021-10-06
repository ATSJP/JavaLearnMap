# Thread

## 线程状态

线程池有5种状态：running，showdown，stop，Tidying，TERMINATED。

- running：线程池处于运行状态，可以接受任务，执行任务，创建线程默认就是这个状态了
- showdown：调用showdown（）函数，不会接受新任务，但是会慢慢处理完堆积的任务。
- stop：调用showdownnow（）函数，不会接受新任务，不处理已有的任务，会中断现有的任务。
- Tidying：当线程池状态为showdown或者stop，任务数量为0，就会变为tidying。这个时候会调用钩子函数terminated（）。
- TERMINATED：terminated（）执行完成。

在线程池中，用了一个原子类来记录线程池的信息，用了int的高3位表示状态，后面的29位表示线程池中线程的个数。

## 终止线程方法

### Stop

- 使用退出标志，说线程正常退出；

### 中断

- 通过判断this.interrupted（） throw new InterruptedException（）来停止 使用String常量池作为锁对象会导致两个线程持有相同的锁，另一个线程不执行，改用其他如new Object（）

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


