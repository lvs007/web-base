package com.liang.dao.jdbc.split.common;

/**
 * Created by liangzhiyan on 2017/6/30.
 */
public class SqlContext {
    private static final ThreadLocal<SqlParseObject> sqlParseObjLocal = new ThreadLocal<>();

    public static void setSqlParseObject(SqlParseObject sqlParseObject) {
        sqlParseObjLocal.set(sqlParseObject);
    }

    public static SqlParseObject getSqlParseObject() {
        return sqlParseObjLocal.get();
    }
}
