[TOC]

# Spring之动态代理


首先，我们知道Spring AOP的底层实现有两种方式：一种是JDK动态代理，另一种是CGLib的方式。
Spring的动态代理策略：
1、如果目标对象实现了接口，默认情况下会采用JDK的动态代理实现AOP
2、如果目标对象实现了接口，可以强制使用CGLIB实现AOP
3、如果目标对象没有实现了接口，必须采用CGLIB库，spring会自动在JDK动态代理和CGLIB之间转换

## 一、原理

Java动态代理是利用反射机制生成一个实现代理接口的匿名类，在调用具体方法前调用InvokeHandler来处理。

而cglib动态代理是利用asm开源包，对代理对象类的class文件加载进来，通过修改其字节码生成子类来处理。

JDK动态代理和CGLIB字节码生成的区别？
1）JDK动态代理只能对实现了接口的类生成代理，而不能针对类（因为生成的代理类默认继承java.lang.reflect.Proxy）

- 实现InvocationHandler
- 使用Proxy.newProxyInstance产生代理对象
- 被代理的对象必须要实现接口

2）CGLIB是针对类实现代理，主要是对指定的类生成一个子类，覆盖其中的方法

- 因为是继承，所以该类或方法最好不要声明成final
- CGLib 必须依赖于CGLib的类库

### 1.JDK动态代理实现

- 被代理的类

```tsx
public interface UserManager {
    public void addUser(String id, String password);
    public void delUser(String id);
}
public class UserManagerImpl implements UserManager {

    public void addUser(String id, String password) {
        System.out.println(".: 调用了UserManagerImpl.addUser()方法！ ");

    }

    public void delUser(String id) {
        System.out.println(".: 调用了UserManagerImpl.delUser()方法！ ");

    }
}
```

- JDKProxy实现

```java
public class JDKProxy implements InvocationHandler {

    private Object targetObject;//需要代理的目标对象

    public Object newProxy(Object targetObject) {//将目标对象传入进行代理
        this.targetObject = targetObject;
        return Proxy.newProxyInstance(targetObject.getClass().getClassLoader(),
                targetObject.getClass().getInterfaces(), this);//返回代理对象
    }

    public Object invoke(Object proxy, Method method, Object[] args)//invoke方法
            throws Throwable {
        checkPopedom();//一般我们进行逻辑处理的函数比如这个地方是模拟检查权限
        Object ret = null;      // 设置方法的返回值
        ret  = method.invoke(targetObject, args);       //调用invoke方法，ret存储该方法的返回值
        return ret;
    }

    private void checkPopedom() {//模拟检查权限的例子
        System.out.println(".:检查权限  checkPopedom()!");
    }
}
```

测试：

```java
JDKProxy jdkPrpxy = new JDKProxy();
UserManager userManagerJDK = (UserManager) jdkPrpxy.newProxy(new UserManagerImpl());
userManagerJDK.addUser("tom", "root");
```

结果：

```css
.:检查权限  checkPopedom()!
.: 调用了UserManagerImpl.addUser()方法！ 
```

### 2.CGLIB实现动态代理

```tsx
public class CGlibProxy implements MethodInterceptor {

    private Object targetObject;// CGLib需要代理的目标对象

    public Object createProxyObject(Object obj) {
        this.targetObject = obj;
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(obj.getClass());
        enhancer.setCallback(this);
        Object proxyObj = enhancer.create();
        return proxyObj;// 返回代理对象
    }

    public Object intercept(Object proxy, Method method, Object[] args,
                            MethodProxy methodProxy) throws Throwable {
        Object obj = null;
        if ("addUser".equals(method.getName())) {// 过滤方法
            checkPopedom();// 检查权限
        }
        obj = method.invoke(targetObject, args);
        return obj;
    }

    private void checkPopedom() {
        System.out.println(".:检查权限  checkPopedom()!");
    }

}
```

测试：

```cpp
UserManager userManager = (UserManager) new CGlibProxy().createProxyObject(new UserManagerImpl());
userManager.addUser("tom", "root");
```

结果：

```css
.:检查权限  checkPopedom()!
.: 调用了UserManagerImpl.addUser()方法！ 
```

## 二、JDK 和 CGLib动态代理性能对比
关于两者之间的性能的话，JDK动态代理所创建的代理对象，在以前的JDK版本中，性能并不是很高，虽然在高版本中JDK动态代理对象的性能得到了很大的提升，但是他也并不是适用于所有的场景。主要体现在如下的两个指标中：

1、CGLib所创建的动态代理对象在实际运行时候的性能要比JDK动态代理高不少，有研究表明，大概要高10倍；

2、但是CGLib在创建对象的时候所花费的时间却比JDK动态代理要多很多，有研究表明，大概有8倍的差距；因此，对于singleton的代理对象或者具有实例池的代理，因为无需频繁的创建代理对象，所以比较适合采用CGLib动态代理，反之，则比较适用JDK动态代理。
