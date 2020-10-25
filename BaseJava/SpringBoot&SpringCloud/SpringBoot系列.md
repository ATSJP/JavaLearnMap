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

### 三、踩坑

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

## Springboot整合Redis

## Springboot整合Redisson

### 一、介绍

### 二、整合

#### 1、引入POM

```POM
        <spring-boot.version>2.1.1.RELEASE</spring-boot.version>
        <redisson.version>3.10.6</redisson.version>
        <redisson-springboot.version>3.10.6</redisson-springboot.version>
         
         <!-- redisson -->
         <dependency>
             <groupId>org.redisson</groupId>
             <artifactId>redisson-spring-boot-starter</artifactId>
             <version>${redisson-springboot.version}</version>
         </dependency>
         <dependency>
             <groupId>org.redisson</groupId>
             <artifactId>redisson</artifactId>
             <version>${redisson.version}</version>
         </dependency>
```

#### 2、配置文件

Tips:新建redisson-config.yml配置文件

```yml
# Redisson配置
singleServerConfig:
    address: "redis://127.0.0.1:6379"
    password: null
    clientName: null
    database: 0
    idleConnectionTimeout: 10000
    pingTimeout: 1000
    connectTimeout: 10000
    timeout: 3000
    retryAttempts: 3
    retryInterval: 1500
    reconnectionTimeout: 3000
    failedAttempts: 3
    subscriptionsPerConnection: 5
    subscriptionConnectionMinimumIdleSize: 1
    subscriptionConnectionPoolSize: 50
    connectionMinimumIdleSize: 32
    connectionPoolSize: 64
    dnsMonitoringInterval: 5000
    # dnsMonitoring: false

threads: 0
nettyThreads: 0
codec:
    class: "org.redisson.codec.JsonJacksonCodec"
transportMode: "NIO"
```

#### 3、写配置类

```java
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * @author sjp
 * @date 2019/4/30
 **/
@Configuration
public class RedissonConfig {

    @Bean
    public RedissonClient redisson() throws IOException {
        // 本模块使用的是yaml格式的配置文件，读取使用Config.fromYAML，如果是Json文件，则使用Config.fromJSON
        Config config = Config.fromYAML(RedissonConfig.class.getClassLoader().getResource("redisson-config.yml"));
        return Redisson.create(config);
    }

}
```

#### 4、测试类

```java
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RBucket;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RunWith(SpringRunner.class)
@Component
public class RedissonTest {

    @Autowired
    private RedissonClient redisson;

    @Autowired
    private RedisTemplate<String, String> template;

    @Test
    public void set() {
        redisson.getKeys().flushall();
        RBucket<String> rBucket =  redisson.getBucket("key");
        rBucket.set("1231");
        RMap<String, String> m = redisson.getMap("test", StringCodec.INSTANCE);
        m.put("1", "2");
        BoundHashOperations<String, String, String> hash = template.boundHashOps("test");
        String t = hash.get("1");
        assertThat(t).isEqualTo("2");
    }

}
```

### 三、踩坑

#### 1、redisson连接池的连接无法释放

Springboot2.1.1 之前的版本在接入redisson的时候，redission无法自动释放redis连接，会导致池中连接都在占用状态，后续的请求将无法从连接池中获取连接，导致请求阻塞。

解决方案，使用 2.1.1及之后的版本即可

// TODO 具体原因待分析。

#### 2、开发阶段应用经常重启，导致redis已经被应用占用的连接无法释放。

// TODO 待解决方案，在系统关闭时，调用redisson.shutdown() 