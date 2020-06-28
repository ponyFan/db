package com.ga.entity.model;

import com.alibaba.fastjson.JSON;

/**
 * @author zelei.fan
 * @date 2019/11/28 14:15
 * @description
 */
public class PageBean<T> {

    private int page;

    private int pageSize;

    private int count;

    private T data;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
