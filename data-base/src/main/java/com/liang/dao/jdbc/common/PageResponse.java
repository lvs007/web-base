/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.liang.dao.jdbc.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 分页的响应
 *
 * @author
 * @param <T>
 */
public class PageResponse<T> implements Serializable {

    private static final long serialVersionUID = 200140725L;
    private long total;
    private List<T> itemList = new ArrayList<>();

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<T> getItemList() {
        return itemList;
    }

    public void setItemList(List<T> itemList) {
        this.itemList = itemList;
    }
}
