# Semaphore

## 是什么

Semaphore 通常我们叫它信号量， 可以用来控制同时访问特定资源的线程数量，通过协调各个线程，以保证合理的使用资源。

## 使用场景

通常用于那些资源有明确访问数量限制的场景，常用于限流 。

比如：数据库连接池，同时进行连接的线程有数量限制，连接不能超过一定的数量，当连接达到了限制数量后，后面的线程只能排队等前面的线程释放了数据库连接才能获得数据库连接。

## 使用

```java
  @Test
  public void test() throws InterruptedException {
    int threadNum = 100;
    CountDownLatch countDownLatch = new CountDownLatch(threadNum);
    Semaphore semaphore = new Semaphore(10);
    // 模拟100辆车进入停车场
    for (int i = 0; i < threadNum; i++) {
      Thread thread =
          new Thread(
              () -> {
                try {
                  System.out.println("====" + Thread.currentThread().getName() + "来到停车场");
                  if (semaphore.availablePermits() == 0) {
                    System.out.println("车位不足，请耐心等待");
                  }
                  semaphore.acquire(); // 获取令牌尝试进入停车场
                  System.out.println(Thread.currentThread().getName() + "成功进入停车场");
                  Thread.sleep(new Random().nextInt(100)); // 模拟车辆在停车场停留的时间
                  System.out.println(Thread.currentThread().getName() + "驶出停车场");
                  semaphore.release(); // 释放令牌，腾出停车场车位
                } catch (InterruptedException e) {
                  e.printStackTrace();
                }
                countDownLatch.countDown();
              },
              i + "号车");
      thread.start();
    }
    // wait all thread
    countDownLatch.await();
  }
```

