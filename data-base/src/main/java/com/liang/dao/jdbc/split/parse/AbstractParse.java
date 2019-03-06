package com.liang.dao.jdbc.split.parse;

import static com.liang.dao.jdbc.split.common.SqlCommonUtil.allTablesForDeleteAndUPdate;
import static com.liang.dao.jdbc.split.common.SqlCommonUtil.generateRealSql;
import static com.liang.dao.jdbc.split.common.SqlCommonUtil.getTableIndex;
import static com.liang.dao.jdbc.split.common.SqlCommonUtil.isHaveWhereCondition;
import static com.liang.dao.jdbc.split.common.SqlCommonUtil.whereConditionContains;

import com.alibaba.fastjson.JSON;
import com.liang.common.exception.ParameterException;
import com.liang.dao.jdbc.common.Sql;
import com.liang.dao.jdbc.split.ParseSql;
import com.liang.dao.jdbc.split.Rule;
import com.liang.dao.jdbc.split.SplitConfig;
import com.liang.dao.jdbc.split.TableRule;
import com.liang.dao.jdbc.split.common.CommonBaseConfig;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by liangzhiyan on 2017/6/23.
 */
public abstract class AbstractParse implements SqlParse {

    private ParseSql parseSql;

    private SplitConfig splitConfig;

    @Override
    public void parse(Sql sql, String sqlStr, String table, TableRule tableRule, List<Sql> sqlList) {
        if (isHaveWhereCondition(sqlStr)) {
            parseHaveWhere(sql, sqlStr, table, tableRule, sqlList);
        } else {
            parseNotHaveWhere(sql, sqlStr, table, tableRule, sqlList);
        }
    }

    private void parseHaveWhere(Sql sql, String sqlStr, String table, TableRule tableRule, List<Sql> sqlList) {
        Set<String> paramSet = new HashSet<>();
        boolean match = true;
        int matchCount = 0;
        Set<String> unExistSet = new HashSet<>();
        for (Rule rule : tableRule.getRuleList()) {
            int contains = whereConditionContains(sqlStr, rule.getColumnName());
            if (contains == -1) {
                match = false;
                unExistSet.add(rule.getColumnName());
            } else {
                int tableIndex = getTableIndex(sql, rule, contains);
                Sql sqlClone = sql.clone();
                sqlClone.setSplitColumn(rule.getColumnName());
                generateRealSql(sqlClone, sqlStr, table, tableIndex);
                sqlList.add(sqlClone);
                ++matchCount;
            }
        }
        if (!match) {
            if (matchCount == 0) {//全部不匹配
                throwEx();
                allTablesForDeleteAndUPdate(sql, sqlStr, table, tableRule, sqlList);
            } else {
                sqlList.clear();
                Sql clone = sql.clone();
                String rawSql = sql.getRawSql();
                String whereSql = rawSql.substring(StringUtils.indexOfIgnoreCase(rawSql, " where "));
                int count = StringUtils.countMatches(whereSql, "?");
                count = clone.getParams().size() - count;
                for (int i = 0; i < count; i++) {
                    clone.getParams().remove(0);
                }
                rawSql = "select count(1) as count from " + table + " " + whereSql;
                clone.setSql(rawSql);
                long queryForSplitCount = parseSql.executeQueryForSplitCount(clone);
                if (queryForSplitCount * tableRule.getRuleList().size() >= tableRule.getIndexSet().size()) {//如果通过条件查询出的数量大于分表的所有的表的数量，那么通过走全表
                    allTablesForDeleteAndUPdate(sql, sqlStr, table, tableRule, sqlList);
                } else {
                    rawSql = "select * from " + table + " " + whereSql;
                    clone.setSql(rawSql);
                    List<Map<String, Object>> mapList = parseSql.executeQueryForSplit(clone);
                    rawSql = sql.getRawSql().replace(";", "");
                    for (Map<String, Object> map : mapList) {
                        Sql cloneSql = sql.clone();
                        String tmpSql = rawSql;
                        for (String field : unExistSet) {
                            tmpSql += " and " + field + " = ?";
                            cloneSql.addParam(map.get(field));
                        }
                        if (!paramSet.add(JSON.toJSONString(cloneSql.getParams()))) {//去掉重复的值
                            continue;
                        }
                        cloneSql.setSql(tmpSql);
                        List<Sql> midSqlList = parseSql.parseSql(cloneSql);
                        if (CollectionUtils.isEmpty(midSqlList)) {
                            sqlList.add(cloneSql);
                        } else {
                            sqlList.addAll(midSqlList);
                        }
                    }
                }
            }
        }
    }

    private void parseNotHaveWhere(Sql sql, String sqlStr, String table, TableRule tableRule, List<Sql> sqlList) {
        throwEx();
        allTablesForDeleteAndUPdate(sql, sqlStr, table, tableRule, sqlList);
    }

    protected void throwEx() {
        if (CommonBaseConfig.isIsThrowEx()) {
            throw ParameterException.throwException("sql语句必须包含分表维度");
        }
    }

    @Override
    public SqlParse setParseSql(ParseSql parseSql) {
        this.parseSql = parseSql;
        return this;
    }

    @Override
    public SqlParse setSplitConfig(SplitConfig splitConfig) {
        this.splitConfig = splitConfig;
        return this;
    }
}
