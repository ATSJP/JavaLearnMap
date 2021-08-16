# 单例模式

## 介绍





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

