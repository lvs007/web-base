package liang.dao.jdbc.split.service;

import liang.common.exception.ParameterException;
import liang.dao.jdbc.common.Sql;
import liang.dao.jdbc.impl.AbstractDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by liangzhiyan on 2017/6/26.
 */
public abstract class DataMoveToNewTablesService<T> extends AbstractDao<T> {

    private static final Logger LOG = LoggerFactory.getLogger(DataMoveToNewTablesService.class);

    public void move(String oldTable, Class clazz, long limitId) {
        move(oldTable, clazz, limitId, 10000);
    }

    public void move(String oldTable, Class clazz, long limitId, int queryNumber) {
        long begin = limitId - queryNumber;
        long end = limitId;
        while (end > 0) {
            copyData(oldTable, clazz, begin, end);
            end = begin - 1;
            begin = begin - queryNumber;
            LOG.info("limitId:{}", end);
        }
    }

    public void move(String oldTable, Class clazz, long start, long finish, int queryNumber) {
        if (start > finish) {
            throw ParameterException.throwException("start 不能大于 finish");
        }
        long begin = finish - queryNumber;
        long end = finish;
        if (begin < 0) {
            begin = 0;
        }
        while (end >= start) {
            copyData(oldTable, clazz, begin, end);
            end = begin - 1;
            begin = begin - queryNumber;
            if (begin < start) {
                begin = start;
            }
        }
    }

    private void copyData(String oldTable, Class clazz, long begin, long end) {
        String sqlStr = "select * from " + oldTable + " where id between " + begin + " and " + end;
        Sql sql = new Sql(sqlStr);
        sql.setBypass(true);
        List<T> list = this.listBySql(sql, clazz);
        insertBatch(list);
    }

}
