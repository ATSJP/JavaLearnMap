# Semaphore

> 参考https://zhuanlan.zhihu.com/p/98593407

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

