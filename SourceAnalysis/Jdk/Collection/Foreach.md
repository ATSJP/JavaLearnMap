# Foreach

> 面试中，初入行业的人往往最怕问到基础的原理问题，为什么呢？因为初入行业的人，对新技术、框架等比较好奇，研究的内容多而广，却迟迟停留于表面。如果你是这类人，那么咱么一起来研究熟悉而又陌生的内容，如果不是，那么请多多指教。

# 介绍

## 起源

最早Foreach出现于Jdk1.5中，较为舒适的写法，引来了大量注目者，直至如今，已经广泛的出现代码中。此优秀的书写规则一直被沿用下去，在Jdk1.8中，出现了Lamda表达式、Stream流等，皆是延用此写法。

## 优点

1. 书写简单，看起来清爽

## 缺点

1. 遍历过程中无法操作元素，比如赋值、删除操作（此删除操作有彩蛋，具体请看[JavaList遍历之删除](https://github.com/atsjp/note)）。
2. 同时只能遍历一个Collection或数组，不能同时遍历多余一个Collection或数组。
3. 只能正向遍历，不能反向遍历
4. 只能Jdk1.5及之后的版本可以支持（相信这个对大伙，都是不存在的问题，是谁还用老版本呢）

# 原理

说的人多了，那便是真理？网上搜一搜Foreach原理，大致都这么说：Foreach最终会变为迭代器来完成遍历的。

对此，半信半疑，是个程序员，有点自信的，一定要问，为啥，啥时候把Foreach变为迭代器遍历了，在编译前底层代码处理，还是编译后直接改写为迭代器遍历？

好，我们不妨先提出了三个问题：

1. List实现了迭代器，如果证明？
2. 何时把Foreach执行，改成了和迭代器遍历的一个效果？
3. 数组也可以使用Foreach遍历，但是好像没有实现迭代器呀？

## List与迭代器

博主不是一个大神，但是绝对是一个好奇的人，不管是为了面试，还是为了在小伙伴面前吹一波，博主和大家一样，翻了常用的集合源码，为了验证迭代器的实现也看到了以下的实现图：

![List](Foreach.assets/List.png)

这里很明显的一点就是AbstractList实现了Iterable接口，这个Iterable大家也不会陌生，肯定和Iterator有关，不妨扒出来看看，下面这张图可以看出，其中方法iterator()直接返回了一个迭代器。这个方法也是我们使用迭代器遍历List，经常要用到的方法。

 ![image-20191210203244936](Foreach.assets/image-20191210203244936.png)

再看看我们我们平时使用迭代器遍历的写法，原来如此，但是各位要注意的是：每个集合的

```java
        Iterator iterator = getList().iterator();
        while(iterator.hasNext()) {
            System.out.println(iterator.next());
        }
```

iterator()方法的实现并非都进行了重写，以Jdk1.8为例，ArrayList重写了iterator()方法，实现了自己的Inner Class：Itr(Itr实现了Iterator接口)，而LinkedList并没有重写iterator()方法，而是其父类AbstractSequentialList负责进行了重写。

到此，我们已经验证了List中的确实现了迭代器相关的内容。

## Foreach如何变成了迭代器遍历

在讲着个问题之前，博主先说下，还在翻Jdk源码、各种在源码打断点的童鞋，停下来，咱么要么百度下，要么看下Jdk官方文档，如果都不行，那你就听我唠唠嗑。为什么这么说呢，压根在Jdk源码中，就找不到Foreach被改写或者被执行成迭代器的代码。

那么，答案是什么呢？不卖关子了，讲重点：

**Jdk的编译器在编译的时候，会将Foreach语句改写为迭代器遍历的写法，这个改动从Jdk1.5开始。**

What？编译器搞得鬼？我有点不信，就说谁动了我代码。不信的话，你听我讲一个验证的方案。咱们把含有Foreach的代码编译一下，在把字节码反编译一下（能看得懂字节码的同学请忽略反编译过程），看看最终反编译获得的代码是不是不一样了，原本的Foreach被改写成了迭代器遍历。

反编译工具：IDEA就行，通过直接打开Target下面的.class文件，即可得到反编译之后的代码。代码如下：

```java
    @Test
    public void testForeachSource() {
        List<Integer> list = Arrays.asList(1, 2, 3);
        for (Integer i : list) {
            System.out.println(i);
        }
    }
```

反编译后：

```java
    @Test
    public void testForeachSource() {
        List<Integer> list = Arrays.asList(1, 2, 3);
        Iterator var2 = list.iterator();

        while(var2.hasNext()) {
            Integer i = (Integer)var2.next();
            System.out.println(i);
        }
    }
```

看到了吧，被编译器搞了一波吧，好叻，收拾收拾，各位准备找同事、朋友吹牛去了。

## 数组为何可以用Foreach

估摸着大家看了上面的分析，都能猜测下面该怎么验证数组的实现了。

测试代码如下：

````java
    @Test
    public void testArrayForeachSource() {
        Integer[] array = new Integer[3];
        array[0] = 0;
        array[1] = 1;
        array[2] = 2;
        for (Integer i : array) {
            System.out.println(i);
        }
    }
````

反编译：

```java
    @Test
    public void testArrayForeachSource() {
        Integer[] array = new Integer[]{0, 1, 2};
        Integer[] var2 = array;
        int var3 = array.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            Integer i = var2[var4];
            System.out.println(i);
        }
    }
```

看了反编译后面的代码，无需多说，编译器在对数组的Foreach处理时，会改写成基本的for()循环来处理。

# 举一反三

看一道题：

```java
	@Test
	public void testForeach() {
		for (Integer i : getList()) {
			System.out.println(i);
		}
		System.out.println("&&&&&&&");
		for (Integer i : getArrays()) {
			System.out.println(i);
		}
		System.out.println("&&&&&&&");
		for (int i = 0; i < getArrays().length; i++) {
			System.out.println(i);
		}
	}

	private static List<Integer> getList() {
		System.out.println("------------");
		return Arrays.asList(1, 2, 3);
	}

	private static Integer[] getArrays() {
		System.out.println("------------");
		return (Integer[]) Arrays.asList(1, 2, 3).toArray();
	}
```

你们认为，执行结果事什么呢？猜错了没事，在看下下面的反编译代码，你就会明白为什么了。

以下为执行结果：

```java
------------
1
2
3
&&&&&&&
------------
1
2
3
&&&&&&&
------------
0
------------
1
------------
2
------------
```

反编译：

```java
    @Test
    public void testForeach() {
        Iterator var1 = getList().iterator();

        while(var1.hasNext()) {
            Integer i = (Integer)var1.next();
            System.out.println(i);
        }

        System.out.println("&&&&&&&");
        Integer[] var5 = getArrays();
        int var7 = var5.length;

        for(int var3 = 0; var3 < var7; ++var3) {
            Integer i = var5[var3];
            System.out.println(i);
        }

        System.out.println("&&&&&&&");

        for(int i = 0; i < getArrays().length; ++i) {
            System.out.println(i);
        }

    }

    private static List<Integer> getList() {
        System.out.println("------------");
        return Arrays.asList(1, 2, 3);
    }

    private static Integer[] getArrays() {
        System.out.println("------------");
        return (Integer[])((Integer[])Arrays.asList(1, 2, 3).toArray());
    }
```



















