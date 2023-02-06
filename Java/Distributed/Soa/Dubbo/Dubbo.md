[TOC]



## Dubbbo

### 泛化调用

官方:[飞机票](http://dubbo.apache.org/zh-cn/docs/user/demos/generic-reference.html)

直连：

```java
         ApplicationConfig application = new ApplicationConfig("test");

		ReferenceConfig<GenericService> reference = new ReferenceConfig<>();
		reference.setApplication(application);
         // 直连IP+端口
	    reference.setUrl("dubbo://127.0.0.1:14200/cn.xx.DemoProvider");
		reference.setInterface("cn.xx.DemoProvider");
		// 是否支持泛化调用
		reference.setGeneric(true);
		GenericService genericService = reference.get();
		// 调用泛化接口
		// 不带参
		Object result = genericService.$invoke("test", null, null);
         // 带参
         // Object result = genericService.$invoke("test", new String[] { "int" }, new Integer[] { 1 });
		// 返回接口输出
		System.out.println(result);
```

注册中心：


```java
	    ApplicationConfig application = new ApplicationConfig("test");
		RegistryConfig registry = new RegistryConfig();
		registry.setAddress("redis2://192.168.56.89:6379");
		application.setRegistry(registry);

		ReferenceConfig<GenericService> reference = new ReferenceConfig<>();
		reference.setApplication(application);
		reference.setInterface("cn.xx.DemoProvider");
		// 是否支持泛化调用
		reference.setGeneric(true);
		GenericService genericService = reference.get();
		// 调用泛化接口
		// 不带参
		Object result = genericService.$invoke("test", null, null);
         // 带参
         // Object result = genericService.$invoke("test", new String[] { "int" }, new Integer[] { 1 });
		// 返回接口输出
		System.out.println(result);
```

