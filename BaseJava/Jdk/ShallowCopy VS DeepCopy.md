

[TOC]



# 浅拷贝和深拷贝

## 介绍

开发过程中，有时会遇到把现有的一个对象的所有成员属性拷贝给另一个对象的需求。

比如说对象 A 和对象 B，二者都是 ClassC 的对象，具有成员变量 a 和 b，现在对对象 A 进行拷贝赋值给 B，也就是 B.a = A.a; B.b = A.b；这时再去改变 B 的属性 a 或者 b 时，可能会遇到问题：

假设 a 是基础数据类型，b 是引用类型。
- 当改变 B.a 的值时，没有问题；
- 当改变 B.b 的值时，同时也会改变 A.b 的值，因为其实上面的例子中只是把 A.b 赋值给了 B.b，因为是 b 引用类型的，所以它们是指向同一个地址的，这可能就会给我们使用埋下隐患。

> Java 中的数据类型分为基本数据类型和引用数据类型。对于这两种数据类型，在进行赋值操作、用作方法参数或返回值时，会有值传递和引用（地址）传递的差别。

## 拷贝分类

根据对对象属性的拷贝程度（基本数据类和引用类型），会分为两种：

- 浅拷贝 (`Shallow Copy`)
- 深拷贝 (`Deep Copy`)

## 浅拷贝

### 介绍

浅拷贝是按位拷贝对象，它会创建一个新对象，这个对象有着原始对象属性值的一份精确拷贝。如果属性是基本类型，拷贝的就是基本类型的值；如果属性是内存地址（引用类型），拷贝的就是内存地址 ，因此如果其中一个对象改变了这个地址，就会影响到另一个对象。即默认拷贝构造函数只是对对象进行浅拷贝复制(逐个成员依次拷贝)，即只复制对象空间而不复制资源。

###  特点

- 对于基本数据类型的成员对象，因为基础数据类型是值传递的，所以是直接将属性值赋值给新的对象。基础类型的拷贝，其中一个对象修改该值，不会影响另外一个。
- 对于引用类型，比如数组或者类对象，因为引用类型是引用传递，所以浅拷贝只是把内存地址赋值给了成员变量，它们指向了同一内存空间。改变其中一个，会对另外一个也产生影响。

### 实现

实现对象拷贝的类，需要实现 `Cloneable` 接口，并覆写 `clone()` 方法。

示例如下：

```java
public class UserDTO implements Serializable, Cloneable {
	private static final long	serialVersionUID	= 4821018283907966769L;
	private Long				id;
	private String				name;
	private UserDTO				child;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public UserDTO getChild() {
		return child;
	}

	public void setChild(UserDTO child) {
		this.child = child;
	}

	// ShallowCopy
	@Override
	protected Object clone() throws CloneNotSupportedException {
	    return super.clone();
	}

	@Override
	public String toString() {
		return "UserDTO{" + "id=" + id + ", name='" + name + '\'' + ", child=" + child + '}';
	}
}
```

```csharp
	@Test
	public void testShallowCopy() throws CloneNotSupportedException {
		UserDTO son = new UserDTO();
		son.setId(1L);
		son.setName("son");

		UserDTO father = new UserDTO();
		father.setId(0L);
		father.setName("father");
		father.setChild(son);
		System.out.println(
				"father init:" + father.hashCode() + "," + father.getChild().hashCode() + "," + father.toString());

		UserDTO mother = (UserDTO) father.clone();
		mother.setName("mother");
		mother.getChild().setName("daughter");
		System.out
				.println("father:" + father.hashCode() + "," + father.getChild().hashCode() + "," + father.toString());
		System.out
				.println("mother:" + mother.hashCode() + "," + mother.getChild().hashCode() + "," + mother.toString());
	}
```

输出的结果：

```css
father init:991505714,385242642,UserDTO{id=0, name='father', child=UserDTO{id=1, name='son', child=null}}
father:991505714,385242642,UserDTO{id=0, name='father', child=UserDTO{id=1, name='daughter', child=null}}
mother:824009085,385242642,UserDTO{id=0, name='mother', child=UserDTO{id=1, name='daughter', child=null}}
```

由输出的结果可见，通过 `father.clone()` 拷贝对象后得到的 `mother`，`mother`和 `father`  是两个不同的对象。``mother`` 和 ``father`` 的基础数据类型的修改互不影响，而引用类型 `UserDTO` 因为是同一个对象，所以修改后是会有影响的。

**浅拷贝和对象拷贝的区别**：

```csharp
	@Test
	public void testObjectCopy() throws CloneNotSupportedException {
		UserDTO son = new UserDTO();
		son.setId(1L);
		son.setName("son");

		UserDTO father = new UserDTO();
		father.setId(0L);
		father.setName("father");
		father.setChild(son);
		System.out.println(
				"father init:" + father.hashCode() + "," + father.getChild().hashCode() + "," + father.toString());

		UserDTO mother = father;
		mother.setName("mother");
		mother.getChild().setName("daughter");
		System.out
				.println("father:" + father.hashCode() + "," + father.getChild().hashCode() + "," + father.toString());
		System.out
				.println("mother:" + mother.hashCode() + "," + mother.getChild().hashCode() + "," + mother.toString());
	}
```

这里把 `UserDTO mother = (UserDTO) father.clone();` 换成了 `UserDTO mother = father;`。
输出的结果：

```css
father init:991505714,385242642,UserDTO{id=0, name='father', child=UserDTO{id=1, name='son', child=null}}
father:991505714,385242642,UserDTO{id=0, name='mother', child=UserDTO{id=1, name='daughter', child=null}}
mother:991505714,385242642,UserDTO{id=0, name='mother', child=UserDTO{id=1, name='daughter', child=null}}
```

可见，**对象拷贝后没有生成新的对象，二者的对象地址是一样的；而浅拷贝的对象地址是不一样的**。

## 深拷贝

### 介绍

通过上面的例子可以看到，浅拷贝会带来数据安全方面的隐患，例如我们只是想修改了 `mother` 的 `UserDTO`，但是 `father` 的 `UserDTO` 也被修改了，因为它们都是指向的同一个地址。所以，此种情况下，我们需要用到深拷贝。

> 深拷贝，在拷贝引用类型成员变量时，为引用类型的数据成员另辟了一个独立的内存空间，实现真正内容上的拷贝。

### 特点

- 对于基本数据类型的成员对象，因为基础数据类型是值传递的，所以是直接将属性值赋值给新的对象。基础类型的拷贝，其中一个对象修改该值，不会影响另外一个（和浅拷贝一样）。
- 对于引用类型，比如数组或者类对象，深拷贝会新建一个对象空间，然后拷贝里面的内容，所以它们指向了不同的内存空间。改变其中一个，不会对另外一个也产生影响。
- 对于有多层对象的，每个对象都需要实现 `Cloneable` 并重写 `clone()` 方法，进而实现了对象的串行层层拷贝。
- 深拷贝相比于浅拷贝速度较慢并且花销较大。

### 实现

在 `UserDTO` 的 `clone()` 方法中，需要拿到拷贝自己后产生的新的对象，然后对新的对象的引用类型再调用拷贝操作，实现对引用类型成员变量的深拷贝。

```java
public class UserDTO implements Serializable, Cloneable {
	private static final long	serialVersionUID	= 4821018283907966769L;
	private Long				id;
	private String				name;
	private UserDTO				child;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public UserDTO getChild() {
		return child;
	}

	public void setChild(UserDTO child) {
		this.child = child;
	}

	// ShallowCopy
	// @Override
	// protected Object clone() throws CloneNotSupportedException {
	// return super.clone();
	// }

	// DeepCopy
	@Override
	protected Object clone() throws CloneNotSupportedException {
		UserDTO userDTO = (UserDTO) super.clone();
		// 每个引用对象都需要Clone
		if (this.child != null) {
			userDTO.child = (UserDTO) this.child.clone();
		}
		return userDTO;
	}

	@Override
	public String toString() {
		return "UserDTO{" + "id=" + id + ", name='" + name + '\'' + ", child=" + child + '}';
	}
}

```

一样的使用方式

```csharp
	@Test
	public void testDeepCopy() throws CloneNotSupportedException {
		UserDTO son = new UserDTO();
		son.setId(1L);
		son.setName("son");

		UserDTO father = new UserDTO();
		father.setId(0L);
		father.setName("father");
		father.setChild(son);
		System.out.println(
				"father init:" + father.hashCode() + "," + father.getChild().hashCode() + "," + father.toString());

		UserDTO mother = (UserDTO) father.clone();
		mother.setName("mother");
		mother.getChild().setName("daughter");
		System.out
				.println("father:" + father.hashCode() + "," + father.getChild().hashCode() + "," + father.toString());
		System.out
				.println("mother:" + mother.hashCode() + "," + mother.getChild().hashCode() + "," + mother.toString());
	}
```

输出结果：

```css
father init:991505714,385242642,UserDTO{id=0, name='father', child=UserDTO{id=1, name='son', child=null}}
father:991505714,385242642,UserDTO{id=0, name='father', child=UserDTO{id=1, name='son', child=null}}
mother:824009085,2085857771,UserDTO{id=0, name='mother', child=UserDTO{id=1, name='daughter', child=null}}
```

由输出结果可见，深拷贝后，不管是基础数据类型还是引用类型的成员变量，修改其值都不会相互造成影响。

## 举一反三

日常学习工作中，我们经常使用到各种拷贝数据的Utils，下面的测试代码，列出了一些，让我们一起猜一猜运行结果？

```java
public class UserDTO implements Serializable {
	private static final long	serialVersionUID	= 4821018283907966769L;
	private Long				id;
	private String				name;
	private UserDTO				child;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public UserDTO getChild() {
		return child;
	}

	public void setChild(UserDTO child) {
		this.child = child;
	}

	@Override
	public String toString() {
		return "UserDTO{" + "id=" + id + ", name='" + name + '\'' + ", child=" + child + '}';
	}
}
```

```java
	@Test
	public void beanCopy() throws InvocationTargetException, IllegalAccessException {
		UserDTO child = new UserDTO();
		child.setId(1L);
		child.setName("child");

		UserDTO father = new UserDTO();
		father.setId(0L);
		father.setName("father");
		father.setChild(child);
		System.out.println(
				"father init:" + father.hashCode() + "," + father.getChild().hashCode() + "," + father.toString());
		// apache
		UserDTO target = new UserDTO();
		org.apache.commons.beanutils.BeanUtils.copyProperties(target, father);
		System.out.println(
				"apache copy:" + target.hashCode() + "," + target.getChild().hashCode() + "," + target.toString());
		// spring
		UserDTO target2 = new UserDTO();
		org.springframework.beans.BeanUtils.copyProperties(father, target2);
		System.out.println(
				"spring copy:" + target2.hashCode() + "," + target2.getChild().hashCode() + "," + target2.toString());
		// orika
		MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
		MapperFacade mapper = mapperFactory.getMapperFacade();
		UserDTO target3 = new UserDTO();
		mapper.map(father, target3);
		System.out.println(
				"orika copy:" + target3.hashCode() + "," + target3.getChild().hashCode() + "," + target3.toString());

		// change
		child.setName("childChange");
		System.out.println(
				"apache copy:" + target.hashCode() + "," + target.getChild().hashCode() + "," + target.toString());
		System.out.println(
				"spring copy:" + target2.hashCode() + "," + target2.getChild().hashCode() + "," + target2.toString());
		System.out.println(
				"orika copy:" + target3.hashCode() + "," + target3.getChild().hashCode() + "," + target3.toString());
	}
```

看完测试代码后，我们提出以下几个问题：

- child的引用地址会变化吗
- 各个工具实现的是浅拷贝、深拷贝？

你能猜出结果吗？

输出：

```css
father init:991505714,385242642,UserDTO{id=0, name='father', child=UserDTO{id=1, name='child', child=null}}
apache copy:866191240,385242642,UserDTO{id=0, name='father', child=UserDTO{id=1, name='child', child=null}}
spring copy:610984013,385242642,UserDTO{id=0, name='father', child=UserDTO{id=1, name='child', child=null}}
orika copy:815674463,1453774246,UserDTO{id=0, name='father', child=UserDTO{id=1, name='child', child=null}}
apache Change:866191240,385242642,UserDTO{id=0, name='father', child=UserDTO{id=1, name='childChange', child=null}}
spring Change:610984013,385242642,UserDTO{id=0, name='father', child=UserDTO{id=1, name='childChange', child=null}}
orika Change:815674463,1453774246,UserDTO{id=0, name='father', child=UserDTO{id=1, name='child', child=null}}
```

Tips：关于各大工具的实现原理，我们将在以后的文章中，一探究竟。