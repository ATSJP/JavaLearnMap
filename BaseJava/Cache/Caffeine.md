[TOC]



# Caffeine

## Caches

### 介绍

各位移步项目Github，查阅即可，有中文版和英文版。

> Github：https://github.com/ben-manes/caffeine/wiki

## 实现

### SpringBoot健康检查

#### SpringBoot实现

版本：

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-actuator</artifactId>
  <version>2.5.4</version>
</dependency>
```



入口：`org.springframework.boot.actuate.autoconfigure.metrics.cache.CacheMetricsRegistrarConfiguration`

核心方法：

```java
private final CacheMetricsRegistrar cacheMetricsRegistrar;

private void bindCachesToRegistry() {
  this.cacheManagers.forEach(this::bindCacheManagerToRegistry);
}

private void bindCacheManagerToRegistry(String beanName, CacheManager cacheManager) {
  cacheManager.getCacheNames()
    .forEach((cacheName) -> bindCacheToRegistry(beanName, cacheManager.getCache(cacheName)));
}

private void bindCacheToRegistry(String beanName, Cache cache) {
  Tag cacheManagerTag = Tag.of("cacheManager", getCacheManagerName(beanName));
  // 此处进行Cache绑定到MetricsRegistrar
  this.cacheMetricsRegistrar.bindCacheToRegistry(cache, cacheManagerTag);
}
```

MetricsRegistrar：`org.springframework.boot.actuate.metrics.cache.CacheMetricsRegistrar`

核心方法：

```java
/**
 * Attempt to bind the specified {@link Cache} to the registry. Return {@code true} if
 * the cache is supported and was bound to the registry, {@code false} otherwise.
 * @param cache the cache to handle
 * @param tags the tags to associate with the metrics of that cache
 * @return {@code true} if the {@code cache} is supported and was registered
 */
public boolean bindCacheToRegistry(Cache cache, Tag... tags) {
  // 此处完成Cache绑定到Registry的功能，即 [Micrometer]（https://micrometer.io/）的Api使用方式
  MeterBinder meterBinder = getMeterBinder(unwrapIfNecessary(cache), Tags.of(tags));
  if (meterBinder != null) {
    meterBinder.bindTo(this.registry);
    return true;
  }
  return false;
}

@SuppressWarnings({ "unchecked" })
private MeterBinder getMeterBinder(Cache cache, Tags tags) {
  // 自定义Tags
  Tags cacheTags = tags.and(getAdditionalTags(cache));
  // 调用binderProviders#getMeterBinder获取MeterBinder
  return LambdaSafe.callbacks(CacheMeterBinderProvider.class, this.binderProviders, cache)
    .withLogger(CacheMetricsRegistrar.class)
    .invokeAnd((binderProvider) -> binderProvider.getMeterBinder(cache, cacheTags)).filter(Objects::nonNull)
    .findFirst().orElse(null);
}

/**
 * Return additional {@link Tag tags} to be associated with the given {@link Cache}.
 * @param cache the cache
 * @return a list of additional tags to associate to that {@code cache}.
 */
protected Iterable<Tag> getAdditionalTags(Cache cache) {
  // 自定义标签
  return Tags.of("name", cache.getName());
}

private Cache unwrapIfNecessary(Cache cache) {
  // 针对事务感知缓存装饰器做处理
  if (ClassUtils.isPresent("org.springframework.cache.transaction.TransactionAwareCacheDecorator",
                           getClass().getClassLoader())) {
    return TransactionAwareCacheDecoratorHandler.unwrapIfNecessary(cache);
  }
  return cache;
}

private static class TransactionAwareCacheDecoratorHandler {

  private static Cache unwrapIfNecessary(Cache cache) {
    try {
      if (cache instanceof TransactionAwareCacheDecorator) {
        // 事务感知缓存装饰器，主要将Cache包了一层
        return ((TransactionAwareCacheDecorator) cache).getTargetCache();
      }
    }
    catch (NoClassDefFoundError ex) {
      // Ignore
    }
    return cache;
  }

}
```

CacheMeterBinderProvider：`org.springframework.boot.actuate.metrics.cache.CaffeineCacheMeterBinderProvider`，实现了接口`org.springframework.boot.actuate.metrics.cache.CacheMeterBinderProvider`，下方为两者的类图：



![CaffeineCacheMeterBinderProvider](Caffeine.assets/CaffeineCacheMeterBinderProvider.png)

核心方法：

```java
// CaffeineCacheMeterBinderProvider.class
@Override
public MeterBinder getMeterBinder(CaffeineCache cache, Iterable<Tag> tags) {
  // 这里直接依赖Micrometer中的Metrics实现
  return new CaffeineCacheMetrics(cache.getNativeCache(), cache.getName(), tags);
}
```

#### Micrometer实现

版本：

```xml
<!-- micrometer -->
<dependency>
  <groupId>io.micrometer</groupId>
  <artifactId>micrometer-registry-prometheus</artifactId>
  <version>1.7.3</version>
</dependency>
```

MeterBinder：`io.micrometer.core.instrument.binder.cache.CaffeineCacheMetrics`，继承`io.micrometer.core.instrument.binder.cache.CacheMeterBinder`，下方为两者的类图：



![CaffeineCacheMetrics&CacheMeterBinder](Caffeine.assets/CaffeineCacheMetrics.png)



核心方法：

```java
// CacheMeterBinder.class
public final void bindTo(MeterRegistry registry) {
  // 显而易见，这里是绑定各种指标
  if (this.size() != null) {
    Gauge.builder("cache.size", this.cache.get(), (c) -> {
      Long size = this.size();
      return size == null ? 0.0D : (double)size;
    }).tags(this.tags).description("The number of entries in this cache. This may be an approximation, depending on the type of cache.").register(registry);
  }

  if (this.missCount() != null) {
    FunctionCounter.builder("cache.gets", this.cache.get(), (c) -> {
      Long misses = this.missCount();
      return misses == null ? 0.0D : (double)misses;
    }).tags(this.tags).tag("result", "miss").description("the number of times cache lookup methods have returned an uncached (newly loaded) value, or null").register(registry);
  }

  FunctionCounter.builder("cache.gets", this.cache.get(), (c) -> {
    return (double)this.hitCount();
  }).tags(this.tags).tag("result", "hit").description("The number of times cache lookup methods have returned a cached value.").register(registry);
  FunctionCounter.builder("cache.puts", this.cache.get(), (c) -> {
    return (double)this.putCount();
  }).tags(this.tags).description("The number of entries added to the cache").register(registry);
  if (this.evictionCount() != null) {
    FunctionCounter.builder("cache.evictions", this.cache.get(), (c) -> {
      Long evictions = this.evictionCount();
      return evictions == null ? 0.0D : (double)evictions;
    }).tags(this.tags).description("cache evictions").register(registry);
  }

  this.bindImplementationSpecificMetrics(registry);
}

// CaffeineCacheMetrics.class
protected void bindImplementationSpecificMetrics(MeterRegistry registry) {
  // 这里是一些Caffeine提供的额外指标
  FunctionCounter.builder("cache.eviction.weight", this.cache, (c) -> {
    return (double)c.stats().evictionWeight();
  }).tags(this.getTagsWithCacheName()).description("The sum of weights of evicted entries. This total does not include manual invalidations.").register(registry);
  if (this.cache instanceof LoadingCache) {
    TimeGauge.builder("cache.load.duration", this.cache, TimeUnit.NANOSECONDS, (c) -> {
      return (double)c.stats().totalLoadTime();
    }).tags(this.getTagsWithCacheName()).description("The time the cache has spent loading new values").register(registry);
    FunctionCounter.builder("cache.load", this.cache, (c) -> {
      return (double)c.stats().loadSuccessCount();
    }).tags(this.getTagsWithCacheName()).tags(new String[]{"result", "success"}).description("The number of times cache lookup methods have successfully loaded a new value").register(registry);
    FunctionCounter.builder("cache.load", this.cache, (c) -> {
      return (double)c.stats().loadFailureCount();
    }).tags(this.getTagsWithCacheName()).tags(new String[]{"result", "failure"}).description("The number of times {@link Cache} lookup methods failed to load a new value, either because no value was found or an exception was thrown while loading").register(registry);
  }

}
```























