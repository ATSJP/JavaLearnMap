### 终止线程方法

- 使用退出标志，说线程正常退出；
- 通过判断this.interrupted（） throw new InterruptedException（）来停止 使用String常量池作为锁对象会导致两个线程持有相同的锁，另一个线程不执行，改用其他如new Object（）

