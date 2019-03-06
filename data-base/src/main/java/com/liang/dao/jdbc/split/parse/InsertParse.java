package com.liang.dao.jdbc.split.parse;

import static com.liang.dao.jdbc.split.common.SqlCommonUtil.findInsertColumnIndex;
import static com.liang.dao.jdbc.split.common.SqlCommonUtil.generateRealSql;
import static com.liang.dao.jdbc.split.common.SqlCommonUtil.getTableBatchIndex;
import static com.liang.dao.jdbc.split.common.SqlCommonUtil.getTableIndex;
import static com.liang.dao.jdbc.split.common.SqlCommonUtil.isHaveWhereCondition;

import com.liang.common.exception.NotSupportException;
import com.liang.dao.jdbc.common.Sql;
import com.liang.dao.jdbc.split.Rule;
import com.liang.dao.jdbc.split.TableRule;
import com.liang.dao.jdbc.split.common.LogUtil;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liangzhiyan on 2017/6/22.
 */
public class InsertParse extends AbstractParse {

    @Override
    public void parse(Sql sql, String sqlStr, String table, TableRule tableRule, List<Sql> sqlList) {
        if (isHaveWhereCondition(sqlStr)) {
            parseHaveWhere(sql, sqlStr, table, tableRule, sqlList);
        } else {
            parseNotHaveWhere(sql, sqlStr, table, tableRule, sqlList);
        }
    }

    private void parseHaveWhere(Sql sql, String sqlStr, String table, TableRule tableRule, List<Sql> sqlList) {
        throw NotSupportException.throwException("不支持的操作，insert语句不能带where条件");
    }

    private void parseNotHaveWhere(Sql sql, String sqlStr, String table, TableRule tableRule, List<Sql> sqlList) {
        for (Rule rule : tableRule.getRuleList()) {
            int index = findInsertColumnIndex(rule.getColumnName(), sqlStr);
            if (isInsertBatch(sql)) {
                Map<Integer, Sql> sqlMap = new HashMap<>();
                long begin = System.currentTimeMillis();
                for (List<Object> entity : sql.getBatchList()) {
                    int tableIndex = getTableBatchIndex(sql, rule, index, entity);
                    Sql midSql = sqlMap.get(tableIndex);
                    if (midSql == null) {
                        midSql = sql.clone();
                        midSql.cleanBatchList();
                        generateRealSql(midSql, sqlStr, table, tableIndex);
                        sqlMap.put(tableIndex, midSql);
                    }
                    midSql.setSplitColumn(rule.getColumnName());
                    midSql.getBatchList().add(entity);
                }
                sqlList.addAll(sqlMap.values());
                LogUtil.info("batch insert parse spend time :{} ms", (System.currentTimeMillis() - begin));
            } else {
                int tableIndex = getTableIndex(sql, rule, index);
                Sql sqlClone = sql.clone();
                sqlClone.setSplitColumn(rule.getColumnName());
                generateRealSql(sqlClone, sqlStr, table, tableIndex);
                sqlList.add(sqlClone);
            }
        }
    }

    private boolean isInsertBatch(Sql sql) {
        if (CollectionUtils.isNotEmpty(sql.getBatchList())) {
            return true;
        } else {
            return false;
        }
    }
}
