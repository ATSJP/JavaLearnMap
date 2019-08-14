# Shiro如何做到无状态

### 一、介绍

#### 1、为什么要做无状态

​      前后端分离带来的优越性，使得更多的企业选择它。分离后的两者，交互只有后端接口返回Json数据（或其他类型数据），前端页面的跳转等无需经过后端，故传统的Session（通过在浏览器中的JSESSIONID，对应服务内存中的存储的Session数据来完成Session的功能）不在存在，所以后端需要和前端约定认证方式，而Token不失为一种解决方案。

### 二、使用

声明：以下基于项目 SpringCloud项目 [Lemon](https://github.com/ATSJP/lemon) 完成设计，直接看效果分析，请下载项目，确保sql执行，数据库密码Redis密码修改好(目前已改为配置中心，请按照项目内，开发文档进行使用)，启动lemon-user子应用即可。

#### 1、正常配置Shiro（参考网上教程即可）

#### 2、无状态Shiro

##### A、拦截器

```java
import com.alibaba.fastjson.JSONObject;
import com.lemon.shiro.token.StatelessToken;
import com.lemon.utils.CookieUtils;
import com.lemon.web.constant.base.ConstantApi;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 无状态拦截器
 * 
 * @author sjp
 * @date 2019/4/30
 **/
public class StatelessAuthcFilter extends AccessControlFilter {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 判断请求是否需要被拦截，拦截后执行onAccessDenied方法
     * 
     * @param request req
     * @param response res
     * @param mappedValue map
     * @return true 不拦截 false 拦截
     */
	@Override
	protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
		return false;
	}

	@Override
	protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
		Cookie[] cookies = ((HttpServletRequest) request).getCookies();
		// 用户唯一id
		String uidStr = CookieUtils.getParamFromCookie(cookies, ConstantApi.UID);
		if (StringUtils.isEmpty(uidStr)) {
			onLoginFail(response);
			return false;
		}
		Long uid = Long.parseLong(uidStr);
		// 用户token
		String token = CookieUtils.getParamFromCookie(cookies, ConstantApi.TOKEN);
		if (StringUtils.isEmpty(token)) {
			onLoginFail(response);
			return false;
		}
		// 用户所在平台
		String sid = CookieUtils.getParamFromCookie(cookies, ConstantApi.SID);
		if (StringUtils.isEmpty(sid)) {
			onLoginFail(response);
			return false;
		}
		// 客户端请求的参数列表
		Map<String, String[]> params = new HashMap<>(request.getParameterMap());
		StatelessToken statelessToken = new StatelessToken(uid, token, params);
		try {
			// 委托给Realm进行登录
			getSubject(request, response).login(statelessToken);
		} catch (Exception e) {
			logger.info("auth error->uid:{},sid:{},token:{},e:{}", uid, sid, token, e);
			// 登录失败
			onLoginFail(response);
			return false;
		}
		return true;
	}

	/**
	 * 登录失败时默认返回401状态码
	 */
	private void onLoginFail(ServletResponse response) throws IOException {
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		httpResponse.setStatus(HttpServletResponse.SC_OK);
		httpResponse.setContentType("application/json;charset=UTF-8");
		httpResponse.getWriter().write("error");
	}

}
```

##### B、登录认证器和无状态Token认证器

```java
package com.lemon.relam;

import com.lemon.entity.LoginInfoEntity;
import com.lemon.repository.LoginInfoRepository;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Set;

/**
 * 登陆和授权认证器
 *
 * @author sjp 2018/12/9
 */
public class LoginRelam extends AuthorizingRealm {

	@Resource
	private LoginInfoRepository	loginInfoRepository;

	private static final String	MONSTER	= "monster";

	/**
	 * 登陆
	 *
	 * @param authenticationToken token
	 * @return info
	 * @throws AuthenticationException exception
	 */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken)
			throws AuthenticationException {
		UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) authenticationToken;
		LoginInfoEntity loginInfoEntity = loginInfoRepository.getByLoginName(usernamePasswordToken.getUsername());
		if (loginInfoEntity == null) {
			throw new UnknownAccountException("用户不存在！");
		}
		if (MONSTER.equals(loginInfoEntity.getLoginName())) {
			throw new LockedAccountException("用户被锁定");
		}
		// 盐值加密，确保凭证一样加密后字符串不一样
		ByteSource credentialsSalt = ByteSource.Util.bytes(loginInfoEntity.getLoginName());
		return new SimpleAuthenticationInfo(loginInfoEntity.getLoginName(), loginInfoEntity.getLoginPwd(),
				credentialsSalt, getName());
	}

	/**
	 * 授权
	 *
	 * @param principalCollection p
	 * @return a
	 */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
		Object principal = principalCollection.getPrimaryPrincipal();
		Set<String> roles = new HashSet<>();
		roles.add("user");
         // 此处授权角色仅供测试
		if ("admin".equals(principal)) {
			roles.add("admin");
		}
		return new SimpleAuthorizationInfo(roles);
	}

}
```

```java
package com.lemon.shiro.realm;

import com.lemon.shiro.token.StatelessToken;
import com.lemon.tools.RedissonTools;
import com.lemon.web.constant.base.ConstantCache;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Set;

/**
 * 无状态认证器
 * 
 * @author sjp
 * @date 2019/4/30
 **/
public class StatelessRealm extends AuthorizingRealm {

	@Resource
	private RedissonTools redissonTools;

	@Override
	public boolean supports(AuthenticationToken token) {
		// 仅支持StatelessToken类型的Token
		return token instanceof StatelessToken;
	}

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		Set<String> roles = new HashSet<>();
		// 此处可以来获取权限
		return new SimpleAuthorizationInfo(roles);
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken)
			throws AuthenticationException {
		StatelessToken statelessToken = (StatelessToken) authenticationToken;
		Long uid = statelessToken.getUid();
         // 从Redis获取服务器存储的Token，用来和前端传入的Token对比 
		// String token = redissonTools.get(ConstantCache.KEY.LOGIN_TOKEN.key + uid);
         // 临时测试 可以设置常量
		String token = "testToken";
         token = StringUtils.isEmpty(token) ? "" : token;
		return new SimpleAuthenticationInfo(uid, token, getName());
	}

}
```

##### C、配置类

```java
import com.lemon.relam.LoginRelam;
import com.lemon.shiro.factory.StatelessDefaultSubjectFactory;
import com.lemon.shiro.filter.StatelessAuthcFilter;
import com.lemon.shiro.realm.StatelessRealm;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.mgt.DefaultSessionManager;
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

import javax.servlet.Filter;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Shiro配置
 * 
 * @author sjp
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
		FilterRegistrationBean<DelegatingFilterProxy> filterRegistrationBean = new FilterRegistrationBean<>();
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
		Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
		filterChainDefinitionMap.put("/user/login", "anon");
		filterChainDefinitionMap.put("/user/register", "anon");
		filterChainDefinitionMap.put("/user/logout", "anon");
		filterChainDefinitionMap.put("/**", "statelessAuthcFilter");
		shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
		// 自定义拦截器
		Map<String, Filter> filtersMap = new LinkedHashMap<>();
		filtersMap.put("statelessAuthcFilter", new StatelessAuthcFilter());
		shiroFilterFactoryBean.setFilters(filtersMap);
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
		// 设置不储存session
		((DefaultSessionStorageEvaluator) ((DefaultSubjectDAO) securityManager.getSubjectDAO())
				.getSessionStorageEvaluator()).setSessionStorageEnabled(false);
		// 设置会话管理器
		securityManager.setSessionManager(defaultSessionManager());
		// 设置无状态SubjectFactory
		securityManager.setSubjectFactory(statelessDefaultSubjectFactory());
		// 设置realm
		LinkedList<Realm> realms = new LinkedList<>();
		realms.add(loginRealm());
		realms.add(statelessRealm());
		securityManager.setRealms(realms);
		return securityManager;
	}

	/**
	 * 会话管理器
	 * 
	 * @return DefaultSessionManager
	 */
	@Bean
	public DefaultSessionManager defaultSessionManager() {
		DefaultSessionManager defaultSessionManager = new DefaultSessionManager();
		defaultSessionManager.setSessionValidationSchedulerEnabled(false);
		return defaultSessionManager;
	}

	/**
	 * 无状态SubjectFactory
	 *
	 * @return StatelessDefaultSubjectFactory
	 */
	@Bean
	public StatelessDefaultSubjectFactory statelessDefaultSubjectFactory() {
		return new StatelessDefaultSubjectFactory();
	}

	/**
	 * 配置shiroRelam并指定凭证匹配器
	 *
	 * @return LoginRealm
	 */
	@Bean
	public LoginRelam loginRealm() {
		LoginRelam loginRelam = new LoginRelam();
		loginRelam.setCredentialsMatcher(hashedCredentialsMatcher());
		return loginRelam;
	}

	/**
	 * 配置无状态登陆realm
	 *
	 * @return StatelessRealm
	 */
	@Bean
	public StatelessRealm statelessRealm() {
		StatelessRealm statelessRealm = new StatelessRealm();
		statelessRealm.setCachingEnabled(false);
		return statelessRealm;
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
		// true 基于类方法代码，false 基于接口代理
		proxyCreator.setProxyTargetClass(true);
		return proxyCreator;
	}

	/**
	 * 开启shiro aop注解支持. 使用代理方式;所以需要开启代码支持;
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



##### D、工具类

- ConstantApi

  ```java
  /**
   * API接口公用返回提示code定义
   * 
   * @author sjp
   * @date 2019/4/15
   **/
  public interface ConstantApi {
  
      String	TOKEN	= "token";
      String	UID		= "uid";
      String	SID		= "sid";
  
  }
  ```

- cookieUtils

```java
import org.springframework.util.CollectionUtils;

import javax.servlet.http.Cookie;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * CookieUtils
 *
 * @author sjp
 * @date 2019/5/3
 */
public class CookieUtils {

	/**
	 * 从cookie中获取名称的值
	 * 
	 * @param cookies cookies
	 * @param name cookie值名称
	 * @return value cookie值
	 */
	public static String getParamFromCookie(Cookie[] cookies, String name) {
		if (cookies == null || cookies.length == 0) {
			return "";
		}
		List<Cookie> cookieList = Arrays.stream(cookies).filter(item -> name.equals(item.getName()))
				.collect(Collectors.toList());
		if (CollectionUtils.isEmpty(cookieList)) {
			return "";
		}
		return cookieList.get(0).getValue();
	}

}
```

- RedissonTools

```java
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * redisson工具
 * 
 * @author sjp
 * @date 2019/1/16
 **/
@Service
public class RedissonTools {

	@Resource
	private RedissonClient	redissonClient;

	public <T> T get(String name) {
		RBucket<T> r = redissonClient.getBucket(name);
		return r.get();
	}

	public <T> void set(String name, T value) {
		RBucket<T> r = redissonClient.getBucket(name);
		r.setAsync(value);
	}

	public <T> T set(String name, T value, int expiredSeconds) {
		RBucket<T> r = redissonClient.getBucket(name);
		r.setAsync(value, expiredSeconds, TimeUnit.SECONDS);
		return r.get();
	}
}
```

```java
import org.apache.commons.codec.digest.DigestUtils;

/**
 * Token生成器
 * 
 * @author sjp
 * @date 2019/2/25
 **/
public class TokenGenerate {

	/**
	 * 统一加密Key
	 */
	private static final String KEY = "LEMON_API";

	/**
	 * 生成token
	 *
	 * @param uid 用户的id
	 * @param sid 用户所在平台
	 * @return String token
	 */
	public static String getToken(Long uid, String sid) {
		String sb = uid + sid + System.currentTimeMillis() + KEY;
		return DigestUtils.md5Hex(sb).toUpperCase();
	}

}
```

​	有了以上的配置，我们就可以正常测试了，大家都知道Shiro的认证器可以多个，策略可以配置成：只要有一个通过即可。这样当用户首次登录的时候，我们希望他通过LoginRelam认证通过，在登录成功后，拿着服务器给的Token请求其他接口。StatelessRealm认证器就会通过认证放行。

​	为了确保用户的Token可以识别出用户身份，所以加密的时候选择将用户的UID加密进去，待下次请求使用同样的参数进行Md5对比，来确保用户信息安全。

### 三、心得

#### 1、传统的Session如何做到绑定状态的？

通过JSESSIONID绑定当前登陆用户，如下图所示，浏览器的JsessionId对应着服务器内存中的某一个Session，而服务器的Session保存了代码中设置的信息，常用的 session.setAttribute("userInfo",userInfo) 设置用户的信息到Session中。如果清除掉JSESSIONID，则服务器认为该浏览器未登录，用户将会变为未登录状态。

![1561617653644](https://raw.githubusercontent.com/ATSJP/note/master/shiro/assets/1561617653644.png)



推荐博客：

一、[Spring Boot集成无状态Shiro--内容详细介绍](<https://blog.csdn.net/qq_35981283/article/details/78619750>)

二、[Shiro学习（20）无状态Web应用集成](https://blog.csdn.net/qq_32347977/article/details/51094665)

