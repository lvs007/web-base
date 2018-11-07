package liang.dao.common;


import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;

/**
 * 构建SQL的快捷方式
 * @author skyfalling
 */
public class SqlBuilder {

    private StringBuilder sb = new StringBuilder();


    public SqlBuilder(String sql) {
        this.sb.append(sql);
    }

    public SqlBuilder append(String sql) {
        sb.append(sql);
        return this;
    }

    public SqlBuilder appendIfNotNull(String sql, Object target) {
        if (target != null) {
            sb.append(sql);
        }
        return this;
    }

    public SqlBuilder appendIfNotEmpty(String sql, String target) {
        if (StringUtils.isNotEmpty(target)) {
            sb.append(sql);
        }
        return this;
    }
    
    public <T> SqlBuilder appendIfNotEmpty(String sql, Collection<T> target) {
        if (CollectionUtils.isNotEmpty(target)) {
            sb.append(sql);
        }
        return this;
    }

    public SqlBuilder appendIfTrue(String sql, boolean expression) {
        if (expression) {
            sb.append(sql);
        }
        return this;
    }


    /**
     * 不为0
     *
     * @param sql
     * @param number
     * @return
     */
    public SqlBuilder appendIfNotZero(String sql, Number number) {
        if (number != null && number.intValue() != 0) {
            sb.append(sql);
        }
        return this;
    }

    /**
     * 非负数
     *
     * @param sql
     * @param number
     * @return
     */
    public SqlBuilder appendIfNotNegative(String sql, Number number) {
        if (number != null && number.intValue() >= 0) {
            sb.append(sql);
        }
        return this;
    }

    /**
     * 正数
     *
     * @param sql
     * @param number
     * @return
     */
    public SqlBuilder appendIfPositive(String sql, Number number) {
        if (number != null && number.intValue() > 0) {
            sb.append(sql);
        }
        return this;
    }

    public String toSql() {
        return sb.toString();
    }

    public String toString() {
        return sb.toString();
    }

}
