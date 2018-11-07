package liang.dao.jdbc.split.db;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 表跟数据库的关系
 * Created by liangzhiyan on 2017/7/20.
 */
public class DbConfig {

    private static boolean isSplitDb = false;

    private static final Map<String, TableDbConfig> tableDbConfigMap = new HashMap<>();

    public static boolean isIsSplitDb() {
        return isSplitDb;
    }

    public static void setIsSplitDb(boolean isSplitDb) {
        DbConfig.isSplitDb = isSplitDb;
    }

    public static Map<String, TableDbConfig> getTableDbConfigMap() {
        return tableDbConfigMap;
    }

    public static void setTableDbRule(String table, String dbRule) {
        if (tableDbConfigMap.containsKey(table)) {
            tableDbConfigMap.get(table).setDbRule(dbRule);
        } else {
            TableDbConfig tableDbConfig = new TableDbConfig();
            tableDbConfig.setDbRule(dbRule);
            tableDbConfigMap.put(table, tableDbConfig);
        }
    }

    public static class TableDbConfig {

        private static final String DB_SPLIT = ",";
        private static final String DB_INDEX = ":";
        private static final String INDEX = "-";

        private boolean isSplitDb = false;
        private Map<String, Set<Integer>> dbWithTableIndexMap = new HashMap<>();
        private Map<Integer, String> tableIndexInDbMap = new HashMap<>();

        public Map<String, Set<Integer>> getDbWithTableIndexMap() {
            return dbWithTableIndexMap;
        }

        public Map<Integer, String> getTableIndexInDbMap() {
            return tableIndexInDbMap;
        }

        public boolean isIsSplitDb() {
            return isSplitDb;
        }

        public void setIsSplitDb(boolean isSplitDb) {
            this.isSplitDb = isSplitDb;
        }

        /**
         * db1:[0-3],db2:[4-6],db3:[7-9]
         *
         * @param dbRule
         */
        public void setDbRule(String dbRule) {
            if (StringUtils.isBlank(dbRule)) {
                return;
            }
            String[] dbTables = StringUtils.split(dbRule, DB_SPLIT);
            for (String dbTable : dbTables) {
                String[] dbIndexs = StringUtils.split(dbTable, DB_INDEX);
                String indexStr = dbIndexs[1].substring(1, dbIndexs[1].length() - 1);
                String[] indexs = StringUtils.split(indexStr, INDEX);
                String db = dbIndexs[0];
                int begin = Integer.parseInt(indexs[0]);
                int end = Integer.parseInt(indexs[1]);
                for (int i = begin; i <= end; i++) {
                    tableIndexInDbMap.put(i, db);
                    if (dbWithTableIndexMap.containsKey(db)) {
                        dbWithTableIndexMap.get(db).add(i);
                    } else {
                        Set<Integer> indexSet = new HashSet<>();
                        indexSet.add(i);
                        dbWithTableIndexMap.put(db, indexSet);
                    }
                }
            }
        }
    }
}
