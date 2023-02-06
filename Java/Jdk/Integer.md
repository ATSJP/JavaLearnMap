# Integer

## IntegerCache

你知道IntegerCache吗，如果不知道，你知道下面的运行结果吗
```java
	@Test
	public void test() {
		System.out.println("Integer.valueOf(1).equals(Integer.valueOf(\"1\")) : "+ Integer.valueOf(1).equals(Integer.valueOf("1")));
		System.out.println("Integer.valueOf(1) == Integer.valueOf(\"1\") : "+ (Integer.valueOf(1) == Integer.valueOf("1")));
		System.out.println("Integer.valueOf(127) == Integer.valueOf(\"127\") : "+ (Integer.valueOf(127) == Integer.valueOf("127")));
		System.out.println("Integer.valueOf(-128) == Integer.valueOf(\"-128\") : "+(Integer.valueOf(-128) == Integer.valueOf("-128")));
		System.out.println("Integer.valueOf(128) == Integer.valueOf(\"128\") : "+(Integer.valueOf(128) == Integer.valueOf("128")));
		System.out.println("Integer.valueOf(-129) == Integer.valueOf(\"-129\") : "+(Integer.valueOf(-129) == Integer.valueOf("-129")));
	}
```

运行结果如下:

```text
Integer.valueOf(1).equals(Integer.valueOf("1")) : true
Integer.valueOf(1) == Integer.valueOf("1") : true
Integer.valueOf(127) == Integer.valueOf("127") : true
Integer.valueOf(-128) == Integer.valueOf("-128") : true
Integer.valueOf(128) == Integer.valueOf("128") : false
Integer.valueOf(-129) == Integer.valueOf("-129") : false
```

或许结果会出乎你的意外，这一切得益于IntegerCache的存在。
