package com.ga.note.tree;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * @author zelei.fan
 * @date 2018/3/12 16:39
 * @description
 */
public class Test {

    public static void main(String[] args) {
        Unsafe unsafe = null;
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (Unsafe) field.get(null);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        long l = unsafe.allocateMemory(1024);
        System.out.println(l);
    }
}