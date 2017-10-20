package liang.dao.jdbc.split.common;

/**
 * 定制分表函数
 * Created by liangzhiyan on 2017/7/7.
 */
public interface SplitTableMethod {
    int getTableIndex(String column, Object value);
}
