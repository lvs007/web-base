package liang.dao.jdbc.split.common;

import liang.common.exception.NotSupportException;
import liang.common.exception.ParameterException;
import liang.dao.jdbc.common.Sql;
import liang.dao.jdbc.split.Rule;
import liang.dao.jdbc.split.TableRule;
import org.apache.commons.lang3.StringUtils;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liangzhiyan on 2017/6/22.
 */
public class SqlCommonUtil {
    //每次生成一个engine实例
    private static final ScriptEngine engine = new ScriptEngineManager().getEngineByName("js");

    private static SplitTableMethod splitTableMethod;

    public static void setSplitTableMethod(SplitTableMethod splitTableMethod) {
        SqlCommonUtil.splitTableMethod = splitTableMethod;
    }

    /**
     * 获取对应表的下标
     *
     * @param sql
     * @param rule
     * @param index column所在list中的下标
     * @return
     */
    public static int getTableIndex(Sql sql, Rule rule, int index) {
        Object value = sql.getParams().get(index);
        return getTableIndex(sql, rule, value);
    }

    public static int getTableBatchIndex(Sql sql, Rule rule, int index, List<Object> params) {
        return getTableIndex(sql, rule, params.get(index));
    }

    private static int getTableIndex(Sql sql, Rule rule, Object value) {
        int tableIndex;
        if (splitTableMethod != null) {
            tableIndex = splitTableMethod.getTableIndex(rule.getColumnName(), value);
        } else if (StringUtils.isNotBlank(rule.getRuleType())) {
            if (StringUtils.equalsIgnoreCase(rule.getRuleType(), "hashCode")) {
                tableIndex = value.hashCode() % rule.getModValue() + rule.getAddValue();
            } else if (StringUtils.equalsIgnoreCase(rule.getRuleType(), "value")) {
                tableIndex = (int) (Long.parseLong(value.toString()) % rule.getModValue() + rule.getAddValue());
            } else {
                throw NotSupportException.throwException("不支持的ruleType：" + rule.getRuleType());
            }
        } else {
            try {
                Bindings binding = engine.createBindings();
                binding.put(rule.getColumnName(), value);
                Object eval = engine.eval(rule.getRule(), binding);

                if (eval instanceof Double) {
                    tableIndex = ((Double) eval).intValue();
                } else if (eval instanceof Float) {
                    tableIndex = ((Float) eval).intValue();
                } else {
                    tableIndex = (int) eval;
                }
            } catch (ScriptException e) {
                throw new RuntimeException(e);
            }
        }
        return tableIndex >= 0 ? tableIndex : -tableIndex;
    }

    /**
     * 对于sql语句中必须要有where条件
     * 获取列所在sql语句中的位置（？的位置），如果列不在sql语句中，返回-1
     *
     * @param sql
     * @param columnName
     * @return
     */
    public static int whereConditionContains(String sql, String columnName) {
        int where = StringUtils.indexOfIgnoreCase(sql, " where ");
        String wherePre = sql.substring(0, where);
        int wherePreCount = StringUtils.countMatches(wherePre, "?");
        String tmp = sql.substring(where + 7);
        int index = StringUtils.indexOfIgnoreCase(tmp, " group ");
        if (index != -1) {
            tmp = tmp.substring(0, index);
        } else {
            index = StringUtils.indexOfIgnoreCase(tmp, " order ");
            if (index != -1) {
                tmp = tmp.substring(0, index);
            }
        }
        int pos = 0;
        boolean result = false;
        String[] array = StringUtils.split(tmp, " and | AND ");
        for (String comp : array) {
            comp = comp.trim().replace("`", "");
            if (StringUtils.contains(comp, "=")) {
                if (comp.startsWith(columnName + "=") || comp.startsWith(columnName + " ")) {
                    result = true;
                    break;
                }
            }
            if (comp.endsWith("?")) {
                ++pos;
            }
        }
        if (!result) {
            pos = -1;
        } else {
            pos = pos + wherePreCount;
        }
        return pos;
    }

    /**
     * 获取表名
     *
     * @param sql
     * @return
     */
    public static String getTable(Sql sql) {
        String sqlStr = sql.getRawSql().trim();
        String table;
        if (StringUtils.startsWithIgnoreCase(sqlStr, "insert")) {//insert into table values(),insert into table() values()
            int start = StringUtils.indexOfIgnoreCase(sqlStr, " into ") + 6;
            sqlStr = sqlStr.substring(start).trim();
            String[] arrays = StringUtils.split(sqlStr, " |\t");
            table = arrays[0].trim();
            if (table.endsWith("(")) {
                table = table.substring(0, table.length() - 1);
            }
        } else if (StringUtils.startsWithIgnoreCase(sqlStr, "delete")
                || StringUtils.startsWithIgnoreCase(sqlStr, "select")) {
            int start = StringUtils.indexOfIgnoreCase(sqlStr, " from ") + 6;
            sqlStr = sqlStr.substring(start).trim();
            String[] arrays = sqlStr.split(" |\t");
            table = arrays[0].trim();
        } else if (StringUtils.startsWithIgnoreCase(sqlStr, "update")) {
            int start = StringUtils.indexOfIgnoreCase(sqlStr, "update") + 6;
            sqlStr = sqlStr.substring(start).trim();
            String[] arrays = sqlStr.split(" |\t");
            table = arrays[0].trim();
        } else {
            throw NotSupportException.throwException("分表目前只支持简单的insert delete update select");
        }
        sql.setTablePre(table);
        return table;
    }

    /**
     * 只适用于insert语句，查找列在sql语句中的位置
     *
     * @param column
     * @param sql
     * @return
     */
    public static int findInsertColumnIndex(String column, String sql) {
        int start = sql.indexOf("(") + 1;
        int end = sql.indexOf(")");
        String value = sql.substring(start, end).replace("`", "").trim();
        String[] array = StringUtils.split(value, ",");
        for (int i = 0; i < array.length; i++) {
            if (StringUtils.equals(array[i].trim(), column)) {
                return i;
            }
        }
        throw ParameterException.throwException("列" + column + "在insert中不存在");
    }

    /**
     * 用真是表名替换虚拟表名
     *
     * @param sql
     * @param sqlStr
     * @param table
     * @param tableIndex
     */
    public static void generateRealSql(Sql sql, String sqlStr, String table, int tableIndex) {
        String realTableSql;
        int index = sqlStr.indexOf(" " + table + " ");
        if (index != -1) {
            realTableSql = sqlStr.replace(" " + table + " ", " " + table + tableIndex + " ");
        } else {
            realTableSql = sqlStr.replace(" " + table, " " + table + tableIndex);
        }
        sql.setIndex(tableIndex);
        sql.setRealTable(table + tableIndex);
        sql.setSql(realTableSql);
    }

    /**
     * select语句专用
     * 扫描全表（指扫描其中一个分表维度的表，取分表数量最少的维度）
     *
     * @param sql
     * @param sqlStr
     * @param table
     * @param tableRule
     * @param sqlList
     */
    public static void allTablesForSelect(Sql sql, String sqlStr, String table, TableRule tableRule, List<Sql> sqlList) {
        for (int index : tableRule.getScanIndexSet()) {
            Sql sqlClone = sql.clone();
            generateRealSql(sqlClone, sqlStr, table, index);
            sqlList.add(sqlClone);
        }
    }

    /**
     * DELETE UPDATE语句专用
     * 扫描全表
     *
     * @param sql
     * @param sqlStr
     * @param table
     * @param tableRule
     * @param sqlList
     */
    public static void allTablesForDeleteAndUPdate(Sql sql, String sqlStr, String table, TableRule tableRule, List<Sql> sqlList) {
        for (int index : tableRule.getIndexSet()) {
            Sql sqlClone = sql.clone();
            generateRealSql(sqlClone, sqlStr, table, index);
            sqlList.add(sqlClone);
        }
    }

    /**
     * 判断sql语句中是否包含where条件
     *
     * @param sql
     * @return
     */
    public static boolean isHaveWhereCondition(String sql) {
        return StringUtils.containsIgnoreCase(sql, " where ");
    }

    public static void main(String[] args) {
        Sql sql = new Sql("insert into bm_user_message_ (`id`,`receiver_id`,`receiver_type`,`publisher_id`,`msg_id`,`channel_type`,`type`,`sub_type`,`source`,`psource`,`msg_title`,`status`,`send_status`,`expiry_time`,`is_read`,`top`,`valid`,`create_time`,`update_time`) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
        Rule rule = new Rule();
        rule.setAddValue(10);
        rule.setModValue(10);
        rule.setColumnName("msg_id");
        rule.setRuleType("value");
        List<Object> list = new ArrayList<Object>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(5);
        list.add(6);
        list.add(7);
        list.add(8);
        list.add(9);
        list.add(10);
        list.add(11);
        list.add(12);
        list.add(13);
        list.add(14);
        list.add(15);
        list.add(16);
        list.add(17);
        list.add(18);
        list.add(19);
        for (int i = 0; i < 10000; i++) {
            sql.getBatchList().add(list);
        }
        int index = 4;
        long begin = System.currentTimeMillis();
        Map<Integer, Sql> sqlMap = new HashMap<>();
        for (List<Object> entity : sql.getBatchList()) {
            int tableIndex = getTableBatchIndex(sql, rule, index, entity);
            Sql midSql = sqlMap.get(tableIndex);
            if (midSql == null) {
                midSql = sql.clone();
                midSql.cleanBatchList();
                generateRealSql(midSql, sql.getRawSql(), "bm_user_message_", tableIndex);
                sqlMap.put(tableIndex, midSql);
            }
            midSql.getBatchList().add(entity);
        }
        System.out.printf("spend:" + (System.currentTimeMillis() - begin));
    }
}
