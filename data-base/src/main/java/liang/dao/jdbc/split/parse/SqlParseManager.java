package liang.dao.jdbc.split.parse;

import liang.dao.jdbc.split.SqlConstants.DML;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liangzhiyan on 2017/6/23.
 */
public class SqlParseManager {

    private static final Map<DML, SqlParse> sqlParseMap = new HashMap<>();

    static {
        sqlParseMap.put(DML.INSERT, new InsertParse());
        sqlParseMap.put(DML.UPDATE, new UpdateParse());
        sqlParseMap.put(DML.DELETE, new DeleteParse());
        sqlParseMap.put(DML.SELECT, new SelectParse());
    }

    public static SqlParse findSqlParse(DML dml) {
        return sqlParseMap.get(dml);
    }
}
