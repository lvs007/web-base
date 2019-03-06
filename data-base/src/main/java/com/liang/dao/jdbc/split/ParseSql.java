package com.liang.dao.jdbc.split;

import com.liang.common.exception.NotSupportException;
import com.liang.dao.jdbc.annotation.DataSource;
import com.liang.dao.jdbc.common.Sql;
import com.liang.dao.jdbc.split.common.LogUtil;
import com.liang.dao.jdbc.split.common.SqlCommonUtil;
import com.liang.dao.jdbc.split.listener.FailListener;
import com.liang.dao.jdbc.split.parse.SqlParseManager;
import com.liang.dao.jdbc.split.db.DBIndexHelper;
import com.liang.dao.jdbc.split.db.DbConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;

/**
 * Created by liangzhiyan on 2017/6/9.
 */
public abstract class ParseSql {

    private static final Logger LOG = LoggerFactory.getLogger(ParseSql.class);

    private Map<String, TableRule> tableRuleMap;

    protected ThreadPoolTaskExecutor splitTableExecutor;

    @Autowired
    protected SplitConfig splitConfig;

    @Resource(name = "defaultFailListener")
    protected FailListener defaultFailListener;

    @Autowired(required = false)
    @Qualifier("failListener")
    protected FailListener failListener;

    @PostConstruct
    private void init() {
        tableRuleMap = splitConfig.getTableRuleMap();
        splitTableExecutor = splitConfig.getSplitTableExecutor();
    }

    public boolean isSelect(Sql sql) {
        return StringUtils.startsWithIgnoreCase(sql.getRawSql().trim(), "select");
    }

    public boolean isSplitTable() {
        return splitConfig.isSplitTable();
    }

    protected boolean isSplitTable(String table) {
        if (StringUtils.isBlank(table)) {
            return false;
        }
        TableRule tableRule = tableRuleMap.get(table);
        if (tableRule == null) {//不分表
            return false;
        } else {
            return true;
        }
    }

    public List<Sql> parseSql(Sql sql) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        String sqlStr = sql.getRawSql().trim();
        String table = SqlCommonUtil.getTable(sql);
        TableRule tableRule = tableRuleMap.get(table);
        if (tableRule == null) {//不分表
            return Collections.emptyList();
        }
        List<Sql> sqlList = new ArrayList<>();
        SqlConstants.DML dml = SqlConstants.DML.getDML(sqlStr);
        if (dml == null) {
            throw NotSupportException.throwException("只支持简单的select、insert、delete、update");
        }
        sql.setDml(dml);
        SqlParseManager
            .findSqlParse(dml).setParseSql(this).setSplitConfig(splitConfig).parse(sql, sqlStr, table, tableRule, sqlList);
        stopWatch.stop();
        LogUtil.info("find the right table spend time : {} ms", stopWatch.getTime());
        return sqlList;
    }

    protected FailListener selectListener(Sql sql) {
        SqlConstants.DML dml = SqlConstants.DML.getDML(sql.getRawSql());
        if (!isSplitTable(sql.getTablePre()) || dml == null || dml == SqlConstants.DML.SELECT) {
            return null;
        }
        if (failListener == null) {
            return defaultFailListener;
        } else {
            return failListener;
        }
    }

    protected void setDataSourceLookUp(Sql sql) {
        if (DbConfig.isIsSplitDb()) {
            DbConfig.TableDbConfig tableDbConfig = DbConfig.getTableDbConfigMap().get(sql.getTablePre());
            if (tableDbConfig != null) {
                String db = tableDbConfig.getTableIndexInDbMap().get(sql.getIndex());
                if (StringUtils.isNotBlank(db)) {
                    DBIndexHelper.setDbIndex(db);
                } else {
                    setDataSourceFromAnnotation();
                }
            } else {
                setDataSourceFromAnnotation();
            }
        } else {
            setDataSourceFromAnnotation();
        }
    }

    private boolean setDataSourceFromAnnotation() {
        Class clazz = getClass();
        DataSource annotation = (DataSource) clazz.getAnnotation(DataSource.class);
        if (annotation != null) {
            DBIndexHelper.setDbIndex(annotation.name());
            return true;
        }
        return false;
    }

    public abstract List<Map<String, Object>> executeQueryForSplit(Sql sql);

    public abstract long executeQueryForSplitCount(Sql sql);

}