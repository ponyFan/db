package com.ga.common.lru;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author zelei.fan
 * @date 2020/5/11 11:23
 * @description lru即在容量满了的情况下删除最近最少用的数据，一般用于缓存数据库热点访问的数据，缓解访问压力，提高查询效率
 */
public class LRU<K, V> extends LinkedHashMap<K, V>{

    public static final int SIZE = 10;
    /*初始容量*/
    public static final int CAPACITY = 10;
    /*负载因子*/
    public static final float LOAD_FACTOR = 0.75f;

    public LRU(){
        /*父类构造，带三个参数是按最新被使用排序*/
        super(CAPACITY, LOAD_FACTOR, true);
    }

    /**
     * 重写父类的removeEldestEntry方法，该方法默认返回false不删除最老数据，返回true则会删除最老的数据
     * @param eldest
     * @return
     */
    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return this.size() > SIZE;
    }

    public static void main(String[] args) {
        /*第三个参数默认false（以key的插入顺排序），true（最新被读取或写的数据排序）*/
        LinkedHashMap<Object, Object> linkedHashMap = new LinkedHashMap<>(CAPACITY, LOAD_FACTOR, true);
        for (int i = 0; i < 10; i++) {
            linkedHashMap.put(i, new Random().nextInt(100));
        }
        System.out.println(linkedHashMap.toString());
        linkedHashMap.get(5);
        System.out.println(linkedHashMap.toString());
        linkedHashMap.get(0);
        System.out.println(linkedHashMap.toString());
        System.out.println("****************LRU*******************");
        LRU<Integer, Object> lru = new LRU<>();
        for (int i = 0; i < 10; i++) {
            lru.put(i, new Random().nextInt(100));
        }
        System.out.println(lru.toString());
        lru.get(5);
        System.out.println(lru.toString());
        lru.put(23, "rt");
        lru.put(24, "s");
        lru.put(25, "d");
        lru.put(26, "ff");
        System.out.println(lru.toString());
    }
}
