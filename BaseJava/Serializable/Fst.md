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

