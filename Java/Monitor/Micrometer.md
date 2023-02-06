## Micrometer

### 一、前言

> Micrometer 为 Java 平台上的性能数据收集提供了一个通用的 API，它提供了多种度量指标类型（Timers、Guauges、Counters等），同时支持接入不同的监控系统，例如 Influxdb、Graphite、Prometheus 等。我们可以通过 Micrometer 收集 Java 性能数据，配合 Prometheus 监控系统实时获取数据，并最终在 Grafana 上展示出来，从而很容易实现应用的监控。

Micrometer 中有两个最核心的概念，分别是计量器（Meter）和计量器注册表（MeterRegistry）。计量器用来收集不同类型的性能指标信息，Micrometer 提供了如下几种不同类型的计量器：

- 计数器（Counter）: 表示收集的数据是按照某个趋势（增加／减少）一直变化的，也是最常用的一种计量器，例如接口请求总数、请求错误总数、队列数量变化等。
- 计量仪（Gauge）: 表示搜集的瞬时的数据，可以任意变化的，例如常用的 CPU Load、Mem 使用量、Network 使用量、实时在线人数统计等，
- 计时器（Timer）: 用来记录事件的持续时间，这个用的比较少。
- 分布概要（Distribution summary）: 用来记录事件的分布情况，表示一段时间范围内对数据进行采样，可以用于统计网络请求平均延迟、请求延迟占比等。

### 二、自定义指标

如果要实现自定义的指标收集，官方提供了接口，供开发者实现。

```java
/**
 * Binders register one or more metrics to provide information about the state
 * of some aspect of the application or its container.
 * <p>
 * Binders are enabled by default if they source data for an alert
 * that is recommended for a production ready app.
 */
public interface MeterBinder {
    void bindTo(@NonNull MeterRegistry registry);
}
```

接下来，我们实现几个小例子。

#### Druid指标监控

##### 以SpringBoot为例

###### 引入jiar包

Tips：Druid及数据库相关配置，不在此篇涉及。

```xml
        <spring-boot.version>2.1.1.RELEASE</spring-boot.version>
        <micrometer.version>1.3.0</micrometer.version>
		
        ...

        <!-- 外部依赖 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>
        <!-- 健康监控 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
		<!-- micrometer -->
        <dependency>
             <groupId>io.micrometer</groupId>
             <artifactId>micrometer-registry-prometheus</artifactId>
        </dependency>
```

###### 实现MeterBinder

```java
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import com.alibaba.druid.pool.DruidDataSource;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.lang.NonNull;

/**
 * Druid Metric
 */
public class DruidDataSourceMetric implements MeterBinder {

	private DruidDataSource druidDataSource;

	public DruidDataSourceMetric(DruidDataSource druidDataSource) {
		this.druidDataSource = druidDataSource;
	}

	@Override
	public void bindTo(@NonNull MeterRegistry meterRegistry) {
		Gauge.builder("jdbc.connections.active", druidDataSource::getActiveCount).register(meterRegistry);
		Gauge.builder("jdbc.connections.max", druidDataSource::getMaxActive).register(meterRegistry);
		Gauge.builder("jdbc.connections.min", druidDataSource::getMinIdle).register(meterRegistry);
		Gauge.builder("jdbc.druid.initialSize", druidDataSource::getInitialSize).register(meterRegistry);
		Gauge.builder("jdbc.druid.maxActive", druidDataSource::getMaxActive).register(meterRegistry);
		Gauge.builder("jdbc.druid.minIdle", druidDataSource::getMinIdle).register(meterRegistry);
		Gauge.builder("jdbc.druid.maxIdle", druidDataSource::getMaxIdle).register(meterRegistry);
		Gauge.builder("jdbc.druid.maxWait", druidDataSource::getMaxWait).register(meterRegistry);
		Gauge.builder("jdbc.druid.notFullTimeoutRetry", druidDataSource::getNotFullTimeoutRetryCount)
				.register(meterRegistry);
		Gauge.builder("jdbc.druid.execute", druidDataSource::getExecuteCount).register(meterRegistry);
		Gauge.builder("jdbc.druid.executeQuery", druidDataSource::getExecuteQueryCount).register(meterRegistry);
		Gauge.builder("jdbc.druid.executeUpdate", druidDataSource::getExecuteUpdateCount).register(meterRegistry);
		Gauge.builder("jdbc.druid.executeBatch", druidDataSource::getExecuteBatchCount).register(meterRegistry);
		Gauge.builder("jdbc.druid.create", druidDataSource::getCreateCount).register(meterRegistry);
		Gauge.builder("jdbc.druid.destroy", druidDataSource::getDestroyCount).register(meterRegistry);
		Gauge.builder("jdbc.druid.pooling", druidDataSource::getPoolingCount).register(meterRegistry);
		Gauge.builder("jdbc.druid.active", druidDataSource::getActiveCount).register(meterRegistry);
		Gauge.builder("jdbc.druid.discard", druidDataSource::getDiscardCount).register(meterRegistry);
		Gauge.builder("jdbc.druid.notEmptyWaitThread", druidDataSource::getNotEmptyWaitThreadCount)
				.register(meterRegistry);
		Gauge.builder("jdbc.druid.notEmptyWaitThreadPeak", druidDataSource::getNotEmptyWaitThreadPeak)
				.register(meterRegistry);
		Gauge.builder("jdbc.druid.recycleError", druidDataSource::getRecycleErrorCount).register(meterRegistry);
		Gauge.builder("jdbc.druid.connect", druidDataSource::getConnectCount).register(meterRegistry);
		Gauge.builder("jdbc.druid.close", druidDataSource::getCloseCount).register(meterRegistry);
		Gauge.builder("jdbc.druid.connectError", druidDataSource::getConnectErrorCount).register(meterRegistry);
		Gauge.builder("jdbc.druid.recycle", druidDataSource::getRecycleCount).register(meterRegistry);
		Gauge.builder("jdbc.druid.removeAbandoned", druidDataSource::getRemoveAbandonedCount).register(meterRegistry);
		Gauge.builder("jdbc.druid.notEmptyWait", druidDataSource::getNotEmptyWaitCount).register(meterRegistry);
		Gauge.builder("jdbc.druid.notEmptySignal", druidDataSource::getNotEmptySignalCount).register(meterRegistry);
		Gauge.builder("jdbc.druid.notEmptyWait", druidDataSource::getNotEmptyWaitNanos).baseUnit("nanos")
				.register(meterRegistry);
		Gauge.builder("jdbc.druid.activePeak", druidDataSource::getActivePeak).register(meterRegistry);
		Gauge.builder("jdbc.druid.poolingPeak", druidDataSource::getPoolingPeak).register(meterRegistry);
	}

}
```

###### 配置类

```java
import com.alibaba.druid.pool.DruidDataSource;
import io.github.mweirauch.micrometer.jvm.extras.ProcessMemoryMetrics;
import io.github.mweirauch.micrometer.jvm.extras.ProcessThreadMetrics;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * Metric
 */
@Configuration
public class MetricConfig {

    /**
     * druidDataSourceMetric
     *
     * @param dataSource 数据源
     * @return MeterBinder 指标绑定
     */
    @Bean
    public MeterBinder druidDataSourceMetric(DataSource dataSource) {
        return new DruidDataSourceMetric((DruidDataSource) dataSource);
    }

}

```

###### Yml

```yml
# actuator 监控
management:
    endpoints:
        web:
            basePath: /actuator
            # 监控请求
            exposure:
                include: *
    server:
        port: 8080
```

启动应用，访问http://localhost:8080/actuator/prometheus，你将会看到：

```
# HELP jdbc_druid_notEmptyWait  
# TYPE jdbc_druid_notEmptyWait gauge
jdbc_druid_notEmptyWait{application="apply",} 0.0
# HELP tomcat_threads_config_max_threads  
# TYPE tomcat_threads_config_max_threads gauge
tomcat_threads_config_max_threads{application="apply",name="http-nio-8082",} NaN
# HELP jvm_gc_memory_promoted_bytes_total Count of positive increases in the size of the old generation memory pool before GC to after GC
# TYPE jvm_gc_memory_promoted_bytes_total counter
jvm_gc_memory_promoted_bytes_total{application="apply",} 8798368.0
# HELP tomcat_sessions_active_current_sessions  
# TYPE tomcat_sessions_active_current_sessions gauge
tomcat_sessions_active_current_sessions{application="apply",} 0.0
# HELP jdbc_druid_notEmptyWaitThreadPeak  
# TYPE jdbc_druid_notEmptyWaitThreadPeak gauge
jdbc_druid_notEmptyWaitThreadPeak{application="apply",} 0.0
# HELP tomcat_global_error_total  
# TYPE tomcat_global_error_total counter
tomcat_global_error_total{application="apply",name="http-nio-8082",} 0.0
# HELP jvm_gc_pause_seconds Time spent in GC pause
# TYPE jvm_gc_pause_seconds summary
jvm_gc_pause_seconds_count{action="end of minor GC",application="apply",cause="Allocation Failure",} 4.0
jvm_gc_pause_seconds_sum{action="end of minor GC",application="apply",cause="Allocation Failure",} 0.049
# HELP jvm_gc_pause_seconds_max Time spent in GC pause
# TYPE jvm_gc_pause_seconds_max gauge
jvm_gc_pause_seconds_max{action="end of minor GC",application="apply",cause="Allocation Failure",} 0.017
# HELP jvm_classes_unloaded_classes_total The total number of classes unloaded since the Java virtual machine has started execution
# TYPE jvm_classes_unloaded_classes_total counter
jvm_classes_unloaded_classes_total{application="apply",} 0.0
# HELP jdbc_druid_activePeak  
# TYPE jdbc_druid_activePeak gauge
jdbc_druid_activePeak{application="apply",} 1.0
# HELP jdbc_druid_active  
# TYPE jdbc_druid_active gauge
jdbc_druid_active{application="apply",} 0.0
# HELP process_memory_rss_bytes  
# TYPE process_memory_rss_bytes gauge
process_memory_rss_bytes{application="apply",} -1.0
# HELP tomcat_cache_access_total  
# TYPE tomcat_cache_access_total counter
tomcat_cache_access_total{application="apply",} 0.0
# HELP tomcat_sessions_active_max_sessions  
# TYPE tomcat_sessions_active_max_sessions gauge
tomcat_sessions_active_max_sessions{application="apply",} 0.0
# HELP jdbc_druid_notEmptyWaitThread  
# TYPE jdbc_druid_notEmptyWaitThread gauge
jdbc_druid_notEmptyWaitThread{application="apply",} 0.0
# HELP jdbc_druid_pooling  
# TYPE jdbc_druid_pooling gauge
jdbc_druid_pooling{application="apply",} 16.0
# HELP jdbc_druid_recycle  
# TYPE jdbc_druid_recycle gauge
jdbc_druid_recycle{application="apply",} 5.0
# HELP jdbc_druid_execute  
# TYPE jdbc_druid_execute gauge
jdbc_druid_execute{application="apply",} 1.0
# HELP tomcat_global_received_bytes_total  
# TYPE tomcat_global_received_bytes_total counter
tomcat_global_received_bytes_total{application="apply",name="http-nio-8082",} 0.0
# HELP jdbc_connections_active  
# TYPE jdbc_connections_active gauge
jdbc_connections_active{application="apply",} 0.0
# HELP jvm_memory_committed_bytes The amount of memory in bytes that is committed for the Java virtual machine to use
# TYPE jvm_memory_committed_bytes gauge
jvm_memory_committed_bytes{application="apply",area="nonheap",id="Compressed Class Space",} 1.245184E7
jvm_memory_committed_bytes{application="apply",area="heap",id="PS Eden Space",} 1.3369344E8
jvm_memory_committed_bytes{application="apply",area="nonheap",id="Code Cache",} 1.4548992E7
jvm_memory_committed_bytes{application="apply",area="heap",id="PS Old Gen",} 3.58088704E8
jvm_memory_committed_bytes{application="apply",area="nonheap",id="Metaspace",} 9.0005504E7
jvm_memory_committed_bytes{application="apply",area="heap",id="PS Survivor Space",} 2.1495808E7
# HELP jdbc_druid_executeUpdate  
# TYPE jdbc_druid_executeUpdate gauge
jdbc_druid_executeUpdate{application="apply",} 0.0
# HELP tomcat_sessions_rejected_sessions_total  
# TYPE tomcat_sessions_rejected_sessions_total counter
tomcat_sessions_rejected_sessions_total{application="apply",} 0.0
# HELP jdbc_druid_connectError  
# TYPE jdbc_druid_connectError gauge
jdbc_druid_connectError{application="apply",} 0.0
# HELP process_threads The number of process threads
# TYPE process_threads gauge
process_threads{application="apply",} -1.0
# HELP jvm_gc_live_data_size_bytes Size of old generation memory pool after a full GC
# TYPE jvm_gc_live_data_size_bytes gauge
jvm_gc_live_data_size_bytes{application="apply",} 0.0
# HELP tomcat_sessions_expired_sessions_total  
# TYPE tomcat_sessions_expired_sessions_total counter
tomcat_sessions_expired_sessions_total{application="apply",} 0.0
# HELP tomcat_sessions_created_sessions_total  
# TYPE tomcat_sessions_created_sessions_total counter
tomcat_sessions_created_sessions_total{application="apply",} 0.0
# HELP jdbc_druid_create  
# TYPE jdbc_druid_create gauge
jdbc_druid_create{application="apply",} 16.0
# HELP jdbc_druid_removeAbandoned  
# TYPE jdbc_druid_removeAbandoned gauge
jdbc_druid_removeAbandoned{application="apply",} 0.0
# HELP tomcat_cache_hit_total  
# TYPE tomcat_cache_hit_total counter
tomcat_cache_hit_total{application="apply",} 0.0
# HELP jdbc_druid_initialSize  
# TYPE jdbc_druid_initialSize gauge
jdbc_druid_initialSize{application="apply",} 16.0
# HELP jdbc_druid_maxWait  
# TYPE jdbc_druid_maxWait gauge
jdbc_druid_maxWait{application="apply",} -1.0
# HELP jdbc_druid_notFullTimeoutRetry  
# TYPE jdbc_druid_notFullTimeoutRetry gauge
jdbc_druid_notFullTimeoutRetry{application="apply",} 0.0
# HELP jdbc_druid_destroy  
# TYPE jdbc_druid_destroy gauge
jdbc_druid_destroy{application="apply",} 0.0
# HELP jvm_buffer_total_capacity_bytes An estimate of the total capacity of the buffers in this pool
# TYPE jvm_buffer_total_capacity_bytes gauge
jvm_buffer_total_capacity_bytes{application="apply",id="mapped",} 0.0
jvm_buffer_total_capacity_bytes{application="apply",id="direct",} 16384.0
# HELP jvm_threads_live_threads The current number of live threads including both daemon and non-daemon threads
# TYPE jvm_threads_live_threads gauge
jvm_threads_live_threads{application="apply",} 67.0
# HELP jvm_classes_loaded_classes The number of classes that are currently loaded in the Java virtual machine
# TYPE jvm_classes_loaded_classes gauge
jvm_classes_loaded_classes{application="apply",} 16954.0
# HELP process_memory_vss_bytes  
# TYPE process_memory_vss_bytes gauge
process_memory_vss_bytes{application="apply",} -1.0
# HELP jdbc_druid_executeQuery  
# TYPE jdbc_druid_executeQuery gauge
jdbc_druid_executeQuery{application="apply",} 1.0
# HELP jdbc_druid_minIdle  
# TYPE jdbc_druid_minIdle gauge
jdbc_druid_minIdle{application="apply",} 16.0
# HELP jvm_gc_max_data_size_bytes Max size of old generation memory pool
# TYPE jvm_gc_max_data_size_bytes gauge
jvm_gc_max_data_size_bytes{application="apply",} 0.0
# HELP jdbc_druid_executeBatch  
# TYPE jdbc_druid_executeBatch gauge
jdbc_druid_executeBatch{application="apply",} 0.0
# HELP jdbc_connections_min  
# TYPE jdbc_connections_min gauge
jdbc_connections_min{application="apply",} 16.0
# HELP process_cpu_usage The "recent cpu usage" for the Java Virtual Machine process
# TYPE process_cpu_usage gauge
process_cpu_usage{application="apply",} 0.3078967105263158
# HELP jdbc_druid_discard  
# TYPE jdbc_druid_discard gauge
jdbc_druid_discard{application="apply",} 0.0
# HELP jdbc_druid_recycleError  
# TYPE jdbc_druid_recycleError gauge
jdbc_druid_recycleError{application="apply",} 0.0
# HELP jdbc_druid_maxActive  
# TYPE jdbc_druid_maxActive gauge
jdbc_druid_maxActive{application="apply",} 32.0
# HELP process_memory_swap_bytes  
# TYPE process_memory_swap_bytes gauge
process_memory_swap_bytes{application="apply",} -1.0
# HELP jdbc_druid_notEmptySignal  
# TYPE jdbc_druid_notEmptySignal gauge
jdbc_druid_notEmptySignal{application="apply",} 5.0
# HELP jdbc_connections_max  
# TYPE jdbc_connections_max gauge
jdbc_connections_max{application="apply",} 32.0
# HELP process_start_time_seconds Start time of the process since unix epoch.
# TYPE process_start_time_seconds gauge
process_start_time_seconds{application="apply",} 1.617784398844E9
# HELP jvm_gc_memory_allocated_bytes_total Incremented for an increase in the size of the young generation memory pool after one GC to before the next
# TYPE jvm_gc_memory_allocated_bytes_total counter
jvm_gc_memory_allocated_bytes_total{application="apply",} 5.57842432E8
# HELP system_cpu_usage The "recent cpu usage" for the whole system
# TYPE system_cpu_usage gauge
system_cpu_usage{application="apply",} 0.4620655172413793
# HELP jvm_memory_used_bytes The amount of used memory
# TYPE jvm_memory_used_bytes gauge
jvm_memory_used_bytes{application="apply",area="nonheap",id="Compressed Class Space",} 1.1830864E7
jvm_memory_used_bytes{application="apply",area="heap",id="PS Eden Space",} 1.29743128E8
jvm_memory_used_bytes{application="apply",area="nonheap",id="Code Cache",} 1.4553152E7
jvm_memory_used_bytes{application="apply",area="heap",id="PS Old Gen",} 4.2182216E7
jvm_memory_used_bytes{application="apply",area="nonheap",id="Metaspace",} 8.65558E7
jvm_memory_used_bytes{application="apply",area="heap",id="PS Survivor Space",} 1.8797528E7
# HELP jvm_threads_daemon_threads The current number of live daemon threads
# TYPE jvm_threads_daemon_threads gauge
jvm_threads_daemon_threads{application="apply",} 54.0
# HELP tomcat_global_request_max_seconds  
# TYPE tomcat_global_request_max_seconds gauge
tomcat_global_request_max_seconds{application="apply",name="http-nio-8082",} 0.0
# HELP logback_events_total Number of error level events that made it to the logs
# TYPE logback_events_total counter
logback_events_total{application="apply",level="error",} 0.0
logback_events_total{application="apply",level="debug",} 0.0
logback_events_total{application="apply",level="trace",} 0.0
logback_events_total{application="apply",level="warn",} 3.0
logback_events_total{application="apply",level="info",} 49.0
# HELP jvm_memory_max_bytes The maximum amount of memory in bytes that can be used for memory management
# TYPE jvm_memory_max_bytes gauge
jvm_memory_max_bytes{application="apply",area="nonheap",id="Compressed Class Space",} 1.073741824E9
jvm_memory_max_bytes{application="apply",area="heap",id="PS Eden Space",} 1.34742016E8
jvm_memory_max_bytes{application="apply",area="nonheap",id="Code Cache",} 2.5165824E8
jvm_memory_max_bytes{application="apply",area="heap",id="PS Old Gen",} 3.58088704E8
jvm_memory_max_bytes{application="apply",area="nonheap",id="Metaspace",} -1.0
jvm_memory_max_bytes{application="apply",area="heap",id="PS Survivor Space",} 2.1495808E7
# HELP tomcat_sessions_alive_max_seconds  
# TYPE tomcat_sessions_alive_max_seconds gauge
tomcat_sessions_alive_max_seconds{application="apply",} 0.0
# HELP jvm_threads_peak_threads The peak live thread count since the Java virtual machine started or peak was reset
# TYPE jvm_threads_peak_threads gauge
jvm_threads_peak_threads{application="apply",} 69.0
# HELP jdbc_druid_connect  
# TYPE jdbc_druid_connect gauge
jdbc_druid_connect{application="apply",} 6.0
# HELP process_uptime_seconds The uptime of the Java virtual machine
# TYPE process_uptime_seconds gauge
process_uptime_seconds{application="apply",} 41.313
# HELP tomcat_servlet_request_seconds  
# TYPE tomcat_servlet_request_seconds summary
tomcat_servlet_request_seconds_count{application="apply",name="dispatcherServlet",} 0.0
tomcat_servlet_request_seconds_sum{application="apply",name="dispatcherServlet",} 0.0
tomcat_servlet_request_seconds_count{application="apply",name="default",} 0.0
tomcat_servlet_request_seconds_sum{application="apply",name="default",} 0.0
# HELP tomcat_global_request_seconds  
# TYPE tomcat_global_request_seconds summary
tomcat_global_request_seconds_count{application="apply",name="http-nio-8082",} 0.0
tomcat_global_request_seconds_sum{application="apply",name="http-nio-8082",} 0.0
# HELP jvm_buffer_count_buffers An estimate of the number of buffers in the pool
# TYPE jvm_buffer_count_buffers gauge
jvm_buffer_count_buffers{application="apply",id="mapped",} 0.0
jvm_buffer_count_buffers{application="apply",id="direct",} 3.0
# HELP tomcat_threads_current_threads  
# TYPE tomcat_threads_current_threads gauge
tomcat_threads_current_threads{application="apply",name="http-nio-8082",} NaN
# HELP system_cpu_count The number of processors available to the Java virtual machine
# TYPE system_cpu_count gauge
system_cpu_count{application="apply",} 4.0
# HELP jvm_buffer_memory_used_bytes An estimate of the memory that the Java virtual machine is using for this buffer pool
# TYPE jvm_buffer_memory_used_bytes gauge
jvm_buffer_memory_used_bytes{application="apply",id="mapped",} 0.0
jvm_buffer_memory_used_bytes{application="apply",id="direct",} 16385.0
# HELP tomcat_servlet_request_max_seconds  
# TYPE tomcat_servlet_request_max_seconds gauge
tomcat_servlet_request_max_seconds{application="apply",name="dispatcherServlet",} 0.0
tomcat_servlet_request_max_seconds{application="apply",name="default",} 0.0
# HELP jdbc_druid_close  
# TYPE jdbc_druid_close gauge
jdbc_druid_close{application="apply",} 5.0
# HELP jvm_threads_states_threads The current number of threads having NEW state
# TYPE jvm_threads_states_threads gauge
jvm_threads_states_threads{application="apply",state="blocked",} 0.0
jvm_threads_states_threads{application="apply",state="runnable",} 28.0
jvm_threads_states_threads{application="apply",state="new",} 0.0
jvm_threads_states_threads{application="apply",state="terminated",} 0.0
jvm_threads_states_threads{application="apply",state="waiting",} 24.0
jvm_threads_states_threads{application="apply",state="timed-waiting",} 15.0
# HELP jdbc_druid_maxIdle  
# TYPE jdbc_druid_maxIdle gauge
jdbc_druid_maxIdle{application="apply",} 8.0
# HELP jdbc_druid_poolingPeak  
# TYPE jdbc_druid_poolingPeak gauge
jdbc_druid_poolingPeak{application="apply",} 16.0
# HELP tomcat_global_sent_bytes_total  
# TYPE tomcat_global_sent_bytes_total counter
tomcat_global_sent_bytes_total{application="apply",name="http-nio-8082",} 0.0
# HELP tomcat_servlet_error_total  
# TYPE tomcat_servlet_error_total counter
tomcat_servlet_error_total{application="apply",name="dispatcherServlet",} 0.0
tomcat_servlet_error_total{application="apply",name="default",} 0.0
# HELP tomcat_threads_busy_threads  
# TYPE tomcat_threads_busy_threads gauge
tomcat_threads_busy_threads{application="apply",name="http-nio-8082",} NaN
```

到此，已经看到我们自定义的指标了。



