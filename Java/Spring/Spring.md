[TOC]

# Spring

## 一、事务

### 1、proxy-target-class

```xml
<tx:annotation-driven transaction-manager="transactionManager" 
                                       proxy-target-class="true"/>
```

**注意：**proxy-target-class属性值决定是基于接口的还是基于类的代理被创建。如果proxy-target-class 属性值被设置为true，那么基于类的代理将起作用（这时需要cglib库）。如果proxy-target-class属值被设置为false或者这个属性被省略，那么标准的JDK 基于接口的代理将起作用