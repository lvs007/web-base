package com.liang.dao.jdbc.split.parse;

import com.liang.dao.jdbc.common.Sql;

import com.liang.dao.jdbc.split.ParseSql;
import com.liang.dao.jdbc.split.SplitConfig;
import com.liang.dao.jdbc.split.TableRule;
import java.util.List;

/**
 * Created by liangzhiyan on 2017/6/23.
 */
public interface SqlParse {

    void parse(Sql sql, String sqlStr, String table, TableRule tableRule, List<Sql> sqlList);

    SqlParse setParseSql(ParseSql parseSql);

    SqlParse setSplitConfig(SplitConfig splitConfig);
}
