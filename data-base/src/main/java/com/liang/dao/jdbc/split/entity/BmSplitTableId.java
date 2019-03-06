package com.liang.dao.jdbc.split.entity;

/**
 * Created by liangzhiyan on 2017/6/21.
 */
public class BmSplitTableId {
    private long id;
    private int createTime;

    public long getId() {
        return id;
    }

    public BmSplitTableId setId(long id) {
        this.id = id;
        return this;
    }

    public int getCreateTime() {
        return createTime;
    }

    public BmSplitTableId setCreateTime(int createTime) {
        this.createTime = createTime;
        return this;
    }

    public static BmSplitTableId build() {
        int time = (int) (System.currentTimeMillis() / 1000);
        return new BmSplitTableId().setCreateTime(time);
    }
}
