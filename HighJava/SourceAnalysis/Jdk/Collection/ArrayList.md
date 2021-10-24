[TOC]



## ArrayList

### 关系图

![image-20200804154736870](ArrayList.assets/image-20200804154736870.png)

### 数据结构

数组：Object[] elementData

### 原理

#### 扩容

**知识了解：**

minCapacity：最小扩容量，分析源码不难知道，等于当前数组的size+1。

```java
    public boolean add(E e) {
        ensureCapacityInternal(size + 1);  // Increments modCount!!
        elementData[size++] = e;
        return true;
    }

    private void ensureCapacityInternal(int minCapacity) {
       // ...
    }
```

#### 首次扩容

当我们使用ArrayList()无参构造器初始化对象的时候，默认不扩容，将默认的空数组赋值给数组。后续，在新增数据的时候，触发了扩容机制。

其中，扩容的大小取默认容量、最小扩容量的最大值，之后扩容逻辑同非首次扩容。

```java
	private static final int DEFAULT_CAPACITY = 10
	private static final Object[] DEFAULTCAPACITY_EMPTY_ELEMENTDATA = {};

    public ArrayList() {
        // 无参构造器，默认不扩容，指向默认的空数组
        this.elementData = DEFAULTCAPACITY_EMPTY_ELEMENTDATA;
    }
    
    private void ensureCapacityInternal(int minCapacity) {
        // 首次扩容
        if (elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA) {
            // 取默认容量和最小扩容量的最大值
            minCapacity = Math.max(DEFAULT_CAPACITY, minCapacity);
        }

        ensureExplicitCapacity(minCapacity);
    }
    
    private void ensureExplicitCapacity(int minCapacity) {
        modCount++;

        // 如果最小扩容量大于当前数组长度，则扩容
        if (minCapacity - elementData.length > 0)
            grow(minCapacity);
    }
```

#### 非首次扩容

源码处理流程：

[查看大图](https://mermaid-js.github.io/mermaid-live-editor/#/edit/eyJjb2RlIjoiZ3JhcGggVEJcbiAgICBzdGFydFvlvIDlp4tdIC0tPiBvcGVyYXRpb24xW25ld0NhcGFjaXR5ID0gMS41ICogZWxlbWVudERhdGEubGVuZ3RoXVxuICAgIG9wZXJhdGlvbjEgLS0-IGNvbmRpdGlvbjF7bmV3Q2FwYWNpdHkgPCBtaW5DYXBhY2l0eX1cbiAgICBjb25kaXRpb24xIC0tIFlFUyAtLT4gb3BlcmF0aW9uMltuZXdDYXBhY2l0eSA9IG1pbkNhcGFjaXR5XSBcbiAgICBvcGVyYXRpb24yIC0tPiBjb25kaXRpb24ye25ld0NhcGFjaXR5ID4gTUFYX0FSUkFZX1NJWkV9XG4gICAgY29uZGl0aW9uMSAtLSBOTyAtLT4gY29uZGl0aW9uMlxuICAgIGNvbmRpdGlvbjIgLS0gWUVTIC0tPiBvcGVyYXRpb24zW25ld0NhcGFjaXR5ID0g5pa55rOVaHVnZUNhcGFjaXR5XSBcbiAgICBvcGVyYXRpb24zIC0tPiBvcGVyYXRpb240W-aJqeWuuV0gXG4gICAgY29uZGl0aW9uMiAtLSBOTyAtLT4gb3BlcmF0aW9uNFxuICAgIG9wZXJhdGlvbjQgLS0-IHN0b3Bb57uT5p2fXVxuICAgIHN1YmdyYXBoIOaWueazlWh1Z2VDYXBhY2l0eVxuICAgIG9wZXJhdGlvbjMgLS0-IGNvbmRpdGlvbjN7aW5DYXBhY2l0eSA8IDB9XG4gICAgY29uZGl0aW9uMyAtLSBZRVMgLS0-IG9wZXJhdGlvbjVbdGhyb3cgbmV3IE91dE9mTWVtb3J5RXJyb3JdIFxuICAgIG9wZXJhdGlvbjUgLS0-IHN0b3BcbiAgICBjb25kaXRpb24zIC0tIE5PIC0tPiBjb25kaXRpb240e21pbkNhcGFjaXR5ID4gTUFYX0FSUkFZX1NJWkV9XG4gICAgY29uZGl0aW9uNCAtLSBZRVMgLS0-IG9wZXJhdGlvbjZbbmV3Q2FwYWNpdHkgPSBJbnRlZ2VyLk1BWF9WQUxVRV0gXG4gICAgb3BlcmF0aW9uNiAtLT4gb3BlcmF0aW9uNFxuICAgIGNvbmRpdGlvbjQgLS0gTk8gLS0-IG9wZXJhdGlvbjdbbmV3Q2FwYWNpdHkgPSBNQVhfQVJSQVlfU0laRV0gXG4gICAgb3BlcmF0aW9uNyAtLT4gb3BlcmF0aW9uNFxuICAgIGVuZCIsIm1lcm1haWQiOnsidGhlbWUiOiJkZWZhdWx0In19)

```mermaid
graph TB
    start[开始] --> operation1[newCapacity = 1.5 * elementData.length]
    operation1 --> condition1{newCapacity < minCapacity}
    condition1 -- YES --> operation2[newCapacity = minCapacity] 
    operation2 --> condition2{newCapacity > MAX_ARRAY_SIZE}
    condition1 -- NO --> condition2
    condition2 -- YES --> operation3[newCapacity = 方法hugeCapacity] 
    operation3 --> operation4[扩容] 
    condition2 -- NO --> operation4
    operation4 --> stop[结束]
    subgraph 方法hugeCapacity
    operation3 --> condition3{inCapacity < 0}
    condition3 -- YES --> operation5[throw new OutOfMemoryError] 
    operation5 --> stop
    condition3 -- NO --> condition4{minCapacity > MAX_ARRAY_SIZE}
    condition4 -- YES --> operation6[newCapacity = Integer.MAX_VALUE] 
    operation6 --> operation4
    condition4 -- NO --> operation7[newCapacity = MAX_ARRAY_SIZE] 
    operation7 --> operation4
    end
```

源码分析：

```java
    private void grow(int minCapacity) {
        int oldCapacity = elementData.length;
        // 新的容量 = 老的容量 + 老的容量的一半 = 老容量的1.5倍
        int newCapacity = oldCapacity + (oldCapacity >> 1);
        if (newCapacity - minCapacity < 0)
            // 新的容量 小于 最小扩容量，则把最小扩容量重新赋值新的容量
            newCapacity = minCapacity;
        if (newCapacity - MAX_ARRAY_SIZE > 0)
            // 新的容量 大于 最大扩容量，则进行大容量扩容处理
            newCapacity = hugeCapacity(minCapacity);
        // 开始扩容
        elementData = Arrays.copyOf(elementData, newCapacity);
    }
     
    private static int hugeCapacity(int minCapacity) {
        if (minCapacity < 0) 
            throw new OutOfMemoryError();
        // 如果最小扩容量 大于 最大扩容量，则取Integer.MAX_VALUE，反之取最大扩容量
        return (minCapacity > MAX_ARRAY_SIZE) ?
            Integer.MAX_VALUE :
            MAX_ARRAY_SIZE;
    }
```

### 使用

1.7和1.8最大的区别就是：1.7是饿汉式创建集合容量，1.8是懒汉式创建集合容量

### 优缺点

优点：

1、因为是数组，所以根据脚标取数据很快

缺点：
1、数组的新增、删除操作效率不高，涉及到数组的扩容、复制等操作
2、线程不安全

### 拓展

1、如何证明线程不安全？

```java
  @Test
  public void testArrayListUnSafe() throws InterruptedException, BrokenBarrierException {
    int size = 100;
    List<Integer> list = new ArrayList<>();
    ThreadPoolExecutor threadPoolExecutor =
        new ThreadPoolExecutor(
            size,
            size,
            0L,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(),
            r -> new Thread(r, "test"));
    CyclicBarrier cyclicBarrier =
        new CyclicBarrier(
            size + 1,
            () -> {
              System.out.println("gate open");
            });
    for (int i = 0; i < size; i++) {
      threadPoolExecutor.execute(
          () -> {
            try {
              cyclicBarrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
              e.printStackTrace();
            }
            try {
              for (int j = 0; j < size; j++) {
                list.add(j);
              }
            } catch (Exception e) {
              // 这里可能会有什么异常？
              e.printStackTrace();
            }
            try {
              cyclicBarrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
              e.printStackTrace();
            }
          });
    }
    cyclicBarrier.await();
    cyclicBarrier.await();
    System.out.println(list.size());
  }
```

在不运行之前，你觉得结果是如何？`list.size()`为10000吗。实际运行结果，每次都是不一样的， 且会出现`ArrayIndexOutOfBoundsException`异常，正是因为`ArrayList`没有保证线程安全，在并发访问时存在线程安全问题。

那么为什么会存在线程安全问题呢？这还得归根到源码里：

```java
    public boolean add(E e) {
		ensureCapacityInternal(size + 1);  // Increments modCount!!
        // size++ 线程不安全
		elementData[size++] = e;
		return true;
	}
```

size++是非原子操作，在并发时，会有线程安全。

>  size++可拆解为size=size+1，细分为：第一步，计算size + 1 ，第二步，把size + 1的结果赋值给size，在执行第二步的时候，可能size的值已经发生了变化。

那么怎么简单解决呢？使用synchronized + volatile即可，当然了还有其他的方式，本篇文章不在赘述，在并发编程相关内容，将会详细研讨。




















