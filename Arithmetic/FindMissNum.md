## FindMissNum

### Question

Given an array containing n distinct numbers taken from 0, 1, 2, ..., n, find the one that is missing from the array.
For example, Given nums = [0, 1, 3] return 2.
Note: Your algorithm should run in linear runtime complexity. Could you implement it using only constant extra space complexity?

### Translate

给定一个包含从0,1,2，…， n，查找数组中缺失的那一个。例如，给定nums =[0,1,3]返回2。
注意:你的算法应该以线性运行时复杂度运行。你能仅仅使用恒定的额外空间复杂度来实现它吗

### Answer

Answer one：

```java
  // 借用BitSet实现，不用了解BitSet原理，直接套白狼
  public int missingNumberInByBitSet(int[] array) {
    BitSet bitset = new BitSet(arrays.length);
    for (int item : arrays) {
      bitset.set(item);
    }
    return bitset.nextClearBit(0);
  }
```

Answer two：

```java
  // 借用BitSet原理实现
  public int missingNumberInByBitSet(int[] array) {
    int bitSet = 0;
    for (int element : array) {
      // 1 << element 表示将二进制1左移element位, |= 表示 bitSet 或运算 1 << element
      // 实际上目的：首先将二进制的 0 看成不存在， 反之 1 表示存在，那么 << 移位操作 就是完成将数组里存在的数字在对应位置上标记为1， |=操作就是将所有的存在的值合并到一起去
      bitSet |= 1 << element;
    }
    for (int i = 0; i < array.length; i++) {
      // 先前，我们将对应位置上存在的值已合并成在一起，那么如何判断对应的数字在不在呢？利用与运算，只有不存在的数字对应位置上的值为0，与1<<n与运算才会得到0，进而知道该值不存在
      if ((bitSet & 1 << i) == 0) {
        return i;
      }
    }
    return 0;
  }
```

### Analysis

飞机票：[BitSet](../SourceAnalysis/Jdk/Collection/BitSet.md)

