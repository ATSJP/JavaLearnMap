## Fst

### 使用

#### 添加依赖

```xml
        <dependency>
            <groupId>de.ruedigermoeller</groupId>
            <artifactId>fst</artifactId>
            <version>2.56</version>
        </dependency>
```

#### 测试代码

```java
import java.io.Serializable;

/**
 * @author shijianpeng
 */
public class TestDto implements Serializable {
	// private static final long serialVersionUID = -6168677275842483524L;
	private static final long	serialVersionUID	= -7142123898645447283L;
	private String				name;
	private Integer				age;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	@Override
	public String toString() {
		return "TestDto{" + "name='" + name + '\'' + ", age=" + age + '}';
	}
}
```



```java
import org.apache.commons.codec.binary.Base64;
import org.junit.Test;
import org.nustaq.serialization.FSTConfiguration;

/**
 * @author shijianpeng
 */
public class FstSerialTest {

	private static final FSTConfiguration conf = FSTConfiguration.createDefaultConfiguration();

	@Test
	public void serializeTest() {
		TestDto testDto = new TestDto();
		testDto.setName("test");
		testDto.setAge(12);

		byte[] byteArray = conf.asByteArray(testDto);
		System.out.println(Base64.encodeBase64String(byteArray));
	}

	@Test
	public void deserializeTest() {
		String string = "AAEOc2VyaWFsLlRlc3REdG/3DPwEdGVzdP8A";
		FSTConfiguration conf = FSTConfiguration.createDefaultConfiguration();
		TestDto object = (TestDto) conf.asObject(Base64.decodeBase64(string));
		System.out.println(object);
	}

}
```

### 常用功能

**@Version**

当给一个Class增加属性的时候，必须使用@Version注解，否则反序列化会失败。@Version注解接受一个Value值，此Value值从0开始，默认是0，每次增加属性，Value必须增加1，例如：

```java
class MyClass implements Serializable {

     // fields on initial release 1.0
     int x;
     String y;

     // fields added with release 1.5
     @Version(1) String added;
     @Version(1) String alsoAdded;

     // fields added with release 2.0
     @Version(2) String addedv2;
     @Version(2) String alsoAddedv2;

}
```

注意：Value必须增加1，是指当前Class内，Value最大值+1























