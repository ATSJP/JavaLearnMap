[TOC]

# 如题：遍历list的过程中，删除元素，异常与不异常的情况分析


<br><br>

### 以下几种情况分析

公共代码：

```java
    private static List<String> list = new LinkedList<>();

    static {
        list.add("1");
        list.add("2");
        list.add("3");
        list.add("4");
        list.add("5");
    }
```

第一种情况：

```java
    public static void test1() {
        for (int i = 1; i < list.size(); i++) {
            if ("4".equals(list.get(i))) {
                list.remove(i);
            }
        }
        list.forEach(System.out::println);
    }
    
》》》运行结果：正常输出

```

第二种情况：

```java
    public static void test2() {
        for (String item : list) {
            if ("3".equals(item)) {  
                list.remove(item);
            }
        }
        list.forEach(System.out::println);
    }
    
》》》运行结果：Exception in thread "main" java.util.ConcurrentModificationException
	
## 此种情况，将“3”改为 “4”，“5” 还会报异常吗?  结果是，正常运行
```

### 为什么会有如此不同的情形？

&emsp;首先，大家都知道list遍历过程中，是不允许删除的，否则会报java.util.ConcurrentModificationException，当然这也是针对部分情况。

&emsp;上面第一种情况，由于我们引入了中间变量i，去遍历list，然后调用他的方法，所以这不属于直接遍历list，也就不会报异常，因为，我们只会每次调用一下他的方法，是不会有问题的。

&emsp;第二种情况，我们使用了增强for循环去遍历list，而增强for循环，其实是使用了LinkedList的内部类ListItr，而Listltr实现了ListIterator的接口。下面这个就是，我从LinkedList截取的一段源码。

```java
  public ListIterator<E> listIterator(int index) {
        checkPositionIndex(index);
        return new ListItr(index);
  }
    
  private class ListItr implements ListIterator<E> {
  
   	 private int expectedModCount = modCount;
   	    
   	 // ...此处省略部分不相关内容，读者可自定查看LinkedList的源码
   		 
        public boolean hasNext() {
            return nextIndex < size;
        }

        public E next() {
            checkForComodification();
            if (!hasNext())
                throw new NoSuchElementException();

            lastReturned = next;
            next = next.next;
            nextIndex++;
            return lastReturned.item;
        }

        public boolean hasPrevious() {
            return nextIndex > 0;
        }

        public E previous() {
            checkForComodification();
            if (!hasPrevious())
                throw new NoSuchElementException();

            lastReturned = next = (next == null) ? last : next.prev;
            nextIndex--;
            return lastReturned.item;
        }

        public void remove() {
            checkForComodification();
            if (lastReturned == null)
                throw new IllegalStateException();

            Node<E> lastNext = lastReturned.next;
            unlink(lastReturned);
            if (next == lastReturned)
                next = lastNext;
            else
                nextIndex--;
            lastReturned = null;
            expectedModCount++;
        }
        
        public void forEachRemaining(Consumer<? super E> action) {
            Objects.requireNonNull(action);
            while (modCount == expectedModCount && nextIndex < size) {
                action.accept(next.item);
                lastReturned = next;
                next = next.next;
                nextIndex++;
            }
            checkForComodification();
        }

        final void checkForComodification() {
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
        }
    }

```

从上面的源码不难看出，这个类就是迭代器的实现，其中报异常的地方也就是checkForComodification()方法，这里说当modCount和expectedModCount不相等的情况下，抛出异常，为此，我查了一下，相关JavaDoc，后来了解到以下内容：

>&emsp;modCount和expectedModCount是用于表示修改次数的，其中modCount表示集合的修改次数，这其中包括了调用集合本身的add方法等修改方法时进行的修改和调用集合迭代器的修改方法进行的修改，而expectedModCount则是表示迭代器对集合进行修改的次数。
>&emsp;集合中是如何保证的呢?在创建迭代器的时候会把对象的modCount的值传递给迭代器的expectedModCount：  
>
```java
private int expectedModCount = modCount; // 此处进行了赋值
```

>目的：设置expectedModCount的目的就是要保证在使用迭代器期间，LinkedList对象的修改只能通过迭代器且只能这一个迭代器进行。

既然知道了这两个参数的意思，那也就不难猜出为何为checkForComodification() 了，就是确保使用了迭代器，就必须由迭代器进行代理修改等操作。

回归第二种情况，我们是否使用了非迭代器操作了数组，的确，我们调用了list.remove()，大家不妨点进去看源码，发现是List接口声明的方法，我们找到对应的LinkedList实现，如下：

```java
    public boolean remove(Object o) {
        if (o == null) {
            for (Node<E> x = first; x != null; x = x.next) {
                if (x.item == null) {
                    unlink(x);
                    return true;
                }
            }
        } else {
            for (Node<E> x = first; x != null; x = x.next) {
                if (o.equals(x.item)) {
                    unlink(x);
                    return true;
                }
            }
        }
        return false;
    }
```

看到这里，其实还不算清晰，有人会觉得，是的，我的确调用了List接口声明方法remove()，但是乍一看，我们也没有修改modCount值呀，别急，这里还有一个unlink()方法，我们还没进去看呢，我们继续跟踪发下以下代码：

```java
    E unlink(Node<E> x) {
        // assert x != null;
        final E element = x.item;
        final Node<E> next = x.next;
        final Node<E> prev = x.prev;

        if (prev == null) {
            first = next;
        } else {
            prev.next = next;
            x.prev = null;
        }

        if (next == null) {
            last = prev;
        } else {
            next.prev = prev;
            x.next = null;
        }

        x.item = null;
        size--;
        // 大Boss挖掘到了
        modCount++;
        return element;
    }
```

现在可以清晰的看到，原来，我们调用了remove()，remove()调用了unlink()，unlink内部操作元素完成之后，会modCount++，表示自己做了修改操作，累计操作+1。这样，修改完了，迭代器checkForComodification()，就会发现expectedModCount比modCount小1，也就不等于，接着抛出异常，原来，困惑我们的异常，是这样产生的。

当你以为问题都结束，我们回过头在去看下事例，如果我们不是要删除“3”，我们要删除“4”，或者“5”，为什么又正常了呢，我们不是调用了remove()，方法了吗，为啥呢？

好，我们都知道增强for循环，使用了集合迭代器，迭代器通过hasNext()，判断是否存在下一个值，我们先看下hasNext()源码：

```java
        public boolean hasNext() {
            return nextIndex < size;
        }
```
其实判断规则很简单，下一个索引与这个数组的大小比较小于他，就表示后面没有元素了。我们要删除“4”，来模拟下，size刚开始为5，nextIndex指到“4”的时候为4（因为当前“4”的index是3），这个如果不理解可以在LinkedList的879行，895行打上断点（jdk8），确认查看。

```java
        ListItr(int index) {
            // assert isPositionIndex(index);
            next = (index == size) ? null : node(index); // 断点，查看index初始值
            nextIndex = index;
        }
		 // ... 省略
        public E next() {
            checkForComodification();
            if (!hasNext())
                throw new NoSuchElementException();

            lastReturned = next;
            next = next.next;
            nextIndex++;
            return lastReturned.item;// 断点，查看当取到“4”的时候，nextIndex值
        }
```
好，为了更加形象一点，我把值写在下面：

```java
》》当前状态：
	数组取到的值：“4”
	nextIndex=4
	size=5 
》》调用remove方法之后
	“4”被删除了，数组变为“1”，“2”，“3”，“5”， // 这里变化参考remove之后的操作，如果你有兴趣，可以了解下ArrayList对应的实现，
	nextIndex=4
	size=4 
》》增强for循环继续取下一值
	调用hasNext方法，nextIndex < size ，  4 < 4  false，这样数组就不会在遍历了，也就不会有异常的出现
```
删除“4”为什么不报错的原因，我们也知道了，那“5”呢，我们可以猜测下，删除了“5”，size=4，nextIndex=5，hasNext()依旧是false，不继续循环。这只是我们的推测，下面看下实际debug的情况：

```
》》当前状态：
	数组取到的值：“5”
	nextIndex=5
	size=5 
》》调用remove方法之后
	“5”被删除了，数组变为“1”，“2”，“3”，“4”， 
	nextIndex=5
	size=4 
》》增强for循环继续取下一值
	调用hasNext方法，nextIndex < size ，  5 < 4  false，这样数组就不会在遍历了，也就不会有异常的出现
```

好了，困惑我们的情况也理清楚了，下面得出结论：
**当增强for循环删除数组元素的时候，删除倒数第二个和倒数第一个，不会抛出异常，删除其他的会抛出异常。**
### 如何正确操作遍历中删除的情况？
继续下个话题，为了避免这些情况，我们应改如何正确的使用遍历中删除元素呢，下面介绍几种方法：
第一种，则是上面的第一种，使用中间值i来遍历。
第二种，使用集合迭代器遍历，并且使用迭代器的删除方法。

```java
	public static void test3() {
        Iterator<String> iterator = list.iterator();
        while (iterator.hasNext()) {
            String item = iterator.next();
            if ("3".equals(item)) {
                iterator.remove();
            }
        }
        list.forEach(System.out::println);
    }
```
第三种，由于jdk8的lamda表达式的特性，我们很优雅的有了以下的两行

```java
    public static void test3() {
        list.removeIf("3"::equals);
        list.forEach(System.out::println);
    }
```
第四中，使用CopyOnWriteArrayList，操作。（不推荐，此容器有更好的应用场景）
```java
    public static void test5() {
        CopyOnWriteArrayList<String> cowList = new CopyOnWriteArrayList<String>(list);
        for (String item : cowList) {
            if ("3".equals(item)) {
                list.remove(item);
            }
        }
        list.forEach(System.out::println);
    }
```


对上面这些情况解释下，如何保证不会抛出异常，第一种情况无需多说，我们直接来看第二种情况。

我们调用了集合迭代器的删除方法，源码如下，可以清晰的看到，方法内部调用了unlink()方法，unlink会对modCount++，累加集合的修改次数，方法的最后，集合迭代器又对expectedModCount++，累加了迭代器的修改次数，这样在checkForComodification()，方法中两者相等，不会抛出异常。
```java
        public void remove() {
            checkForComodification();
            if (lastReturned == null)
                throw new IllegalStateException();

            Node<E> lastNext = lastReturned.next;
            unlink(lastReturned);
            if (next == lastReturned)
                next = lastNext;
            else
                nextIndex--;
            lastReturned = null;
            expectedModCount++;
        }
```

第三种，lamda表达式的写法，也是使用了集合迭代器进行操作，只是jdk8支持了这种函数式编程方法。

第四种，Copy-On-Write简称COW，是一种用于程序设计中的优化策略。其基本思路是，从一开始大家都在共享同一个内容，当某个人想要修改这个内容的时候，才会真正把内容Copy出去形成一个新的内容然后再改，这是一种延时懒惰策略。面对我们的list，我们在修改的时候其实COW帮我们copy出一个snapshot（快照），我们修改的操作会映射在这个snapshot上，最后在修改引用，指向copy出的snapshot。

```java
   public boolean remove(Object o) {
        Object[] snapshot = getArray(); // 取出当前的一个快照版本
        int index = indexOf(o, snapshot, 0, snapshot.length);
        return (index < 0) ? false : remove(o, snapshot, index);
    }

    private boolean remove(Object o, Object[] snapshot, int index) {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            Object[] current = getArray();
            int len = current.length;
            if (snapshot != current) findIndex: {
                int prefix = Math.min(index, len);
                for (int i = 0; i < prefix; i++) {
                    if (current[i] != snapshot[i] && eq(o, current[i])) {
                        index = i;
                        break findIndex;
                    }
                }
                if (index >= len)
                    return false;
                if (current[index] == o)
                    break findIndex;
                index = indexOf(o, current, index, len);
                if (index < 0)
                    return false;
            }
            Object[] newElements = new Object[len - 1]; // new一个新的数组
            System.arraycopy(current, 0, newElements, 0, index); // 进行copy操作，映射remove的效果
            System.arraycopy(current, index + 1,
                             newElements, index,
                             len - index - 1);
            setArray(newElements); // 修改引用
            return true;
        } finally {
            lock.unlock();
        }
    }
```