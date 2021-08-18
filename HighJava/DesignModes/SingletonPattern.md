# 单例模式

## 介绍

> 引用百度百科：
>
> 单例模式，属于创建类型的一种常用的[软件设计模式](https://baike.baidu.com/item/软件设计模式/2117635)。通过单例模式的方法创建的类在当前进程中只有一个[实例](https://baike.baidu.com/item/实例/3794138)（根据需要，也有可能一个线程中属于单例，如：仅线程上下文内使用同一个实例）

## 追求的目标

- 线程安全

- 懒加载

- 调用效率高

## 方法

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
