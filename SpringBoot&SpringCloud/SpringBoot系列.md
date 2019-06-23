# SpringBoot系列

官方文档：https://docs.spring.io/spring-boot/docs/2.0.8.RELEASE/reference/htmlsingle/

## SpringBoot开启事务

### 一、介绍

### 二、配置

**以SpringDataJpa为例** ：

#### 1、配置jpa生成数据库平台

（注意：mysql的InnoDB支持事务，MyISAM不支持事务）

```yaml
    jpa:
        generateDdl: false
        properties:
            hibernate:
                show_sql: true
                # 选配，自行在mysql中设置库表引擎为InnoDB，可不需要配置
                database-platform: org.hibernate.dialect.MySQL5InnoDBDialect
```

#### 2、类方法配置注解

（注意：SpringBoot2.0后，动态代理默认使用Cglib代理，基于类方法的代理，而Jdk代理基于接口，这里先基于类方法代理，下面在具体说明）

```java
@Transactional(rollbackOn = Exception.class)
public LoginInfoEntity register(String loginName, String password, String userName) {
 		...
         // 手动抛异常
         int i = 1 / 0；
		return loginInfoEntity;
}
```

3、写测试类

```java
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * UserServiceTest
 *
 * @author sjp
 * @date 2019/5/4
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTest {

	@Resource
	private UserService userService;

	@Test
	public void register() {
		userService.register("test9", "test9", "test9");
	}
}
```

注意：别忘记方法类，手动抛异常，int i = 1 / 0；

**Tip1**：关于代理的proxy-target-class参数设置？

**解决**：proxy-target-class属性值决定是基于接口的还是基于类的代理被创建。如果proxy-target-class 属性值被设置为true，那么基于类的代理将起作用（这时需要cglib库）。如果proxy-target-class属值被设置为false或者这个属性被省略，那么标准的JDK 基于接口的代理将起作用

```yaml
spring:
    aop:
   	   # SpringBoot2.0后默认是true
        proxy-target-class: true
```

**Tip2**：@EnableTransactionManagement 咋回事，要不要在启动类设置？

**解决**：SpringBoot2.0后不需要，默认是加载事务管理器的



**博客**：<https://www.cnblogs.com/alimayun/p/10296340.html>

<https://blog.csdn.net/equaker/article/details/81534922>



### 三、踩坑

## SpringBoot引入AOP

### 一、介绍

### 二、配置

博客：https://blog.csdn.net/huang_550/article/details/53672321

<https://www.cnblogs.com/onlymate/p/9630788.html>

#### 1、配置

### 三、踩坑

## SpringBoot上传文件

### 一、介绍

### 二、整合

```java
 @PutMapping("/upload")
 public SearchResponse upload(MultipartFile[] files) {
    SearchResponse response = new SearchResponse();
    return response;
 }
```

### 三、采坑

#### 1、上传文件受大小限制

```log
Spring Boot:The field file exceeds its maximum permitted size of 1048576 bytes.
```

解决：

Spring Boot1.4版本后配置更改为:

```yml
spring.http.multipart.maxFileSize = 10Mb  
spring.http.multipart.maxRequestSize=100Mb  
```

Spring Boot2.0之后的版本配置修改为:

```yml
spring.servlet.multipart.max-file-size = 10MB  
spring.servlet.multipart.max-request-size=100MB
```

#### 2、复杂类型无法映射到对象中去

**Controller:**

```java
 @PutMapping("/upload")
    public SearchResponse upload(SearchRequest request, MultipartFile[] files) {
        // TODO 为何映射不到复杂类型里 待解决
        SearchResponse response = new SearchResponse();
        request.setFiles(files);
        searchService.upload(request, response);
        return response;
    }
```

**entity**

```java
public class SearchRequest extends BaseRequest {
    @NotBlank
    private String key;

    private MultipartFile[] files;

    public MultipartFile[] getFiles() {
        return files;
    }

    public void setFiles(MultipartFile[] files) {
        this.files = files;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
```

(待解决)

## SpringBoot整合shiro

### 一、介绍

### 二、整合

#### 1、导入jar

```pom
<shiro.version>1.3.2</shiro.version>
```

```pom
<!-- shiro -->
<dependency>
    <groupId>org.apache.shiro</groupId>
    <artifactId>shiro-core</artifactId>
    <version>${shiro.version}</version>
</dependency>
<dependency>
    <groupId>org.apache.shiro</groupId>
    <artifactId>shiro-spring</artifactId>
    <version>${shiro.version}</version>
</dependency>
```

#### 2、配置类

```java
import com.lemon.relam.LoginRelam;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.filter.DelegatingFilterProxy;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author shijianpeng
 */
@Configuration
public class ShiroConfig {

    /**
     * 使用FilterRegistrationBean管理DelegatingFilterProxy的生命周期，代替spring项目中shiro在web.xml的配置
     *
     * @return FilterRegistrationBean<DelegatingFilterProxy>
     */
	@Bean
	public FilterRegistrationBean<DelegatingFilterProxy> delegatingFilterProxy() {
		FilterRegistrationBean<DelegatingFilterProxy> filterRegistrationBean = new FilterRegistrationBean<DelegatingFilterProxy>();
		DelegatingFilterProxy proxy = new DelegatingFilterProxy();
		filterRegistrationBean.setFilter(proxy);
		// 保留Filter原有的init，destroy方法的调用
		proxy.setTargetFilterLifecycle(true);
		proxy.setTargetBeanName("shiroFilter");
		return filterRegistrationBean;
	}

	/**
	 * 配置shiro filter
	 *
	 * @return ShiroFilterFactoryBean
	 */
	@Bean("shiroFilter")
	public ShiroFilterFactoryBean shirFilter() {
		ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
		shiroFilterFactoryBean.setSecurityManager(securityManager());
		shiroFilterFactoryBean.setLoginUrl("/u/index");
		shiroFilterFactoryBean.setSuccessUrl("/u/main");
		shiroFilterFactoryBean.setUnauthorizedUrl("/403");
		Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
		filterChainDefinitionMap.put("/u/video/get", "anon");
		filterChainDefinitionMap.put("/static/**", "anon");
		filterChainDefinitionMap.put("/u/user/login", "anon");
		filterChainDefinitionMap.put("/u/user/logout", "logout");

		filterChainDefinitionMap.put("/**", "authc");
		shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
		return shiroFilterFactoryBean;
	}

	/**
	 * 配置securityManager
	 *
	 * @return SecurityManager
	 */
	@Bean
	public SecurityManager securityManager() {
		DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
		securityManager.setRealm(loginRealm());
		return securityManager;
	}

	/**
	 * 配置shiroRelam并指定凭证匹配器
	 *
	 * @return LoginRelam
	 */
	@Bean
	public LoginRelam loginRealm() {
		LoginRelam loginRelam = new LoginRelam();
		loginRelam.setCredentialsMatcher(hashedCredentialsMatcher());
		return loginRelam;
	}

	/**
	 * 凭证匹配器
	 * 
	 * @return HashedCredentialsMatcher
	 */
	@Bean
	public HashedCredentialsMatcher hashedCredentialsMatcher() {
		HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
		hashedCredentialsMatcher.setHashAlgorithmName("md5");
		hashedCredentialsMatcher.setHashIterations(5);
		return hashedCredentialsMatcher;
	}

	/// @Bean
	// public ShiroDialect shiroDialect() {
	// return new ShiroDialect();
	// }

	/**
	 * 配置 LifecycleBeanPostProcessor. 可以自定的来调用配置在 Spring IOC 容器中 shiro bean 的生命周期方法.
	 *
	 * @return LifecycleBeanPostProcessor
	 */
	@Bean
	public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
		return new LifecycleBeanPostProcessor();
	}

	/**
	 * 启用 IOC 容器中使用 shiro 的注解. 但必须在配置了 LifecycleBeanPostProcessor 之后才可以使用
	 *
	 * @return DefaultAdvisorAutoProxyCreator
	 */
	@Bean
	@DependsOn("lifecycleBeanPostProcessor")
	public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
		DefaultAdvisorAutoProxyCreator proxyCreator = new DefaultAdvisorAutoProxyCreator();
		/// proxyCreator.setProxyTargetClass(true);
		return proxyCreator;
	}

	/**
	 * 开|启shiro aop注解支持. 使用代理方式;所以需要开启代码支持;
	 * 
	 * @return AuthorizationAttributeSourceAdvisor
	 */
	@Bean
	public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor() {
		AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
		advisor.setSecurityManager(securityManager());
		return advisor;
	}

	// @Bean
	// public SessionManager configWebSessionManager(){
	// DefaultWebSessionManager manager = new DefaultWebSessionManager();
	// // 加入缓存管理器
	// //manager.setCacheManager(cacheManager);
	// //manager.setSessionDAO(sessionDao);
	// // 删除过期的session
	// manager.setDeleteInvalidSessions(true);
	// // 设置全局session超时时间
	// manager.setGlobalSessionTimeout(1800000);
	// // 是否定时检查session
	// manager.setSessionValidationSchedulerEnabled(true);
	// manager.setSessionIdCookie(simpleCookie());
	// return manager;
	// }

	// /**
	// * 注入cookie模板
	// * @return
	// */
	// @Bean
	// public SimpleCookie simpleCookie(){
	// SimpleCookie simpleCookie = new SimpleCookie("sid-shrio");
	// simpleCookie.setMaxAge(-1);
	// simpleCookie.setHttpOnly(true);
	// return simpleCookie;
	// }
}

```

#### 3、常用

##### A、FormAuthenticationFilter 

继承FormAuthenticationFilter实现一些

## SpringBoot整合SpringData

### 四、踩坑

#### 1、save无法获取自增主键

解决方案：

- 为实体类的id注解 @GeneratedValue(strategy=GenerationType.IDENTITY) 指定id的生成策略

```java

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "article_id", nullable = false)
    public int getArticleId() {
        return articleId;
    }
 
    public void setArticleId(int articleId) {
        this.articleId = articleId;
    }
```

- 获取自增id

```java
 ArticleEntity article=articleRepository.saveAndFlush(articleEntity);
 int id=article.getArticleId();
```



## SpringBoot整合UrlRewrite

### 一、介绍

如楼主般配置，shiro和Urlrewirte整合，shiro的拦截器首先执行，后续才是urlrewrite的拦截器

验证：urlrewrite拦截器打上断点 org.tuckey.web.filters.urlrewrite.UrlRewriteFilter#doFilter

​           shiro的拦截器打上断点 org.apache.shiro.web.servlet.OncePerRequestFilter#doFilter

### 二、整合

#### 1、导入jar

```pom
<urlrewrite.version>3.2.0</urlrewrite.version>
```

```pom
<!-- urlRewrite -->
<dependency>
    <groupId>org.tuckey</groupId>
    <artifactId>urlrewritefilter</artifactId>
    <version>${urlrewrite.version}</version>
</dependency>
```

#### 2、配置类

```java
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.tuckey.web.filters.urlrewrite.Conf;
import org.tuckey.web.filters.urlrewrite.UrlRewriteFilter;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import java.io.IOException;

/**
 * @author sjp
 * @date 2019/1/19
 **/
@Configuration
public class UrlRewriteFilterConfig extends UrlRewriteFilter {

    @Value("classpath:/urlRewrite/urlRewrite.xml")
    private Resource resource;

    @Override
    protected void loadUrlRewriter(FilterConfig filterConfig) throws ServletException {
        try {
            // lemon-user为系统名
            Conf conf = new Conf(filterConfig.getServletContext(), resource.getInputStream(), resource.getFilename(),
                "lemon-user");
            checkConf(conf);
        } catch (IOException ex) {
            throw new ServletException("Unable to load URL rewrite configuration file from classpath:/urlRewrite/urlRewrite.xml", ex);
        }
    }

}
```

### 三、采坑

问题：SpringBoot整合shiro和UrlRewrite，请求接口一直报错。

**推荐文章：[Spring Boot整合shiro出现UnavailableSecurityManagerException](https://www.cnblogs.com/ginponson/p/6217057.html)**

```log
org.apache.shiro.UnavailableSecurityManagerException: No SecurityManager accessible to the calling code, either bound to the org.apache.shiro.util.ThreadContext or as a vm static singleton.  This is an invalid application configuration.
```

解决：

```java
/**
 * 使用FilterRegistrationBean管理DelegatingFilterProxy的生命周期，代替spring项目中shiro在web.xml的配置
 *
 * @return FilterRegistrationBean<DelegatingFilterProxy>
 */
@Bean
public FilterRegistrationBean<DelegatingFilterProxy> delegatingFilterProxy() {
    FilterRegistrationBean<DelegatingFilterProxy> filterRegistrationBean = new FilterRegistrationBean<DelegatingFilterProxy>();
    DelegatingFilterProxy proxy = new DelegatingFilterProxy();
    filterRegistrationBean.setFilter(proxy);
    // 保留Filter原有的init，destroy方法的调用
    proxy.setTargetFilterLifecycle(true);
    proxy.setTargetBeanName("shiroFilter");
    return filterRegistrationBean;
}

@Bean("shiroFilter")
public ShiroFilterFactoryBean shiroFilterFactoryBean() {
    ShiroFilterFactoryBean filterFactoryBean = new ShiroFilterFactoryBean();
    // .......
    return filterFactoryBean;
}
```



## SpringBoot-Actuator 健康监控

### 一、介绍

推荐文章：https://www.jianshu.com/p/1aadc4c85f51

### 二、整合

#### 1、导入jar

```pom
<!-- 健康监控 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

#### 2、配置yml

```yml
# actuator 监控
management:
    endpoints:
        web:
            exposure:
                # 开启全部监控请求
                include: "*"
    server:
        # 监控端口
        port: 10111
        servlet:
            context-path: /
        ssl:
            enabled: false
    endpoint:
        health:
            # heath展示全部信息
            show-details: always

info:
    app.name: eureka-provider
    company.name: www.demo.com
    build.artifactId: project.artifactId
    build.version: 0.0.1
```

注意：

监控本身不具有安全认证，所以一般如果要实际使用，需配合 spring-boot-starter-security 使用，另外，actuator自带监控可能不满足场景业务需求，所以可以集成自己的需要的监控。

## SpringBoot-整合UrlRewriter

### 一、介绍

&emsp;Urlrewriter的作用主要是重写url路径，以此来隐藏真实的路径，如："http://www.xxxx.com/crm_index.do"，而真实访问的是"http://www.xxxx.com/crm/index.jsp"。还有一种就是，在高并发访问的时候，可以更具是否用静态文件来进行路径的重写。

### 二、整合

第一步 jar包

```java
<dependency>
     <groupId>org.tuckey</groupId>
     <artifactId>urlrewritefilter</artifactId>
     <version>4.0.3</version>
</dependency>
```

第二步 配置类

```java

import java.io.IOException;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.tuckey.web.filters.urlrewrite.Conf;
import org.tuckey.web.filters.urlrewrite.UrlRewriteFilter;

@Configuration
public class UrlRewriteFilterConfig extends UrlRewriteFilter {

    @Value("classpath:/urlRewrite.xml")
    private Resource resource;

    @Override
    protected void loadUrlRewriter(FilterConfig filterConfig) throws ServletException {
        try {
            Conf conf = new Conf(filterConfig.getServletContext(), resource.getInputStream(), resource.getFilename(),
                "lemon-user");
            checkConf(conf);
        } catch (IOException ex) {
            throw new ServletException("Unable to load URL rewrite configuration file from classpath:/urlRewrite.xml", ex);
        }
    }
}
```

第三步 配置文件

```xml
<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE urlrewrite PUBLIC "-//tuckey.org//DTD UrlRewrite 4.0//EN"
    "http://www.tuckey.org/res/dtds/urlrewrite4.0.dtd">

<urlrewrite>
    <rule>
        <from>^/test.html$</from>
        <to type="redirect">/test</to>
    </rule>
</urlrewrite>
```

到此整合完成，下面我们进行测试，启动项目，访问127.0.0.1:80/test.html，回车后url变为127.0.0.1:80/test，即成功。



小提示：

问：应用拆分，同一个域名，如何映射各个应用？

（总不至于，每个接口的url，前面都加上应用区分的url吧，如“/u/user/login”,"/a/admin/login",/u 和 /a 仅仅用来区分应用，如果我们在代码中都加上，肯定不是一个好的解决方案）

答：在应用端，使用UrlRewriter帮助我们重写请求，这样可以做到代码层面不加上区分应用的Url，直接配置在UrlRewriter中，重写Url即可。（解决方案不止这一种）

UrlRewriter快速了解文章入口: <a href="https://blog.csdn.net/u010690828/article/details/53351897">关于UrlRewrite的使用</a>

# SpringCloud系列

> 以下内容，均在搭建 [lemon](https://github.com/ATSJP/lemon) 项目时，收集和写下的一些内容，如有错误欢迎指正>>>[@SJP](mailto:shijianpeng2010@163.com)

## 实际中应用及推荐文章

推荐文章：https://blog.csdn.net/zrl0506/article/details/80165477

![1548589316026](F:\文件\桌面\markdown筆記\note\SpringBoot&SpringCloud\assets\20170918114736747.png)

推荐文章：https://blog.csdn.net/qq_37170583/article/details/80704904

## SpringCloud和SpringBoot对应版本

官方文档：http://spring.io/projects/spring-cloud（一切始于官方文档）

**Table1**

| SpringCloud | SpringBoot |
| ----------- | ---------- |
| Greenwich   | 2.1.X      |
| Finchley    | 2.0.X      |
| Edgware     | 1.5.x      |
| Dalston     | 1.5.x      |

**Table2**

| Component                 | Edgware.SR5    | Finchley.SR2  | Finchley.BUILD-SNAPSHOT |
| ------------------------- | -------------- | ------------- | ----------------------- |
| spring-cloud-aws          | 1.2.3.RELEASE  | 2.0.1.RELEASE | 2.0.1.BUILD-SNAPSHOT    |
| spring-cloud-bus          | 1.3.3.RELEASE  | 2.0.0.RELEASE | 2.0.1.BUILD-SNAPSHOT    |
| spring-cloud-cli          | 1.4.1.RELEASE  | 2.0.0.RELEASE | 2.0.1.BUILD-SNAPSHOT    |
| spring-cloud-commons      | 1.3.5.RELEASE  | 2.0.2.RELEASE | 2.0.2.BUILD-SNAPSHOT    |
| spring-cloud-contract     | 1.2.6.RELEASE  | 2.0.2.RELEASE | 2.0.2.BUILD-SNAPSHOT    |
| spring-cloud-config       | 1.4.5.RELEASE  | 2.0.2.RELEASE | 2.0.2.BUILD-SNAPSHOT    |
| spring-cloud-netflix      | 1.4.6.RELEASE  | 2.0.2.RELEASE | 2.0.2.BUILD-SNAPSHOT    |
| spring-cloud-security     | 1.2.3.RELEASE  | 2.0.1.RELEASE | 2.0.1.BUILD-SNAPSHOT    |
| spring-cloud-cloudfoundry | 1.1.2.RELEASE  | 2.0.1.RELEASE | 2.0.1.BUILD-SNAPSHOT    |
| spring-cloud-consul       | 1.3.5.RELEASE  | 2.0.1.RELEASE | 2.0.2.BUILD-SNAPSHOT    |
| spring-cloud-sleuth       | 1.3.5.RELEASE  | 2.0.2.RELEASE | 2.0.2.BUILD-SNAPSHOT    |
| spring-cloud-stream       | Ditmars.SR4    | Elmhurst.SR1  | Elmhurst.BUILD-SNAPSHOT |
| spring-cloud-zookeeper    | 1.2.2.RELEASE  | 2.0.0.RELEASE | 2.0.1.BUILD-SNAPSHOT    |
| spring-boot               | 1.5.16.RELEASE | 2.0.6.RELEASE | 2.0.7.BUILD-SNAPSHOT    |
| spring-cloud-task         | 1.2.3.RELEASE  | 2.0.0.RELEASE | 2.0.1.BUILD-SNAPSHOT    |
| spring-cloud-vault        | 1.1.2.RELEASE  | 2.0.2.RELEASE | 2.0.2.BUILD-SNAPSHOT    |
| spring-cloud-gateway      | 1.0.2.RELEASE  | 2.0.2.RELEASE | 2.0.2.BUILD-SNAPSHOT    |
| spring-cloud-openfeign    |                | 2.0.2.RELEASE | 2.0.2.BUILD-SNAPSHOT    |
| spring-cloud-function     | 1.0.1.RELEASE  | 1.0.0.RELEASE | 1.0.1.BUILD-SNAPSHOT    |

注意：

SpringCloud 版本为 Edgware 及以下，eureka包改为：

```pom
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-eureka-server</artifactId>
</dependency>
```

SpringCloud 版本为 Edgware 以上，eureka包改为netflix：

```pom
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
</dependency>
```

## SpringCloudEureka

### 入门案例（一） Server与Client

### 入门案例（二）Server、Porvider与Consumer

#### 项目整体结构：

![1548589316026](F:\文件\桌面\markdown筆記\note\SpringBoot&SpringCloud\assets\1548589316026.png)

#### eureka-server入门

##### 一、导入jar

```pom
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <artifactId>lemon-soa</artifactId>
    <packaging>jar</packaging>

    <name>spring-cloud-eureka-server</name>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>1.5.9.RELEASE</version>
        <relativePath/>
    </parent>

    <properties>
        <spring-cloud.version>Dalston.SR4</spring-cloud.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-eureka-server</artifactId>
        </dependency>
    </dependencies>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

</project>

```

##### 二、建立启动类

```java
package com.lemon.eureka.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @author sjp
 */
@EnableEurekaServer
@SpringBootApplication
public class EurekaServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(EurekaServerApplication.class, args);
	}

}
```

##### 三、配置yml

```yml
server:
    port: 9001

eureka:
    instance:
        hostname: eureka-service
    client:
        # 不注册自己
        register-with-eureka: false
        # 获取服务
        fetch-registry: false
        # 注册中心地址
        service-url:
            defaultZone: http://localhost:${server.port}/eureka/

```

##### 四、访问

http://localhost:9001/

![1548158001871](F:\文件\桌面\markdown筆記\note\SpringBoot\assets\1548158001871.png)

#### eureka-api 入门

**Tips**:此部分属于provider和consumer公用部分，所以单独作为一个模块，打包成jar供provider和consumer使用

##### 模块结构

![1548340246457](F:\文件\桌面\markdown筆記\note\SpringBoot&SpringCloud\assets\1548340246457.png)

##### 一、导入jar

```pom
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <artifactId>eureka-api</artifactId>

</project>

```

##### 二、建立相关类

```java
package com.lemon.soa.api;

public interface VideoService {
   /**
    * 获取视频信息
    * @param videoId 视频id
    * @return 视频信息
    */
   double getVideo(long videoId);
}
```

#### eureka-provider入门

**Tips**: Eureka本身只区分server和client(client有provider和consumer)，通过不同的配置来告知client，本身是provider还是consumer.

##### 模块结构

![1548340282069](F:\文件\桌面\markdown筆記\note\SpringBoot\assets\1548340282069.png)

##### 一、导入jar

```pom
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <artifactId>eureka-provider</artifactId>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>1.5.9.RELEASE</version>
        <relativePath/>
    </parent>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
        <java.version>1.8</java.version>
        <spring-cloud.version>Dalston.SR4</spring-cloud.version>
    </properties>

    <dependencies>

        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-eureka</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>

        <!--内部依赖-->
        <dependency>
            <groupId>com.lemon</groupId>
            <artifactId>eureka-api</artifactId>
            <version>0.0.1-SNAPSHOT</version>
        </dependency>

    </dependencies>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

</project>

```

##### 二、建立相关类

启动类：

```java
package com.lemon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class EurekaProviderApplication {

	public static void main(String[] args) {
		SpringApplication.run(EurekaProviderApplication.class, args);
	}

}
```

服务接口实现：

```java
package com.lemon.api.impl;

import org.springframework.stereotype.Service;

import com.lemon.soa.api.VideoService;

/**
 * @author sjp
 * @date 2019/1/24
 **/
@Service
public class VideoServiceImpl implements VideoService {
   @Override
   public double getVideo(long videoId) {
      return Math.random();
   }
}
```

暴露服务：

```java
package com.lemon.controller;

import com.lemon.soa.api.VideoService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class VideoController {

   @Resource
   private VideoService videoService;

   @RequestMapping(value = "/{videoId}", method = RequestMethod.GET)
   public Double getVideo(@PathVariable long videoId) {
      return videoService.getVideo(videoId);
   }
}
```

##### 三、配置yml

```yml
server:
    port: 9002

spring:
    application:
        name: eureka-provider

eureka:
    instance:
        #使用ip进行注册
        prefer-ip-address: true
    client:
        serviceUrl:
            defaultZone: http://localhost:9001/eureka/
```

##### 四、访问

http://localhost:9001/

![1548331878651](F:\文件\桌面\markdown筆記\note\SpringBoot&SpringCloud\assets\1548331878651.png)



#### eureka-consumer入门

##### 模块结构

![1548340368984](F:\文件\桌面\markdown筆記\note\SpringBoot&SpringCloud\assets\1548340368984.png)

##### 一、导入jar

```pom
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <artifactId>eureka-consumer</artifactId>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>1.5.12.RELEASE</version>
    </parent>

    <properties>
        <spring-cloud.version>Edgware.SR3</spring-cloud.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-eureka</artifactId>
        </dependency>
    </dependencies>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

</project>

```

##### 二、建立相关类

启动类：

```java
package com.lemon.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * @author sjp
 * @date 2019/1/24
 **/
@EnableDiscoveryClient
@SpringBootApplication
public class EurekaConsumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(EurekaConsumerApplication.class, args);
	}

	/**
	 * 启用负载均衡，默认算法是轮询
	 */
	@LoadBalanced
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}
```

调用服务：

```java
package com.lemon.consumer.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

/**
 * @author sjp
 * @date 2019/1/24
 **/
@RestController
public class ConsumerController {

   @Resource
   private RestTemplate restTemplate;

   @RequestMapping("/")
   public Double index() {
        return restTemplate.getForObject("http://eureka-provider/1", Double.class);
   }
}
```

##### 三、配置yml

```yml
server:
    port: 9003

spring:
    application:
        name: eureka-consumer
eureka:
    instance:
        # 使用IP注册
        prefer-ip-address: true
    #注册地址
    client:
        service-url:
            defaultZone: http://localhost:9001/eureka/
```

##### 四、访问

http://localhost:9001/

![1548339679969](F:\文件\桌面\markdown筆記\note\SpringBoot&SpringCloud\assets\1548339679969.png)





### 熟悉掌握

#### eureka-server配置详解

| 参数名                        | 默认值 | 备注                                                         |
| ----------------------------- | ------ | ------------------------------------------------------------ |
| enable-self-preservation      | true   | 自我保护模式，当出现出现网络分区、eureka在短时间内丢失过多客户端时，会进入自我保护模式，即一个服务长时间没有发送心跳，eureka  也不会将其删除，默认为true |
| eviction-interval-timer-in-ms | 60000  | eureka server清理无效节点的时间间隔，默认60000毫秒，即60秒   |
| a-s-g-cache-expiry-timeout-ms | 6000  | 缓存ASG信息的到期时间，单位为毫秒，默认为10 * 60 * 1000 |
| a-s-g-query-timeout-ms | 300 | 查询AWS上ASG（自动缩放组）信息的超时值，单位为毫秒，默认为300 |
| a-s-g-update-interval-ms | 5 * 60 * 1000  | 从AWS上更新ASG信息的时间间隔，单位为毫秒 |
| a-w-s-access-id |   | 获取aws访问的id，主要用于弹性ip绑定，此配置是用于aws上的 |
| a-w-s-secret-key |  | 获取aws私有秘钥，主要用于弹性ip绑定，此配置是用于aws上的 |
| batch-replication | false | 表示集群节点之间的复制是否为了网络效率而进行批处理 |
| binding-strategy |   | 获取配置绑定EIP或Route53的策略 |
| delta-retention-timer-interval-in-ms | 30 * 1000 | 清理任务程序被唤醒的时间间隔，清理过期的增量信息，单位为毫秒 |
| disable-delta | false  | 增量信息是否可以提供给客户端看，默认为false |
| disable-delta-for-remote-regions | false  | 增量信息是否可以提供给客户端或一些远程地区 |
| disable-transparent-fallback-to-other-region | false | 如果在远程区域本地没有实例运行，对于应用程序回退的旧行为是否被禁用 |
| e-i-p-bind-rebind-retries | 3  | 获取服务器尝试绑定到候选的EIP的次数|
| e-i-p-binding-retry-interval-ms-when-unbound | 1 * 60 * 1000 | 服务器检查ip绑定的时间间隔，单位为毫秒 |
| e-i-p-binding-retry-interval-ms | 5 * 60 * 1000 | 与上面的是同一作用，仅仅是稳定状态检查 |
| enable-replicated-request-compression | false | 复制的数据在发送请求时是否被压缩 |
| g-zip-content-from-remote-region | true | eureka服务器中获取的内容是否在远程地区被压缩，默认为true |
| json-codec-name |   | 如果没有设置默认的编解码器将使用全JSON编解码器，获取的是编码器的类名称 |
| list-auto-scaling-groups-role-name | ListAutoScalingGroups | 用来描述从AWS第三账户的自动缩放组中的角色名称 |
| log-identity-headers | true | Eureka服务器是否应该登录clientAuthHeaders |
| max-elements-in-peer-replication-pool | 10000 | 复制池备份复制事件的最大数量 |
| max-elements-in-status-replication-pool: | 10000 | 可允许的状态复制池备份复制事件的最大数量 |
| max-idle-thread-age-in-minutes-for-peer-replication | 10 | 状态复制线程可以保持存活的空闲时间 |
| min-threads-for-status-replication | 1 | 被用于状态复制的线程的最小数目 |
| max-idle-thread-in-minutes-age-for-status-replication | 15 | 复制线程可以保持存活的空闲时间,默认为15分钟 |
| max-threads-for-peer-replication | 20 | 获取将被用于复制线程的最大数目 |
| max-time-for-replication | 30000 | 尝试在丢弃复制事件之前进行复制的时间，默认为30000毫秒 |
| min-threads-for-peer-replication | 5 | 获取将被用于复制线程的最小数目 |
| number-of-replication-retries | 5 | 获取集群里服务器尝试复制数据的次数 |
| peer-eureka-nodes-update-interval-ms | 10 * 60 * 1000 | 集群里eureka节点的变化信息更新的时间间隔，单位为毫秒 |
| peer-eureka-status-refresh-time-interval-ms | 30 * 1000 | 服务器节点的状态信息被更新的时间间隔，单位为毫秒 |
| peer-node-connect-timeout-ms | 200 | 连接对等节点服务器复制的超时的时间，单位为毫秒 |
| peer-node-read-timeout-ms | 200 | 读取对等节点服务器复制的超时的时间，单位为毫秒 |
| peer-node-total-connections | 1000 | 获取对等节点上http连接的总数 |
| peer-node-connection-idle-timeout-seconds | 30 | http连接被清理之后服务器的空闲时间，默认为30秒 |
| peer-node-total-connections-per-host | 500  | 获取特定的对等节点上http连接的总数 |
| prime-aws-replica-connections | true | 对集群中服务器节点的连接是否应该准备 |
| rate-limiter-enabled |   | 限流是否应启用或禁用，默认为false |
| rate-limiter-burst-size | | 速率限制的burst size ，默认为10，这里用的是令牌桶算法 |
| rate-limiter-full-fetch-average-rate | 100 | 速率限制器用的是令牌桶算法，此配置指定平均执行请求速率，默认为100 |
| rate-limiter-privileged-clients |  | 认证的客户端列表，这里是除了标准的eureka Java客户端。 |
| rate-limiter-registry-fetch-average-rate | 500 | 速率限制器用的是令牌桶算法，此配置指定平均执行注册请求速率，默认为500 |
| rate-limiter-throttle-standard-clients | false | 是否对标准客户端进行限流，默认false |
| registry-sync-retries | 5 | 当eureka服务器启动时尝试去获取集群里其他服务器上的注册信息的次数，默认为5 |
| registry-sync-retry-wait-ms | 30 * 1000 | 当eureka服务器启动时获取其他服务器的注册信息失败时，会再次尝试获取，期间需要等待的时间，默认为30 * 1000毫秒 |
| remote-region-app-whitelist |  | 必须通过远程区域中检索的应用程序的列表 |
| remote-region-connect-timeout-ms | 1000 | 连接到对等远程地eureka节点的超时时间，默认为1000毫秒 |
| remote-region-connection-idle-timeout-seconds | 30 | http连接被清理之后远程地区服务器的空闲时间，默认为30秒 |
| remote-region-fetch-thread-pool-size | 20 | 用于执行远程区域注册表请求的线程池的大小，默认为20 |
| remote-region-read-timeout-ms | 1000 | 获取从远程地区eureka节点读取信息的超时时间，默认为1000毫秒 |
| remote-region-registry-fetch-interval | 30 | 从远程区域取出该注册表的信息的时间间隔，默认为30秒 |
| remote-region-total-connections | 1000 | 获取远程地区对等节点上http连接的总数，默认为1000 |
| remote-region-total-connections-per-host | 500 | 获取远程地区特定的对等节点上http连接的总数，默认为500 |
| remote-region-trust-store |  | 用来合格请求远程区域注册表的信任存储文件，默认为空 |
| remote-region-trust-store-password |  | 获取偏远地区信任存储文件的密码，默认为“changeit” |
| remote-region-urls |  | 远程地区的URL列表 |
| remote-region-urls-with-name |  | 针对远程地区发现的网址域名的map |
| renewal-percent-threshold | 0.85 | 阈值因子，默认是0.85，如果阈值比最小值大，则自我保护模式开启 |
| renewal-threshold-update-interval-ms | 15 * 60 * 1000  | 阈值更新的时间间隔，单位为毫秒 |
| response-cache-auto-expiration-in-seconds | 180 | 当注册表信息被改变时，则其被保存在缓存中不失效的时间，默认为180秒 |
| response-cache-update-interval-ms | 30 * 1000 | 客户端的有效负载缓存应该更新的时间间隔，默认为30 * 1000毫秒 |
| retention-time-in-m-s-in-delta-queue | 3 * 60 * 1000 | 客户端保持增量信息缓存的时间，从而保证不会丢失这些信息，单位为毫秒 |
| route53-bind-rebind-retries | 3 | 服务器尝试绑定到候选Route53域的次数 |
| route53-binding-retry-interval-ms | 5 * 60 * 1000  | 服务器应该检查是否和Route53域绑定的时间间隔，默认为5 * 60 * 1000毫秒 |
| route53-domain-t-t-l | 301 | 用于建立route53域的ttl，默认为301 |
| sync-when-timestamp-differs | true | 当时间变化实例是否跟着同步，默认为true |
| use-read-only-response-cache | true | 目前采用的是二级缓存策略，一个是读写高速缓存过期策略，另一个没有过期只有只读缓存，默认为true，表示只读缓存 |
| wait-time-in-ms-when-sync-empty | 1000 * 60 * 5  | 在Eureka服务器获取不到集群里对等服务器上的实例时，需要等待的时间，单位为毫秒，默认为1000 * 60 * 5 |
| xml-codec-name |  | 如果没有设置默认的编解码器将使用xml编解码器，获取的是编码器的类名称 |



## SpringCloudFegin

官方文档：https://cloud.spring.io/spring-cloud-static/spring-cloud-openfeign/2.0.2.RELEASE/single/spring-cloud-openfeign.html

##### 一、导入jar

Springboot 2.0.0 以下

```
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-feign</artifactId>
</dependency>
```

Springboot 2.0.0 及以上

```pom
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```

##### 二、建立相关类

启动类(加上注解)：

```java
/**
  * 如果为了把eureka共有接口抽成单独模块，需注明扫描包，才可以加载jar包中的@FeignClient
  * @EnableFeignClients(basePackages = { "com.lemon.soa.api" })
  */
@EnableFeignClients
```

调用服务：

```java
package com.lemon.consumer.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

/**
 * @author sjp
 * @date 2019/1/24
 **/
@RestController
public class ConsumerController {

   @Resource
   private RestTemplate restTemplate;

   @RequestMapping("/")
   public Double index() {
        return restTemplate.getForObject("http://eureka-provider/1", Double.class);
   }
}
```

##### 三、配置yml

```yml
server:
    port: 9003

spring:
    application:
        name: eureka-consumer
eureka:
    instance:
        # 使用IP注册
        prefer-ip-address: true
    #注册地址
    client:
        service-url:
            defaultZone: http://localhost:9001/eureka/
```

##### 四、访问

http://localhost:9001/

![1548339679969](F:\文件\桌面\markdown筆記\note\SpringBoot&SpringCloud\assets\1548339679969.png)





坑：

有些公共的组件抽出来其他模块的maven依赖，此时要在使用的项目中加载此jar包的spring component以及feign组件，仅仅依靠@ComponentScan是不够的，还需要在@EnableFeignClients(basePackages = {"com.xixicat"})中标注basekPackages。

