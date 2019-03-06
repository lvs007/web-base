package com.liang.dao.jdbc.split.db;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by liangzhiyan on 2017/6/27.
 */
public class DBIndexHelper {

    private static final ThreadLocal<String> dbIndexLocal = new ThreadLocal<>();

    public static void setDbIndex(String index) {
        dbIndexLocal.set(index);
    }

    public static String getDbIndex() {
        return dbIndexLocal.get();
    }

    public static void setDataSource(String table, int idx) {
        if (DbConfig.isIsSplitDb()) {
            DbConfig.TableDbConfig tableDbConfig = DbConfig.getTableDbConfigMap().get(table);
            if (tableDbConfig != null) {
                String db = tableDbConfig.getTableIndexInDbMap().get(idx);
                if (StringUtils.isNotBlank(db)) {
                    DBIndexHelper.setDbIndex(db);
                }
            }
        }
    }

    public static void clean() {
        dbIndexLocal.remove();
    }
}
