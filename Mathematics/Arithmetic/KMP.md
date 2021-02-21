# KMP

> KMP算法是一种改进的字符串匹配算法，由D.E.Knuth，J.H.Morris和V.R.Pratt提出的，因此人们称它为克努特—莫里斯—普拉特操作（简称KMP算法）。KMP算法的核心是利用匹配失败后的信息，尽量减少模式串与主串的匹配次数以达到快速匹配的目的。具体实现就是通过一个next()函数实现，[函数](https://baike.baidu.com/item/函数/18686609)本身包含了模式串的局部匹配信息。KMP算法的[时间复杂度](https://baike.baidu.com/item/时间复杂度/1894057)O(m+n) [1] 。

## 引言

**出自LeetCode的一道题**：

给你两个字符串 haystack 和 needle ，请你在 haystack 字符串中找出 needle 字符串出现的第一个位置（下标从 0 开始）。如果不存在，则返回  -1 。

**解析**：

我相信大部分人，第一时间蹦出来的想法是，拿needle与haystack的所有子串去匹配，暴力枚举对不。显而易见，这是个费力不费脑的解法，不过既然提出来了，

我们按照暴力枚举的解法来走一遍。

例如：haystack = "ABCABDABCEABD"，needle = "ABCE"。

暴力匹配，就是目标串和模式串一个一个的对比。

![img](https://pic4.zhimg.com/50/v2-db77fb04ba5d6857c7a474f938bb9b97_hd.jpg?source=1940ef5c)

当A匹配成功，继续开始比对，直到我们遇见一个不匹配的字符。

![img](https://pic4.zhimg.com/80/v2-6a3e73ebd7c4a935a794eb808a492d6e_720w.jpg?source=1940ef5c)

然后我们调整模式串，从目标串的下一个字符开始匹配。很遗憾，还是没有匹配成功（A和B）

![img](https://pic1.zhimg.com/80/v2-ff46738bc63ec863ccf2bc116a49df5a_720w.jpg?source=1940ef5c)

继续这个步骤：

![img](https://pic2.zhimg.com/80/v2-96b13986c8d762d4a5242d7ce4031e05_720w.jpg?source=1940ef5c)

直到我们完成整个匹配过程：

![img](https://pic2.zhimg.com/80/v2-5f6cde713f69fded420ffa6b24b2109c_720w.jpg?source=1940ef5c)

假若我们目标串长度为m，模式串长度为n。模式串与目标串至少比较m次，又因其自身长度为n，所以理论的时间复杂度为**O(m*n)。**但我们可以看到，**因为途中遇到不能匹配的字符时，就可以停止，并不需要完全对比（比如上图第2行）**。所以虽然理论时间复杂度为 **O(m\*n)** ，但其实大部分情况效率高很多。

暴力匹配又称为BF算法，暴风算法。代码比较简单：

```java
public int strStr(String haystack, String needle) {
    int n = haystack.length(), m = needle.length();
    for (int i = 0; i + m <= n; i++) {
        boolean flag = true;
        for (int j = 0; j < m; j++) {
            if (haystack.charAt(i + j) != needle.charAt(j)) {
                flag = false;
                break;
            }
        }
        if (flag) {
            return i;
        }
    }
    return -1;
}
```

## 正文

接下来我们开始说KMP，假如还是上面的这个串。最开始其实还是一样，我们依次对比A-A、B-B、C-C，直到遇见第一个无法匹配的字符A-E。

![img](https://pic1.zhimg.com/80/v2-85e22c339c14097372bd1c021ad277a5_720w.jpg?source=1940ef5c)

现在开始不一样了，如果按照上面的暴力匹配。此时目标串我们应该回到 B 这个位置，模式串应直接回到头。但是按照 KMP 的思路，**在我们在第一次匹配后，因为 BC 匹配成功了，所以我们知道了 BC 不等于 A（注意这个逻辑关系）**：

![img](https://pic1.zhimg.com/80/v2-e7e6f1bca471bb368ad8b71cd1a68f86_720w.jpg?source=1940ef5c)

那既然已知了 BC 不等于 A，我们就没必要用 A 和 BC 进行匹配了。那我们直接用 A 越过前面不需要匹配的 BC：

![img](https://pic2.zhimg.com/80/v2-3b3518c5a6dac69a9b34808b2fdeedad_720w.jpg?source=1940ef5c)

继续向下适配，我们发现在 D-C 处，匹配不上了。

![img](https://pic4.zhimg.com/80/v2-98fca0a5b3a040b8706ecebcb94dc959_720w.jpg?source=1940ef5c)

那我们因为前面的 B 又匹配成功了，那我们就知道 B 不等于 A，所以我们又可以直接略过前面的 B：

![img](https://pic1.zhimg.com/80/v2-06058ea370a67a27ae70259b85dfee6e_720w.jpg?source=1940ef5c)

也就是说，我们可以直接从 D 处开始比较：

![img](https://pic3.zhimg.com/80/v2-b8ae7e01a841689b3f3cb3757c65ee7c_720w.jpg?source=1940ef5c)

继续向下比较：

![img](https://pic2.zhimg.com/80/v2-9aaabb13ed1f326a5d7268bc1587078a_720w.jpg?source=1940ef5c)

到现在为止，你已经掌握了 KMP 的前百分之五十：**在KMP中，如果模式串和目标串没有匹配成功，目标串不回溯**。

现在我们需要换一个新串，来掌握接下来的百分之五十：

![img](https://pic4.zhimg.com/80/v2-e32dcc7c2780aa7f1f106e4fe7865353_720w.jpg?source=1940ef5c)

我们还是从头开始匹配，直到遇到第一个不匹配的字符：

![img](https://pic1.zhimg.com/80/v2-bb8c3ed066527b8af492e1b3ecf04d24_720w.jpg?source=1940ef5c)

到这里和上面的例子还是一样，**因为我们的 BC 匹配成功了，所以我们知道 BC 不等于 A，所以我们可以跳过 BC（注意这个逻辑）**：

![img](https://pic4.zhimg.com/80/v2-060f16fa5fd3ba13779de753603c3376_720w.jpg?source=1940ef5c)

所以我们从 A 处开始比较：

![img](https://pic1.zhimg.com/80/v2-50ec2ecfc4031f58300e62b12b09c1f2_720w.jpg?source=1940ef5c)

直到我们再次匹配失败：

![img](https://pic2.zhimg.com/80/v2-c87a17f262dc7629c0725288ba95fe58_720w.jpg?source=1940ef5c)

我想到现在你已经知道怎么做了，来和我一起说。**因为前面的 B 匹配成功了，所以我们知道 B 不等于 A，所以我们可以跳过 B。**当然，跳过之后下一次的匹配直接失败了（A-D）。

![img](https://pic1.zhimg.com/80/v2-a7c46a92f8e21974061454bf91fad7d6_720w.jpg?source=1940ef5c)

重点来了！！！然后我们继续匹配下一位。我们发现这一次，我们的匹配基本就要匹配成功了，但是卡在了最后一步的比较（D-B）。

![img](https://pic4.zhimg.com/80/v2-bac63c422f8981e38aa2e9c9314a3477_720w.jpg?source=1940ef5c)

现在怎么办？假若我们把两个串修改一下（把里边的AB修改成XY），那么显而易见，你当然知道从哪里开始：

![img](https://pic1.zhimg.com/80/v2-16d4b5574974fdae1073042bf53b6687_720w.jpg?source=1940ef5c)

但是现在的问题是，在模式串中 AB 重复出现了，那我们是不是可以在下次比较的时候直接把 AB 给让出来？

![img](https://pic1.zhimg.com/80/v2-a3c5bbdc5c99c8eab486c60e49593819_720w.jpg?source=1940ef5c)

所以我们把这个AB让出来，让出来之后，我们相当于在 模式串 上又跳过了 2个字符。（也就是说模式串下一次匹配从C开始）

![img](https://pic2.zhimg.com/80/v2-8a50bfcf2ab4e42d9f0f0c74385190c4_720w.jpg?source=1940ef5c)

其实到这里 KMP 就基本完事了。我们可以稍微总结下：

- 如果模式串和目标串匹配成功，长串短串都加一
  
- 如果模式串和目标串没有匹配成功：
  
  - 目标串不回溯（**在上面的分割线之前，我都是给你讲这个**）
  - 模式串回溯到匹配未成功的字符前的子串的相同的真前缀和真后缀的最大长度**（在上面的分割线之后，我重点是给你讲这个）**

好了，我知道上面匹配成功后的第二种情况有点拗口。所以我又单独拎出来和你说。这句话是啥意思呢？

假若我们有个串 abbaab：

- a, ab, abb, abba, abbaa，就是它的真前缀。
- b, ab, aab, baab, bbaab, 就是它的真后缀。
- “真”字，**说白了就是不包含自己**。

在我们上面的示例中，未成功的字符前的子串是 ABCEAB，它相同的最长的真前缀和真后缀就是 AB，最大长度就是2。所以我们就把模式串回溯到第2个位置处。

![img](https://pic2.zhimg.com/80/v2-c75c331d1f7e3543336ad4fd7446546a_720w.jpg?source=1940ef5c)

我猜有人要说话了，“不是说模式串是回溯到真前缀和真后缀的最大长度位置处吗？那为什么上面的第一个例子，是回到了起始位置呢？”

![img](https://pic3.zhimg.com/80/v2-50ec2ecfc4031f58300e62b12b09c1f2_720w.jpg?source=1940ef5c)

其实，不是我们没有回溯模式串，而是此时的最大长度（指的是相同真前缀和真后缀的最大长度，后面都省略）其实就是 0。

那我们怎么获取最大长度呢？就可以很自然的引入 next表 了。**不管你是把next表 理解成描述最大长度的东东，还是把 next表 理解成用来回溯模式串的东东，其实都是可以的！！！这也是为什么你在网上看到很多人文章对next表理解不一致的原因。**

![img](https://pic2.zhimg.com/80/v2-36d419d3dd6447dfe62ef97e88498dba_720w.jpg?source=1940ef5c)

我们拿上面标黄色那个解释一下，ABCEAB 不包含自己，那就是 ABCEA，ABCEA的 真前缀 和 真后缀 为：

- A,AB,ABC,ABCE
- A,EA,CEA,BCEA

所以最大长度就是 1。那这个 1 干啥用呢？我们可以在下次比的时候就直接把这个 A 让过去，直接从 B 开始比。

![img](https://pic1.zhimg.com/80/v2-fb197dd4c75e02ebfab8c45cc1b6cf38_720w.jpg?source=1940ef5c)

这里注意，如果我们模式串稍微修改成下面这样，此时 F 的最大长度就是 0，并不是 2。初学者很容易把 AB 误认为是最长相同的真前缀和真后缀。

![img](https://pic4.zhimg.com/80/v2-58443bddd316043e2d64b6df7d92e009_720w.jpg?source=1940ef5c)

到这里为止，其实 KMP 的思路已经快说完了。但是大神说话了，大神认为这个匹配表，还得再改改，不然会出问题。

![img](https://pic1.zhimg.com/80/v2-eac4778e53522f9a9395584342358869_720w.jpg?source=1940ef5c)

为什么会出问题呢，我们说了，对 KMP 而言，**如果没有匹配成功，目标串是不回溯的**。那如果目标串不回溯，如果模式串一直都是 0，是不是意味着这个算法就没办法继续进行下去？所以大神把这个 next匹配表 改了一下，把 0 位置处的 next表 值改为了 -1。

![img](https://pic1.zhimg.com/80/v2-b65530f3687669192edf1df152e4bbe7_720w.jpg?source=1940ef5c)

那这个 -1 是干嘛用的呢？**其实只是一个代码技巧**！大家注意一下第 7 行代码，假若没有 j == -1，此时如果 next[j] 等于 0，是不是就进死循环了。而加上这一句，相当于说无论什么情况下，模式串的第一个字符都可以匹配（对 j 而言，此时 -1++，是不是还是0。但是此时模式串却向前走了。不就不会因为死循环而卡死了吗？**请大家自行脑补没有 j==-1 这行代码时，死循环卡死在11行的过程**）

```java
public int kmpSearch(String haystack, String needle, int[] next){
    int l1 = haystack.length();
    int l2 = needle.length();
    int i = 0, j = 0;
    while (i < l1 && j < l2) {
        if (j == -1 || haystack.charAt(i) == needle.charAt(j)) {
            i++;
            j++;
        } else {
            j = next[j];
        }
    }
    if (j == l2) {
        return i - j;
    }
    return -1;
}
```

到这里为止，其实 KMP 就讲的差不太多了，代码还是比较简单的。但是麻烦的是？一般我们并没有现成的 next表 直接使用。那 next表 又该如何生成呢？

其实 next表 的生成，我们也可以看作是字符串匹配的过程：**即原模式串和原模式串自身前缀进行匹配的过程。**

我们用下面这个字符串来讲一下：XXYXXYXXX。

<img src="https://pic1.zhimg.com/50/v2-b5cee9ee8ee005dacf293529e91d727a_hd.jpg?source=1940ef5c" alt="img"  />                     

对于该字符串：

- 真前缀为 X,XX,XXY,XXYX,XXYXX.....
- 真后缀为 X,XX,XXX,YXXX,XYXXX.....

为了方便大家理解，我画了两种图（左图是真实的填表过程，右图是脑补过程）：

- 首先 index[0] 肯定是填写 0

<img src="https://pic1.zhimg.com/50/v2-da21a4dd9cfaa77255270d4ad0376c69_hd.jpg?source=1940ef5c" alt="img" style="zoom: 45%;" />                              <img src="https://pic1.zhimg.com/80/v2-da21a4dd9cfaa77255270d4ad0376c69_720w.jpg?source=1940ef5c" alt="img" style="zoom: 45%;" />

- 然后填写 index[1]。**如果匹配上，我们把 i 和 j 都加一**。

<img src="https://pic3.zhimg.com/50/v2-5af23f3346f6a1aabd41d375957aefb0_hd.jpg?source=1940ef5c" alt="img" style="zoom:45%;" />                              <img src="https://pic3.zhimg.com/80/v2-5af23f3346f6a1aabd41d375957aefb0_720w.jpg?source=1940ef5c" alt="img" style="zoom:45%;" />

- 然后填写 index[2]，**如果没有匹配上，就把 j 回溯到 j 当前指向的前一个位置的 index 处。在这里，也就是 0 。**

<img src="https://pic1.zhimg.com/50/v2-b35a768f7aef845ab218dfae66426e1b_hd.jpg?source=1940ef5c" alt="img" style="zoom:45%;" />                              <img src="https://pic1.zhimg.com/80/v2-b35a768f7aef845ab218dfae66426e1b_720w.jpg?source=1940ef5c" alt="img" style="zoom:45%;" />

- 注意，是回溯完成后才开始填表，此时 index[2] 为 0

<img src="https://pic2.zhimg.com/50/v2-79011ce06e96d9eb7731016b727c0070_hd.jpg?source=1940ef5c" alt="img" style="zoom:45%;" />                              <img src="https://pic2.zhimg.com/80/v2-79011ce06e96d9eb7731016b727c0070_720w.jpg?source=1940ef5c" alt="img" style="zoom:45%;" />

- 然后我们移动 i，发现下一位匹配成功。同时给 i 和 j 加一，并填表。

<img src="https://pic1.zhimg.com/50/v2-35574577303695f0810e795417a4b9ca_hd.jpg?source=1940ef5c" alt="img" style="zoom:45%;" />                              <img src="https://pic1.zhimg.com/80/v2-35574577303695f0810e795417a4b9ca_720w.jpg?source=1940ef5c" alt="img" style="zoom:45%;" />

- 填完表后，我们发现下一位仍然匹配。继续移动 i 和 j。

<img src="https://pic1.zhimg.com/50/v2-376e0e1a729c6e32e72cce055ef9b2ef_hd.jpg?source=1940ef5c" alt="img" style="zoom:45%;" />                              <img src="https://pic1.zhimg.com/80/v2-376e0e1a729c6e32e72cce055ef9b2ef_720w.jpg?source=1940ef5c" alt="img" style="zoom:45%;" />

（填表）

<img src="https://pic1.zhimg.com/50/v2-2a8485d2bf2c55a7f68aac13bec6232a_hd.jpg?source=1940ef5c" alt="img" style="zoom:45%;" />                              <img src="https://pic1.zhimg.com/80/v2-2a8485d2bf2c55a7f68aac13bec6232a_720w.jpg?source=1940ef5c" alt="img" style="zoom:45%;" />

（仍然匹配，继续移动 i 和 j）

- 仍然匹配成功，继续重复上面的操作。

<img src="https://pic1.zhimg.com/50/v2-0445944025c4c8795beb35fec3bd0c93_hd.jpg?source=1940ef5c" alt="img" style="zoom:45%;" />                              <img src="https://pic1.zhimg.com/80/v2-0445944025c4c8795beb35fec3bd0c93_720w.jpg?source=1940ef5c" alt="img" style="zoom:45%;" />

<img src="https://pic2.zhimg.com/50/v2-95b611f6a6a8fe9b36de36dd8bd456fb_hd.jpg?source=1940ef5c" alt="img" style="zoom:45%;" />                              <img src="https://pic2.zhimg.com/80/v2-95b611f6a6a8fe9b36de36dd8bd456fb_720w.jpg?source=1940ef5c" alt="img" style="zoom:45%;" />

<img src="https://pic1.zhimg.com/50/v2-f0624e7f871aec1f758e10077153175c_hd.jpg?source=1940ef5c" alt="img" style="zoom:45%;" />                              <img src="https://pic1.zhimg.com/80/v2-f0624e7f871aec1f758e10077153175c_720w.jpg?source=1940ef5c" alt="img" style="zoom:45%;" />

- 注意，**到这里开始匹配失败了**。上面说了，如果没有匹配成功，**把 j 回溯到 j 当前指向的前一个位置的 index 处**。在这里，也就是 2 。

<img src="https://pic2.zhimg.com/50/v2-b4d9bf0df7d3d5b085f5cc5bd5f6124f_hd.jpg?source=1940ef5c" alt="img" style="zoom:45%;" />                              <img src="https://pic2.zhimg.com/80/v2-b4d9bf0df7d3d5b085f5cc5bd5f6124f_720w.jpg?source=1940ef5c" alt="img" style="zoom:45%;" />

<img src="https://pic1.zhimg.com/50/v2-27f722b97b4e75e41ac02b39d81ca17f_hd.jpg?source=1940ef5c" alt="img" style="zoom:45%;" />                              <img src="https://pic1.zhimg.com/80/v2-27f722b97b4e75e41ac02b39d81ca17f_720w.jpg?source=1940ef5c" alt="img" style="zoom:45%;" />

（j 的前一个位置的 index）

<img src="https://pic1.zhimg.com/50/v2-d69be84ad5c8d46cb62bc1bc78a8d369_hd.jpg?source=1940ef5c" alt="img" style="zoom:45%;" />                              <img src="https://pic1.zhimg.com/80/v2-d69be84ad5c8d46cb62bc1bc78a8d369_720w.jpg?source=1940ef5c" alt="img" style="zoom:45%;" />

（回溯完成后，我们发现仍然不匹配）

- 继续这个回溯的过程。。。（这一步是整个 next表 构建的核心）

<img src="https://pic2.zhimg.com/50/v2-5fe58feb5b5be755bc45ccce624ce2c1_hd.jpg?source=1940ef5c" alt="img" style="zoom: 67%;" />                              <img src="https://pic2.zhimg.com/80/v2-5fe58feb5b5be755bc45ccce624ce2c1_720w.jpg?source=1940ef5c" alt="img" style="zoom: 67%;" />

（这个蓝色的小标是下次的回溯位置）

<img src="https://pic1.zhimg.com/50/v2-4fdac37af5c526f59e6e9685e38fa1e1_hd.jpg?source=1940ef5c" alt="img" style="zoom: 67%;" />                              <img src="https://pic1.zhimg.com/80/v2-4fdac37af5c526f59e6e9685e38fa1e1_720w.jpg?source=1940ef5c" alt="img" style="zoom: 67%;" />

（回溯后，我们发现匹配成功了）

<img src="https://pic4.zhimg.com/50/v2-93810b939533a0c5545c4d29af145720_hd.jpg?source=1940ef5c" alt="img" style="zoom: 67%;" />                              <img src="https://pic4.zhimg.com/80/v2-93810b939533a0c5545c4d29af145720_720w.jpg?source=1940ef5c" alt="img" style="zoom: 67%;" />

（然后我们可以填表了）

- 注意！这里为什么是填2，其实就是填写上次回溯到的那个匹配成功的位置的index值加1。

细心的读者，估计到这里发现一点问题。我们把填完后的表拿出来：

<img src="https://pic4.zhimg.com/50/v2-0564a477f5fa09d6a158d107c8c7e42e_hd.jpg?source=1940ef5c" alt="img" style="zoom: 67%;" />                              <img src="https://pic4.zhimg.com/80/v2-0564a477f5fa09d6a158d107c8c7e42e_720w.jpg?source=1940ef5c" alt="img" style="zoom: 67%;" />

我们发现这个表和我们最上面说的不太一样，我们最上面说的 next表 的首位是 -1，并且要记录哪一个 index 位置的 next 值，是去看该元素前面所有子串的真前缀和真后缀的最大长度。这句话有点拗口，我们还是看到下面这个。

<img src="https://pic1.zhimg.com/50/v2-97ad4d519ef7e81e3b4044ad6e12d6a7_hd.jpg?source=1940ef5c" alt="img" style="zoom:50%;" />                              <img src="https://pic1.zhimg.com/80/v2-97ad4d519ef7e81e3b4044ad6e12d6a7_720w.jpg?source=1940ef5c" alt="img" style="zoom:50%;" />

比如 index 为 5 时，此时next的值是看 ABCEA 的最大长度（真后缀A，真前缀A，所以为1）。**但是在我们下面这个表中，我们发现我们是记录的当前索引位置处的最大长度**。其实我这里要说一下，下面这个表，其实我们一般称为**部分匹配表**，或者pmt。

<img src="https://pic1.zhimg.com/50/v2-0564a477f5fa09d6a158d107c8c7e42e_hd.jpg?source=1940ef5c" alt="img" style="zoom: 50%;" />                              <img src="https://pic1.zhimg.com/80/v2-0564a477f5fa09d6a158d107c8c7e42e_720w.jpg?source=1940ef5c" alt="img" style="zoom: 50%;" />

那这个表和我们的 next 表有什么关系吗，我们发现把这个表往后串一位，就得到了我们最终的 next 表。

<img src="https://pic1.zhimg.com/50/v2-d4b950298c0150fa17c9398faff2acb8_hd.jpg?source=1940ef5c" alt="img" style="zoom: 50%;" />                              <img src="https://pic1.zhimg.com/80/v2-d4b950298c0150fa17c9398faff2acb8_720w.jpg?source=1940ef5c" alt="img" style="zoom: 50%;" />

但是但是但是！！！并不是所有讲解 KMP 的地方都会给你提一提部分匹配表的概念，有的地方干脆就直接把这个 pmt 等同于 next 表使用。**这种属于错误讲解吗？其实不是的！**因为我上面也说了，next表 在最初始位置补 -1，或者甚至干脆把 pmt 的第一位补一个 -1 当作 next表，这都统统是可以的。**因为最关键的还是说你到时候怎么去使用！毕竟 next表 的定义也是人们给它赋予的！**

举个例子，假如你 next表 的首位不补 -1，我们其实就可以在前面 KMP 的算法中，去掉 -1 的逻辑。而单独加一个 if 判断来解决上面说的死循环的问题。

## 举一反三

好了，到此为止，相信你对KMP至少了解了一些，那么我们解下来就用KMP来优化引言那道题的解法：

**出自LeetCode的一道题**：

给你两个字符串 haystack 和 needle ，请你在 haystack 字符串中找出 needle 字符串出现的第一个位置（下标从 0 开始）。如果不存在，则返回  -1 。

**解析**：

```java
	public int strStr(String haystack, String needle) {
		int n = haystack.length(), m = needle.length();
		if (m == 0) {
			return 0;
		}
		// NEXT表
		int[] pi = new int[m];
		for (int i = 1, j = 0; i < m; i++) {
			while (j > 0 && needle.charAt(i) != needle.charAt(j)) {
				j = pi[j - 1];
			}
			if (needle.charAt(i) == needle.charAt(j)) {
				j++;
			}
			pi[i] = j;
		}
		// 匹配
		for (int i = 0, j = 0; i < n; i++) {
			while (j > 0 && haystack.charAt(i) != needle.charAt(j)) {
				j = pi[j - 1];
			}
			if (haystack.charAt(i) == needle.charAt(j)) {
				j++;
			}
			if (j == m) {
				return i - m + 1;
			}
		}
		return -1;
	}
```

**测试**：

```java
	@Test
	public void test() {
		System.out.println(strStr("qqXXYXXYXXX", "XXYXXYXXX"));
		System.out.println(strStr("mississippi", "issipi"));
		System.out.println(strStr("babbbbbabb", "bbab"));
		System.out.println(strStr("mississippi", "sipp"));
		System.out.println(strStr("hello", "ll"));
		System.out.println(strStr("aaaaa", "bba"));
		System.out.println(strStr("", ""));
		System.out.println(strStr("", "asd"));
	}
```





























