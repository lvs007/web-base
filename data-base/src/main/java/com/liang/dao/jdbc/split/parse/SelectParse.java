package com.liang.dao.jdbc.split.parse;


import com.liang.dao.jdbc.common.Sql;

import com.liang.dao.jdbc.split.Rule;
import com.liang.dao.jdbc.split.TableRule;
import java.util.List;

import static com.liang.dao.jdbc.split.common.SqlCommonUtil.*;

/**
 * Created by liangzhiyan on 2017/6/22.
 */
public class SelectParse extends AbstractParse {

    @Override
    public void parse(Sql sql, String sqlStr, String table, TableRule tableRule, List<Sql> sqlList) {
        if (isHaveWhereCondition(sqlStr)) {
            parseHaveWhere(sql, sqlStr, table, tableRule, sqlList);
        } else {
            parseNotHaveWhere(sql, sqlStr, table, tableRule, sqlList);
        }
    }

    private void parseHaveWhere(Sql sql, String sqlStr, String table, TableRule tableRule, List<Sql> sqlList) {
        int notMatchCount = 0;
        for (Rule rule : tableRule.getRuleList()) {
            int contains = whereConditionContains(sqlStr, rule.getColumnName());
            if (contains != -1) {
                int tableIndex = getTableIndex(sql, rule, contains);
                generateRealSql(sql, sqlStr, table, tableIndex);
                sql.setSplitColumn(rule.getColumnName());
                break;
            } else {
                ++notMatchCount;
            }
        }
        if (notMatchCount == tableRule.getRuleList().size()) {
            throwEx();
            allTablesForSelect(sql, sqlStr, table, tableRule, sqlList);
        }
    }

    private void parseNotHaveWhere(Sql sql, String sqlStr, String table, TableRule tableRule, List<Sql> sqlList) {
        throwEx();
        allTablesForSelect(sql, sqlStr, table, tableRule, sqlList);
    }
}
