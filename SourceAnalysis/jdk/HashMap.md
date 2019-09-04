```java
/**
* 这个方法是TreeNode类的一个实例方法，调用该方法的也就是一个TreeNode对象，
* 该对象就是树上的某个节点，以该节点作为根节点，查找其所有子孙节点，
* 看看哪个节点能够匹配上给定的键对象
* h k的hash值
* k 要查找的对象
* kc k的Class对象，该Class应该是实现了Comparable<K>的，否则应该是null，参见：
*/
final TreeNode<K,V> find(int h, Object k, Class<?> kc) {
    TreeNode<K,V> p = this; // 把当前对象赋给p，表示当前节点
    do { // 循环
        int ph, dir; K pk; // 定义当前节点的hash值、方向（左右）、当前节点的键对象
        TreeNode<K,V> pl = p.left, pr = p.right, q; // 获取当前节点的左孩子、右孩子。定义一个对象q用来存储并返回找到的对象
        if ((ph = p.hash) > h) // 如果当前节点的hash值大于k得hash值h，那么后续就应该让k和左孩子节点进行下一轮比较
            p = pl; // p指向左孩子，紧接着就是下一轮循环了
        else if (ph < h) // 如果当前节点的hash值小于k得hash值h，那么后续就应该让k和右孩子节点进行下一轮比较
            p = pr; // p指向右孩子，紧接着就是下一轮循环了
        else if ((pk = p.key) == k || (k != null && k.equals(pk))) // 如果h和当前节点的hash值相同，并且当前节点的键对象pk和k相等（地址相同或者equals相同）
            return p; // 返回当前节点
 
        // 执行到这里说明 hash比对相同，但是pk和k不相等
        else if (pl == null) // 如果左孩子为空
            p = pr; // p指向右孩子，紧接着就是下一轮循环了
        else if (pr == null)
            p = pl; // p指向左孩子，紧接着就是下一轮循环了
 
        // 如果左右孩子都不为空，那么需要再进行一轮对比来确定到底该往哪个方向去深入对比
        // 这一轮的对比主要是想通过comparable方法来比较pk和k的大小     
        else if ((kc != null ||
                    (kc = comparableClassFor(k)) != null) &&
                    (dir = compareComparables(kc, k, pk)) != 0)
            p = (dir < 0) ? pl : pr; // dir小于0，p指向右孩子，否则指向右孩子。紧接着就是下一轮循环了
 
        // 执行到这里说明无法通过comparable比较  或者 比较之后还是相等
        // 从右孩子节点递归循环查找，如果找到了匹配的则返回    
        else if ((q = pr.find(h, k, kc)) != null) 
            return q;
        else // 如果从右孩子节点递归查找后仍未找到，那么从左孩子节点进行下一轮循环
            p = pl;
    } while (p != null); 
    return null; // 为找到匹配的节点返回null
}



        /**
         * 将root移到链表的第一个节点
         * Ensures that the given root is the first node of its bin.
         */
        static <K,V> void moveRootToFront(Node<K,V>[] tab, TreeNode<K,V> root) {
            int n;
            if (root != null && tab != null && (n = tab.length) > 0) {
                int index = (n - 1) & root.hash;
                TreeNode<K,V> first = (TreeNode<K,V>)tab[index];
                // 判断是否是同一个对象，即已经是第一个节点了
                if (root != first) {
                    Node<K,V> rn;
                    tab[index] = root;
                    // 这里就是链表的节点移动操作
                    // 取出root的上一节点
                    TreeNode<K,V> rp = root.prev;
                    // 取出root的下一节点，且将下一节点的prev指向root的上一节点
                    if ((rn = root.next) != null)
                        ((TreeNode<K,V>)rn).prev = rp;
                    // root的上一节点的next指向root的下一节点
                    if (rp != null)
                        rp.next = rn;
                    // 原本第一个节点的prev指向root
                    if (first != null)
                        first.prev = root;
                    // root的next指向原本第一个节点，prev指向null
                    root.next = first;
                    root.prev = null;
                }
                // 断言检查是否已经符合要求
                assert checkInvariants(root);
            }
        }
```

