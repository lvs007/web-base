package liang.dao.jdbc.split.listener;

import liang.common.util.ReflectUtils;
import liang.dao.jdbc.common.Sql;
import liang.dao.jdbc.impl.AbstractDao;
import liang.dao.jdbc.split.Rule;
import liang.dao.jdbc.split.SplitConfig;
import liang.dao.jdbc.split.SqlConstants;
import liang.dao.jdbc.split.SqlConstants.DML;
import liang.dao.jdbc.split.TableRule;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by liangzhiyan on 2017/7/11.
 */
public abstract class AbstractFailListener<T> extends AbstractDao<T> implements FailListener {

    private static final String UPDATE_TIME = "updateTime";
    private static final String ID = "id";

    public List<T> listExist(Sql sql) {
        String sqlStr = "select * from " + sql.getRealTable() + " where ";
        Sql sqlSelect = new Sql(sqlStr);
        sqlSelect.setBypass(true);
        return null;
    }

    @Override
    public void doSomething(Sql sql) {
        sql.setBypass(true);
        if (sql.getDml() == DML.INSERT) {
            try {
                insertFail(sql);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if (sql.getDml() == DML.UPDATE) {
            this.executeUpdate(sql);
        } else if (sql.getDml() == DML.DELETE) {
            this.executeUpdate(sql);
        }
    }

    private void insertFail(Sql sql) throws NoSuchFieldException, IllegalAccessException {
        TableRule tableRule = splitConfig.getTableRuleMap().get(sql.getTablePre());
        if (CollectionUtils.isNotEmpty(sql.getBatchList())) {
            //批量插入出错
            for (List<Object> params : sql.getBatchList()) {
                Sql cloneSql = sql.clone();
                cloneSql.setBypass(true);
                cloneSql.getBatchList().clear();
                cloneSql.addAllParams(params);
                singleSql(cloneSql);
            }
        } else {
            singleSql(sql);
        }
    }

    private void singleSql(Sql sql) throws NoSuchFieldException, IllegalAccessException {
        Object id = findValue(ID, sql, sql.getParams());
        T t = findFromOldTable(sql, (Long) id);
        if (t == null) {//不存在老表数据
            List<T> existList = listFromSplitTable(sql, id);
            if (CollectionUtils.isEmpty(existList)) {
                this.executeUpdate(sql);
            } else {
                t = findMaxOldUpdateTime(existList);
                Object oldUpdateTime = ReflectUtils.getValue(t, UPDATE_TIME);
                executeInsertFail(sql, t, oldUpdateTime);
            }
        } else {//存在老表数据
            Object oldUpdateTime = ReflectUtils.getValue(t, UPDATE_TIME);
            executeInsertFail(sql, t, oldUpdateTime);
        }
    }

    private void executeInsertFail(Sql sql, T t, Object oldUpdateTime) {
        Object failUpdateTime = findValue(UPDATE_TIME, sql, sql.getParams());
        if (oldUpdateTime instanceof Date) {
            Date odate = (Date) oldUpdateTime;
            Date fdate = (Date) failUpdateTime;
            if (odate.compareTo(fdate) <= 0) {
                this.executeUpdate(sql);
            } else {
                executeInsertOld(sql, t);
            }
        } else if (oldUpdateTime instanceof Timestamp) {
            Timestamp otime = (Timestamp) oldUpdateTime;
            Timestamp ftime = (Timestamp) failUpdateTime;
            if (otime.compareTo(ftime) <= 0) {
                this.executeUpdate(sql);
            } else {
                executeInsertOld(sql, t);
            }
        } else {
            long outime = Long.parseLong(oldUpdateTime.toString());
            long futime = Long.parseLong(failUpdateTime.toString());
            if (outime <= futime) {
                this.executeUpdate(sql);
            } else {
                executeInsertOld(sql, t);
            }
        }
    }

    private T findMaxOldUpdateTime(List<T> existList) throws NoSuchFieldException, IllegalAccessException {
        Object oldUpdateTime = null;
        int i = 1;
        T t = null;
        for (T et : existList) {
            if (i == 1) {
                oldUpdateTime = ReflectUtils.getValue(et, UPDATE_TIME);
                t = et;
            } else {
                Object value = ReflectUtils.getValue(et, UPDATE_TIME);
                if (oldUpdateTime instanceof Date) {
                    Date odate = (Date) oldUpdateTime;
                    Date date = (Date) value;
                    if (odate.compareTo(date) < 0) {
                        oldUpdateTime = value;
                        t = et;
                    }
                } else if (oldUpdateTime instanceof Timestamp) {
                    Timestamp otime = (Timestamp) oldUpdateTime;
                    Timestamp time = (Timestamp) value;
                    if (otime.compareTo(time) < 0) {
                        oldUpdateTime = value;
                        t = et;
                    }
                } else {
                    long outime = Long.parseLong(oldUpdateTime.toString());
                    long utime = Long.parseLong(value.toString());
                    if (outime < utime) {
                        oldUpdateTime = value;
                        t = et;
                    }
                }
            }
            ++i;
        }
        return t;
    }

    private void executeInsertOld(Sql sql, T t) {
        Sql newSql = getBuilder().getInsertSql();
        newSql.addAllParams(getBuilder().buildInsertParams(t));
        newSql.setSql(newSql.getRawSql().replace(sql.getTablePre(), sql.getRealTable()));
        newSql.setBypass(true);
        this.executeUpdate(newSql);
    }

    private void executeUpdateOld(Sql sql, T t) throws NoSuchFieldException, IllegalAccessException {
        String sqlStr = sql.getRawSql();
        String sqlSet = sqlStr.substring(sqlStr.indexOf(" set ") + 5, sqlStr.indexOf(" where "));
        String[] columnArrays = StringUtils.split(",");
        List<Object> params = sql.getParams();
        List<Object> newParams = new ArrayList<>();
        for (String columnExpress : columnArrays) {
            String column = columnExpress.substring(0, columnExpress.indexOf("=")).trim();
            Object value = ReflectUtils.getValue(t, column);
            newParams.add(value);
            params.remove(0);
        }
        for (Object obj : params) {
            newParams.add(obj);
        }
        sql.getParams().clear();
        sql.addAllParams(newParams);
        sql.setBypass(true);
        this.executeUpdate(sql);
    }

    private List<T> listFromSplitTable(Sql sql, Object id) {
        TableRule tableRule = splitConfig.getTableRuleMap().get(sql.getTablePre());
        List<T> result = new ArrayList<>();
        for (Rule rule : tableRule.getRuleList()) {
            Object value = findValue(rule.getColumnName(), sql, sql.getParams());
            String sqlStr = "select * from " + sql.getTablePre() + " where id = ? and " + rule.getColumnName() + "=?";
            Sql querySql = new Sql(sqlStr);
            querySql.addParam(id);
            querySql.addParam(value);
            result.addAll(this.listBySql(sql));
        }
        return result;
    }

    private T findFromOldTable(Sql sql, long id) {
        try {
            String sqlStr = "select * from " + sql.getTablePre() + " where id = ?";
            Sql sqlSelect = new Sql(sqlStr);
            sqlSelect.addParam(id);
            sqlSelect.setBypass(true);
            return this.findBySql(sqlSelect);
        } catch (Throwable e) {
        }
        return null;
    }

    private Object findValue(String columnName, Sql sql, List<Object> params) {
        String sqlStr = sql.getRawSql();
        if (sql.getDml() == DML.INSERT) {
            sqlStr = sqlStr.substring(sqlStr.indexOf("(") + 1, sqlStr.indexOf(")"));
            String[] columnArray = StringUtils.split(sqlStr, ",");
            for (int i = 0; i < columnArray.length; i++) {
                if (columnArray[i].equals(columnName)) {
                    return params.get(i);
                }
            }
        } else if (sql.getDml() == DML.UPDATE) {

        }
        return -1;
    }
}
