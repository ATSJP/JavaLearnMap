## Synchronized 关键字

> Synchronized关键字在需要原子性、可见性和有序性这三种特性的时候都可以作为其中一种解决方案。

### 使用

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

#### 特点

1. 可重入性
2. 当代码段执行结束或出现异常后会自动释放对监视器的锁定
3. 是非公平锁，在等待获取锁的过程中不可被中断
4. Synchronized的内存语义
5. 互斥性，被Synchronized修饰的方法同时只能由一个线程执行

##### 证明

1、如何证明可重入性

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

2、是非公平锁，在等待获取锁的过程中不可被中断

其实对于synchronized来说，如果一个线程在等待锁，那么结果只有两种，要么它获得这把锁继续执行，要么它就保存等待，**即使调用中断线程的方法，也不会生效。**

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

#### 原理

Synchronized底层实际上是通过Mark Word实现的。

