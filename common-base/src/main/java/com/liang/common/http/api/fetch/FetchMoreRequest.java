package com.liang.common.http.api.fetch;

/**
 * 加载更多的请求
 *
 */
public class FetchMoreRequest {
    private String cursor;//分界的游标
    private int pageSize;//每一页的数据量，可选

    public FetchMoreRequest() {
    }

    public String getCursor() {
        return cursor;
    }

    public void setCursor(String cursor) {
        this.cursor = cursor;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
