# String系列

> 很多时候，我们都会认为我们十分的了解String，久而久之，在使用过程中，就会踩一些坑。

## 从String的+实现原理引发的一系列问题

先来看一道题，你认为结果如何？

```java
        String a = "helloWord1";

        final String b = "helloWord";
        String F = "helloWord";

        String c = b + 1;
        String e = F + 1;
        System.out.println((a == c));
        System.out.println((a == e));
         // 获取内存地址
		// System.out.println(System.identityHashCode(a));
		// System.out.println(System.identityHashCode(c));
		// System.out.println(System.identityHashCode(e));
```

我先不说结果，对的人自然对，不对的人自然不对，不过不要紧，你花一点儿时间听我唠叨完。

习惯了使用`String str = str1 + str2`，却从未想过以前经常准备过的面试题：`String`类型是不可变。对呀，明明不可变，那这里为啥看起来好像可以变呢，是不是我们在进行一次`str = str1`，就该可以改变了呢，答案当然是否定的。

这里讲下这个`+`的原理：实际上底层（编译器做的事）是使用`StringBuilder`（`JDK1.5`之前是`StringBuffer`）的`append`来完成字符串的拼接，对于每一个 `str1`，则调用其`String.valueOf(str1)`获取其值，最后的执行效果如：

```java
String str = (new StringBuilder(String.valueOf(str1))).append(String.valueOf(str2)).toString()
```

看到这里是不是对`String`类型是不可变的加深了理解了呢，因为每次都是`new StringBuilder()`，所以这里看似可变，其实每次都是`new`出的新对象，底层实际上是更改了栈内变量名指向的地址，使之指向堆中新的对象。(当然，String为什么不可变，原理不仅仅于此，源码更能解释String如何实现了不可变，感兴趣的，继续往下翻）

明白了`+`号实现原理，是不是可以举一反三，请看以下代码：

```java
String str = null;
str += "a";
System.out.println(str);
```

你认为输出是什么呢？

结果是：`nulla`

为什么呢，根据上述原理分析，在`StringBuilder`进行`append`之前，会对对象进行`String.valueof()`，那么我们来翻下源码：

```java
    public static String valueOf(Object obj) {
        return (obj == null) ? "null" : obj.toString();
    }
```

这下应该明白了，所以平时在开发中针对字符串的处理，一定要三思而后行。

## String是不可变的

### 什么是不可变对象

如果一个对象它被创建后，状态不能改变，则这个对象被认为是不可变的。

### 为什么不可变

`String`的底层实际上一个char数组，相信好奇心重的盆友都翻开过源码，那么我把他扒出来的目的只有一个，大家请看char数组是不是多了个修饰：final。

```java
    /** The value is used for character storage. */
    private final char value[];

    /** Cache the hash code for the string */
    private int hash; // Default to 0
```

> 被final修饰的变量在其初始化完成后，不可变。
>
> 补充一句，这里的不可变是指引用变量所引用的地址不可变， 即一直引用同一个对象，但这个对象完全可以发生改变 。 例如某个指向数组的final引用，它必须从此至终指向初始化时指向的数组，但是这个数组的内容完全可以改变。 

既然存在了final，那么value引用的地址，肯定无法变了。再看看内容，貌似没有任何一个方法，允许我们直接修改value的内容。到此已经可以证明：String不可变。

但是，String真的不可变吗？

不死心呀，程序员可是能上天入地的，这点小事我就要从了吗？客官别着急，还真有法子打破String不可变的真理——反射，这个强大的功能，让我们可以强行修改value的值，上例子：

```java
        String str = "Hello World";
        System.out.println("修改前的str:" + str);
        System.out.println("修改前的str的内存地址" + System.identityHashCode(str));
        // 获取String类中的value字段
        Field valueField = String.class.getDeclaredField("value");
        // 改变value属性的访问权限
        valueField.setAccessible(true);
        // 获取str对象上value属性的值
        char[] value = (char[]) valueField.get(str);
        // 改变value所引用的数组中的字符
        value[3] = '?';
        System.out.println("修改后的str:" + str);
        System.out.println("修改前的str的内存地址" + System.identityHashCode(str)); 
```

执行结果：

```java
修改前的str:Hello World
修改前的str的内存地址1784662007
修改后的str:Hel?o World
修改前的str的内存地址1784662007
```

可以看到了，内存地址未变，但是内容变掉了。

总结：String类型是不可变的，但是在我们使用了反射之后，往往是可以打破这些原则。



