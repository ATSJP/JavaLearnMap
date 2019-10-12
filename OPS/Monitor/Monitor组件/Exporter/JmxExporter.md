Gthub:

JMX Exporter
=====

官方Github：https://github.com/prometheus/jmx_exporter

JMX to Prometheus exporter: a collector that can configurably scrape and
expose mBeans of a JMX target.

This exporter is intended to be run as a Java Agent, exposing a HTTP server
and serving metrics of the local JVM. It can be also run as an independent
HTTP server and scrape remote JMX targets, but this has various
disadvantages, such as being harder to configure and being unable to expose
process metrics (e.g., memory and CPU usage). Running the exporter as a Java
Agent is thus strongly encouraged.

## Running

To run as a javaagent [download the jar](https://repo1.maven.org/maven2/io/prometheus/jmx/jmx_prometheus_javaagent/0.12.0/jmx_prometheus_javaagent-0.12.0.jar) and run:

```shell
java -javaagent:./jmx_prometheus_javaagent-0.12.0.jar=8080:config.yaml -jar yourJar.jar
```
Metrics will now be accessible at http://localhost:8080/metrics

To bind the java agent to a specific IP change the port number to `host:port`.

See `./run_sample_httpserver.sh` for a sample script that runs the httpserver against itself.

## Configuration
The configuration is in YAML. An example with all possible options:
```yaml
---
startDelaySeconds: 0
hostPort: 127.0.0.1:1234
username: 
password: 
jmxUrl: service:jmx:rmi:///jndi/rmi://127.0.0.1:1234/jmxrmi
ssl: false
lowercaseOutputName: false
lowercaseOutputLabelNames: false
whitelistObjectNames: ["org.apache.cassandra.metrics:*"]
blacklistObjectNames: ["org.apache.cassandra.metrics:type=ColumnFamily,*"]
rules:
  - pattern: 'org.apache.cassandra.metrics<type=(\w+), name=(\w+)><>Value: (\d+)'
    name: cassandra_$1_$2
    value: $3
    valueFactor: 0.001
    labels: {}
    help: "Cassandra metric $1 $2"
    type: GAUGE
    attrNameSnakeCase: false
```
| Name                      | Description                                                  |
| ------------------------- | ------------------------------------------------------------ |
| startDelaySeconds         | 延迟响应请求，任何请求在延迟期间内，得到的仅仅是一个空集合。 |
| hostPort                  | 远程JVM端口                                                  |
| username                  | JMX认证用户名                                                |
| password                  | JMX认证密码                                                  |
| jmxUrl                    | JMX的地址，如果此处配置端口，则hostPort不应该在配置          |
| ssl                       | 是否启动SSL连接JMX。 为了配置证书，你应该设置以下系统参数          `-Djavax.net.ssl.keyStore=/home/user/.keystore`<br/>`-Djavax.net.ssl.keyStorePassword=changeit`<br/>`-Djavax.net.ssl.trustStore=/home/user/.truststore`<br/>`-Djavax.net.ssl.trustStorePassword=changeit` |
| lowercaseOutputName       | 配置输出的metric名称为小写，默认`false`                      |
| lowercaseOutputLabelNames | 配置输出的metric标签名称为小写，默认`false`                  |
| whitelistObjectNames      | Object白名单， 默认所有                                      |
| blacklistObjectNames      | Object黑名单，优先于白名单匹配， 默认`none`                  |
| rules                     | A list of rules to apply in order, processing stops at the first matching rule. Attributes that aren't matched aren't collected. If not specified, defaults to collecting everything in the default format. |
| pattern                   | Regex pattern to match against each bean attribute. The pattern is not anchored. Capture groups can be used in other options. Defaults to matching everything. |
| attrNameSnakeCase         | Converts the attribute name to snake case. This is seen in the names matched by the pattern and the default format. For example, anAttrName to an\_attr\_name. Defaults to false. |
| name                      | The metric name to set. Capture groups from the `pattern` can be used. If not specified, the default format will be used. If it evaluates to empty, processing of this attribute stops with no output. |
| value                     | Value for the metric. Static values and capture groups from the `pattern` can be used. If not specified the scraped mBean value will be used. |
| valueFactor               | Optional number that `value` (or the scraped mBean value if `value` is not specified) is multiplied by, mainly used to convert mBean values from milliseconds to seconds. |
| labels                    | A map of label name to label value pairs. Capture groups from `pattern` can be used in each. `name` must be set to use this. Empty names and values are ignored. If not specified and the default format is not being used, no labels are set. |
| help                      | Help text for the metric. Capture groups from `pattern` can be used. `name` must be set to use this. Defaults to the mBean attribute description and the full name of the attribute. |
| type                      | metric的类型, 可以是 `GAUGE`, `COUNTER` or `UNTYPED`。使用此参数必须设置`name` 。默认是`UNTYPED`. |

Metric names and label names are sanitized. All characters other than `[a-zA-Z0-9:_]` are replaced with underscores,
and adjacent underscores are collapsed. There's no limitations on label values or the help text.

A minimal config is `{}`, which will connect to the local JVM and collect everything in the default format.
Note that the scraper always processes all mBeans, even if they're not exported.

Example configurations for javaagents can be found at  https://github.com/prometheus/jmx_exporter/tree/master/example_configs

### Pattern input
The format of the input matches against the pattern is
```
domain<beanpropertyName1=beanPropertyValue1, beanpropertyName2=beanPropertyValue2, ...><key1, key2, ...>attrName: value
```

| Part                  | Description                                                  |
| --------------------- | ------------------------------------------------------------ |
| domain                | Bean name. This is the part before the colon in the JMX object name. |
| beanProperyName/Value | Bean properties. These are the key/values after the colon in the JMX object name. |
| keyN                  | If composite or tabular data is encountered, the name of the attribute is added to this list. |
| attrName              | The name of the attribute. For tabular data, this will be the name of the column. If `attrNameSnakeCase` is set, this will be converted to snake case. |
| value                 | The value of the attribute.                                  |

No escaping or other changes are made to these values, with the exception of if `attrNameSnakeCase` is set.
The default help includes this string, except for the value.

### Default format
The default format will transform beans in a way that should produce sane metrics in most cases. It is
```
domain_beanPropertyValue1_key1_key2_...keyN_attrName{beanpropertyName2="beanPropertyValue2", ...}: value
```
If a given part isn't set, it'll be excluded.

## Testing

`mvn test` to test.

## Debugging

You can start the jmx's scraper in standalone mode in order to debug what is called 

```
git clone https://github.com/prometheus/jmx_exporter.git
cd jmx_exporter
mvn package
java -cp collector/target/collector*.jar  io.prometheus.jmx.JmxScraper  service:jmx:rmi:your_url
```

To get finer logs (including the duration of each jmx call),
create a file called logging.properties with this content:

```
handlers=java.util.logging.ConsoleHandler
java.util.logging.ConsoleHandler.level=ALL
io.prometheus.jmx.level=ALL
io.prometheus.jmx.shaded.io.prometheus.jmx.level=ALL
```

Add the following flag to your Java invocation:

`-Djava.util.logging.config.file=/path/to/logging.properties`


## Installing

A Debian binary package is created as part of the build process and it can
be used to install an executable into `/usr/bin/jmx_exporter` with configuration
in `/etc/jmx_exporter/jmx_exporter.yaml`.