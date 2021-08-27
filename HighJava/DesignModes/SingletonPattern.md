[TOC]



# 单例模式

## 介绍

> 引用百度百科：
>
> 单例模式，属于创建类型的一种常用的[软件设计模式](https://baike.baidu.com/item/软件设计模式/2117635)。通过单例模式的方法创建的类在当前进程中只有一个[实例](https://baike.baidu.com/item/实例/3794138)（根据需要，也有可能一个线程中属于单例，如：仅线程上下文内使用同一个实例）

## 追求的目标

- 线程安全

- 懒加载

- 调用效率高

## 方式

### 饿汉

```java
public class Singleton {

    private static Singleton instance = new Singleton();
 
    private Singleton() {}
 
    public static Singleton getInstance() {
        return instance;
    }
}
```

### 懒汉

```java
public class Singleton {
 
    private static Singleton instance;
 
    private Singleton() {}
 
    public static synchronized Singleton getInstance() {
        if (instance == null) {
            instance = new Singleton();
        }
        return instance;
    }
}
```

### 双重检测

```java
public class Singleton {
 
    private volatile static Singleton singleton;
 
    private Singleton() {}
 
    public static Singleton getSingleton() {
        if (singleton == null) {
            synchronized (Singleton.class) {
                if (singleton == null) {
                    singleton = new Singleton();
                }
            }
        }
        return singleton;
    }
}
```

#### 为什么要加volatile？

因为`singleton = new Singleton();`无法保证原子性，为什么？通常Java文件将使用`javac`命令编译，得到Class，我们在使用`javap -c Singleton.class`，将获得Jvm指令，如下：

```java
Compiled from "Singleton.java"
public class Singleton {
  public static Singleton getSingleton();
    Code:
       0: getstatic     #2                  // Field singleton:LSingleton;
       3: ifnonnull     37
       6: ldc           #3                  // class Singleton
       8: dup
       9: astore_0
      10: monitorenter
      11: getstatic     #2                  // Field singleton:LSingleton;
      14: ifnonnull     27
      17: new           #3                  // class Singleton
      20: dup
      21: invokespecial #4                  // Method "<init>":()V
      24: putstatic     #2                  // Field singleton:LSingleton;
      27: aload_0
      28: monitorexit
      29: goto          37
      32: astore_1
      33: aload_0
      34: monitorexit
      35: aload_1
      36: athrow
      37: getstatic     #2                  // Field singleton:LSingleton;
      40: areturn
    Exception table:
       from    to  target type
          11    29    32   any
          32    35    32   any
}
```

熟知Synchronized原理的，很快便知道以下指令和代码是对应的：

> 不熟悉的也没事，我教你呀，飞机票：[Synchronized](../SourceAnalysis/Jdk/ConcurrentProgramming/Synchronized.md)

```java
      synchronized (Singleton.class) {
        if (singleton == null) {
        	singleton = new Singleton();
        }
      }

  		10: monitorenter
      11: getstatic     #2                  // Field singleton:LSingleton;
      14: ifnonnull     27
      17: new           #3                  // class Singleton             创建一个对象，分配内存
      20: dup																//                             复制栈顶数值并将复制值压入栈顶
      21: invokespecial #4                  // Method "<init>":()V         调用超类构造方法，实例初始化方法，私有方法
      24: putstatic     #2                  // Field singleton:LSingleton; 为指定的类的静态域赋值
      27: aload_0 												  // 														 将第一个引用类型本地变量推送至栈顶
      28: monitorexit

```

由此可见，`singleton = new Singleton();`可以为分为三步：1、创建对象，分配内存；2、实例初始化；3、变量引用实例。

同时，Jvm存在指令重排序

### 静态内部类

```java
public class Singleton {
 
    private static class Holder {
        private static final Singleton INSTANCE = new Singleton();
    }
 
    private Singleton() {}
 
    public static final Singleton getInstance() {
        return Holder.INSTANCE;
    }
}	
```

### 枚举

```java
public enum Singleton {
 
    INSTANCE;
 
}	
```

## 对比

|    方式    |           优点           |   缺点   |
| :--------: | :----------------------: | :------: |
|    饿汉    |     线程安全、效率高     | 非懒加载 |
|    懒汉    |     线程安全、懒加载     |  效率低  |
|  双重检测  | 线程安全、懒加载、效率高 |    无    |
| 静态内部类 | 线程安全、懒加载、效率高 |    无    |
|    枚举    |     线程安全、效率高     | 非懒加载 |

所以推荐双重检测、静态内部类的写法，实际生产中，更推荐静态内部类的写法，因为便于书写逻辑不易犯错。
