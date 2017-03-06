/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package liang.dao.jdbc.common;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 分页的请求
 *
 * @author
 */
public class PageRequest {

    private int page;
    private int limit;
    private List<Sort> sort = new ArrayList<>();

    /**
     * 获取数据时候的下标，优先级如下：
     * 1，当有start的时候，取start的值，
     * 2，当没有start的时候，取page*limit的值
     *
     * @return
     */
    public int getOffset() {
        if (page >= 1) {
            return (page - 1) * limit;
        } else {
            return 0;
        }
    }

    public List<Sort> getSort() {
        return sort;
    }

    public void setSort(List<Sort> sort) {
        this.sort = sort;
    }

    @Deprecated
    public void setSort(String property, Direction direction) {
        setSort0(property, direction);
    }

    private void setSort0(String property, Direction direction) {
        //如果有存在的，则更新之
        for (Sort s : sort) {
            if (StringUtils.equalsIgnoreCase(property, s.property)) {
                s.direction = direction;
                s.property = property;
                return;
            }
        }
        //否则就添加之
        this.sort.add(new Sort(property, direction));
    }

    public void addSort(String property, Direction direction) {
        //重复添加就抛异常
        addSort0(property, direction);
    }

    private void addSort0(String property, Direction direction) {
        //重复添加就抛异常
        for (Sort s : sort) {
            if (StringUtils.equalsIgnoreCase(property, s.property)) {
                throw new IllegalArgumentException("不能重复添加,property=" + property);
            }
        }
        this.sort.add(new Sort(property, direction));
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public static enum Direction {

        ASC, DESC;
    }

    public static class Sort {

        private String property;
        private Direction direction;

        public Sort(String property, Direction direction) {
            this.property = property;
            this.direction = direction;
        }

        public Direction getDirection() {
            return direction;
        }

        public String getProperty() {
            return property;
        }
    }
}
