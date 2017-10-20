package liang.dao.jdbc.split;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by liangzhiyan on 2017/6/22.
 */
public class SqlConstants {
    public static final String SELECT = "select";
    public static final String INSERT = "insert";
    public static final String DELETE = "delete";
    public static final String UPDATE = "update";
    public static final String WHERE = " where ";
    public static final String INTO = " into ";
    public static final String VALUES = " values(| values ";
    public static final String ORDER = " order ";
    public static final String GROUP = " group ";
    public static final String IN = " in(| in ";

    public static enum DML {
        SELECT, INSERT, DELETE, UPDATE;

        public static DML getDML(String sql) {
            if (StringUtils.startsWithIgnoreCase(sql, "select")) {
                return SELECT;
            } else if (StringUtils.startsWithIgnoreCase(sql, "insert")) {
                return INSERT;
            } else if (StringUtils.startsWithIgnoreCase(sql, "delete")) {
                return DELETE;
            } else if (StringUtils.startsWithIgnoreCase(sql, "update")) {
                return UPDATE;
            }
            return null;
        }
    }
}
