[TOC]





# 加密算法

## 介绍

### 数字签名
**数字签名**，简单来说就是通过提供 **可鉴别** 的 **数字信息** 验证 **自身身份** 的一种方式。一套 **数字签名** 通常定义两种 **互补** 的运算，一个用于 **签名**，另一个用于 **验证**。分别由 **发送者** 持有能够 **代表自己身份** 的 **私钥** (私钥不可泄露),由 **接受者** 持有与私钥对应的 **公钥** ，能够在 **接受** 到来自发送者信息时用于 **验证** 其身份。

### 对称加密

**对称加密算法** 是应用较早的加密算法，又称为 **共享密钥加密算法**。在 **对称加密算法** 中，使用的密钥只有一个，**发送** 和 **接收** 双方都使用这个密钥对数据进行 **加密** 和 **解密**。这就要求加密和解密方事先都必须知道加密的密钥。

1. 数据加密过程：在对称加密算法中，**数据发送方** 将 **明文** (原始数据) 和 **加密密钥** 一起经过特殊 **加密处理**，生成复杂的 **加密密文** 进行发送。
2. 数据解密过程：**数据接收方** 收到密文后，若想读取原数据，则需要使用 **加密使用的密钥** 及相同算法的 **逆算法** 对加密的密文进行解密，才能使其恢复成 **可读明文**。

### 非对称加密

**非对称加密算法**，又称为 **公开密钥加密算法**。它需要两个密钥，一个称为 **公开密钥** (`public key`)，即 **公钥**，另一个称为 **私有密钥** (`private key`)，即 **私钥**。

因为 **加密** 和 **解密** 使用的是两个不同的密钥，所以这种算法称为 **非对称加密算法**。

1. 如果使用 **公钥** 对数据 **进行加密**，只有用对应的 **私钥** 才能 **进行解密**。
2. 如果使用 **私钥** 对数据 **进行加密**，只有用对应的 **公钥** 才能 **进行解密**。

## 常见的加密算法

### MD5

`MD5` 用的是 **哈希函数**，它的典型应用是对一段信息产生 **信息摘要**，以 **防止被篡改**。严格来说，`MD5` 不是一种 **加密算法** 而是 **摘要算法**。无论是多长的输入，`MD5` 都会输出长度为 `128bits` 的一个串 (通常用 `16` **进制** 表示为 `32` 个字符)。

### SHA1

`SHA1` 是和 `MD5` 一样流行的 **消息摘要算法**，然而 `SHA1` 比 `MD5` 的 **安全性更强**。对于长度小于 `2 ^ 64` 位的消息，`SHA1` 会产生一个 `160` 位的 **消息摘要**。基于 `MD5`、`SHA1` 的信息摘要特性以及 **不可逆** (一般而言)，可以被应用在检查 **文件完整性** 以及 **数字签名** 等场景。

### HMAC算法

`HMAC` 是密钥相关的 **哈希运算消息认证码**（Hash-based Message Authentication Code），`HMAC` 运算利用 **哈希算法** (`MD5`、`SHA1` 等)，以 **一个密钥** 和 **一个消息** 为输入，生成一个 **消息摘要** 作为 **输出**。

`HMAC` **发送方** 和 **接收方** 都有的 `key` 进行计算，而没有这把 `key` 的第三方，则是 **无法计算** 出正确的 **散列值**的，这样就可以 **防止数据被篡改**。

### DES

`DES` 加密算法是一种 **分组密码**，以 `64` 位为 **分组对数据** 加密，它的 **密钥长度** 是 `56` 位，**加密解密** 用 **同一算法**。

`DES` 加密算法是对 **密钥** 进行保密，而 **公开算法**，包括加密和解密算法。这样，只有掌握了和发送方 **相同密钥** 的人才能解读由 `DES`加密算法加密的密文数据。因此，破译 `DES` 加密算法实际上就是 **搜索密钥的编码**。对于 `56` 位长度的 **密钥** 来说，如果用 **穷举法** 来进行搜索的话，其运算次数为 `2 ^ 56` 次。 

### 3DES

是基于 `DES` 的 **对称算法**，对 **一块数据** 用 **三个不同的密钥** 进行 **三次加密**，**强度更高**。

### AES

`AES` 加密算法是密码学中的 **高级加密标准**，该加密算法采用 **对称分组密码体制**，密钥长度的最少支持为 `128` 位、 `192` 位、`256` 位，分组长度 `128` 位，算法应易于各种硬件和软件实现。这种加密算法是美国联邦政府采用的 **区块加密标准**。

`AES` 本身就是为了取代 `DES` 的，`AES` 具有更好的 **安全性**、**效率** 和 **灵活性**。

### RSA

`RSA` 加密算法是目前最有影响力的 **公钥加密算法**，并且被普遍认为是目前 **最优秀的公钥方案** 之一。`RSA` 是第一个能同时用于 **加密** 和 **数字签名** 的算法，它能够 **抵抗** 到目前为止已知的 **所有密码攻击**，已被 `ISO` 推荐为公钥数据加密标准。

> `RSA` **加密算法** 基于一个十分简单的数论事实：将两个大 **素数** 相乘十分容易，但想要对其乘积进行 **因式分解** 却极其困难，因此可以将 **乘积** 公开作为 **加密密钥**。

### ECC

`ECC` 也是一种 **非对称加密算法**，主要优势是在某些情况下，它比其他的方法使用 **更小的密钥**，比如 `RSA` **加密算法**，提供 **相当的或更高等级** 的安全级别。不过一个缺点是 **加密和解密操作** 的实现比其他机制 **时间长** (相比 `RSA` 算法，该算法对 `CPU` 消耗严重)。

### DSA





### 总结

#### 归类

##### 散列算法比较

| 名称  | 安全性 | 速度 |
| ----- | ------ | ---- |
| SHA-1 | 高     | 慢   |
| MD5   | 中     | 快   |

##### 对称加密算法比较

| 名称 | 密钥名称        | 运行速度 | 安全性 | 资源消耗 |
| ---- | --------------- | -------- | ------ | -------- |
| DES  | 56位            | 较快     | 低     | 中       |
| 3DES | 112位或168位    | 慢       | 中     | 高       |
| AES  | 128、192、256位 | 快       | 高     | 低       |

##### 非对称加密算法比较

| 名称 | 成熟度 | 安全性 | 运算速度 | 资源消耗 |
| ---- | ------ | ------ | -------- | -------- |
| RSA  | 高     | 高     | 中       | 中       |
| ECC  | 高     | 高     | 慢       | 高       |

