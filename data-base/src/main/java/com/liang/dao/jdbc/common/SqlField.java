package com.liang.dao.jdbc.common;

import org.apache.commons.lang3.StringUtils;

/**
 * select 用的字段
 *
 */
public class SqlField {

    private String field; //
    private SqlFunc func;
    private String alias; // 当有function时设定的

    private SqlField() {

    }

    public SqlField(String field) {
        this.field = field;
    }

    public SqlField(String field, String alias) {
        this.field = field;
        this.alias = alias;
    }

    public SqlField(String field, SqlFunc func, String alias) {
        this.field = field;
        this.func = func;
        this.alias = alias;
    }

    public String toSqlString() {
        String str = null;
        if (func != null) {
            str = func.convert(field);
        } else {
            str = "`" + FieldUtils.convertToColumnName(field) + "`";
        }
        if (StringUtils.isNotBlank(alias)) {
            return str + " as " + alias;
        } else {
            return str;
        }
    }

}
