package com.ga.common.sqoop;

import java.sql.Timestamp;

/**
 * @author zelei.fan
 * @date 2020/5/22 17:45
 * @description
 */
public class SqoopBean {

    private int i;
    private Timestamp ts;

    public int getI() {
        return i;
    }

    public int setI(int i) {
        this.i = i;
        return i;
    }

    public Timestamp getTs() {
        return ts;
    }

    public Timestamp setTs(Timestamp ts) {
        this.ts = ts;
        return ts;
    }

    @Override
    public String toString() {
        return "sqoopBean{" +
                "i=" + i +
                ", ts=" + ts +
                '}';
    }
}
