package liang.common.http.api.fetch;

import java.util.List;


/**
 * 加载更多的服务器响应数据的封装
 *
 */
public class FetchMoreResponse<T> {
    private int pageCount; //总共有多少页，可空
    private String cursor; //下一次获取更多的时候，传入的参数,第一次请求的时候是可空的
    private boolean hasMore;
    private List<T> list;

    public String getCursor() {
        return cursor;
    }

    public void setCursor(String cursor) {
        this.cursor = cursor;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public boolean isHasMore() {
        return hasMore;
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
