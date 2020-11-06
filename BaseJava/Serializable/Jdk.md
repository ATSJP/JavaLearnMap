## JDKSerializable

### 使用

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
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author shijianpeng
 */
public class JdkSerialTest {

	private static Object deserialize(String filePath) {
		Object ob = null;
		ObjectInputStream inputStream = null;
		try {
			inputStream = new ObjectInputStream(new FileInputStream(filePath));
			try {
				ob = inputStream.readObject();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return ob;
	}

	private static void serialize(Object ob, String filePath) {
		ObjectOutputStream outputStream = null;
		try {
			outputStream = new ObjectOutputStream(new FileOutputStream(filePath));
			outputStream.writeObject(ob);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (outputStream != null) {
					outputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Test
	public void deserializeTest() {
		String filePath = "jdkSerial.txt";
		Object o = deserialize(filePath);
		System.out.println(o);
	}

	@Test
	public void serializeTest() {
		String filePath = "jdkSerial.txt";
		TestDto testDto = new TestDto();
		testDto.setName("test");
		testDto.setAge(12);
		serialize(testDto, filePath);
		System.out.println(testDto);
	}

}
```

### 常见使用

**增加减少属性**

增加属性和减少属性，只要在不修改serialVersionUID的前提下，反序列化正常。

**修改属性**

字段名不变，修改属性的类型，反序列化报错。

