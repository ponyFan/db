package com.ga.common.lru;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author zelei.fan
 * @date 2020/5/11 12:01
 * @description LRU1基于linkedHashMap实现算法都已经现成的，LRU2自己实现算法具体细节：链表+hashMap
 */
public class LRU2<K, V> {

    /*map缓存*/
    private Map<K, Node> cache;

    private Node first;

    private Node last;

    /*容量*/
    private int capcity = 10;

    LRU2(int capcity){
        this.capcity = capcity;
        cache = new HashMap<>(capcity);
    }

    /**
     * 插入
     *  1、判断该值是否在缓存中
     *  2、不在则直接插入，并放在最
     * @param k
     * @param v
     */
    public void put(K k, V v){
        Node node = cache.get(k);
        /*判断缓存中否存在*/
        if (null == node){
            /*不存在
            *   1、判断缓存容量是否大于指定大小，大的话则将最早的元素删除
            *   2、将最新的插入，并移到最前面
            * */
            if (cache.size() > capcity){
                removeLast();
            }
            node = new Node();
            node.key = k;
        }
        /*将node存储到cache中，并将该节点移到第一个*/
        node.value = v;
        cache.put(k, node);
        moveFirst(node);
    }

    /**
     * 读取，如果存在，则移到最前面
     * @param k
     * @return
     */
    public V get(K k){
        Node node = cache.get(k);
        if (null == node){
            return null;
        }
        moveFirst(node);
        return node.value;
    }

    /**
     * 将节点移动到最前面
     *      1、当前链表为空，那么node既是first又是last
     *      2、该node就在第一个，那么不需要移动
     *      3、该node在最后一个，将该node断开，新的last为该node的前一个节点，该node移到first前
     *      4、该node在链表中间，将该node节点断开，将其前后节点相连，即node的pre的next=node的next，node的next的pre=node的pre
     * @param node
     */
    private void moveFirst(Node node){
        /*如果操作的当前node就是第一个则无需处理*/
        if (node == first){
            return;
        }
        /*如果当前node存在next，那么将它移除，让pre和它的next直接相连，即把它的pre赋值给它的next的pre*/
        if (node.next != null){
            node.next.pre = node.pre;
        }
        /*如果当前node有pre，那么把它的next赋值给它pre的next*/
        if (node.pre != null){
            node.pre.next = node.next;
        }
        /*如果node是最后一个，那么把它的pre设置成last*/
        if (node == last){
            last = node.pre;
        }
        /*如果first或last为空，说明是第一个元素，那么该node既是first又是last*/
        if (null == first || null == last){
            last = first = node;
            return;
        }
        /*将当前的first设置为node的next；将当前first的pre设置成node，然后设置新的first即node*/
        node.next = first;
        first.pre = node;
        first = node;
        first.pre = null;
    }

    /**
     * 删除最后一个元素
     */
    private void removeLast(){
        /*将缓存中元素删除*/
        cache.remove(last.key);
        /*将last更新为其前一个元素*/
        if (null != last){
            last = last.pre;
            if (null != last){
                last.next = null;
            }else {
                first = null;
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        Node node = this.first;
        while (null != node){
            builder.append(node.key);
        }
        return builder.toString();
    }

    class Node{

        private Node pre;

        private Node next;

        private K key;

        private V value;

        Node(){}
    }

    public static void main(String[] args) {
        LRU2<Integer, Integer> lru2 = new LRU2<>(10);
        for (int i = 0; i < 10; i++) {
            lru2.put(i, new Random().nextInt(10));
        }
        System.out.println(lru2.toString());
    }
}
