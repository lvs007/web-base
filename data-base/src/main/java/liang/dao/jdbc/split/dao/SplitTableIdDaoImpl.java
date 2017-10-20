package liang.dao.jdbc.split.dao;

import liang.dao.jdbc.BaseDao;
import liang.dao.jdbc.common.Sql;
import liang.dao.jdbc.impl.AbstractDao;
import liang.dao.jdbc.split.entity.BmSplitTableId;
import org.springframework.stereotype.Repository;

/**
 * Created by liangzhiyan on 2017/6/21.
 */
@Repository
public class SplitTableIdDaoImpl extends AbstractDao<BmSplitTableId> implements BaseDao<BmSplitTableId> {

    public void deleteByLtId(long id) {
        String sqlStr = "delete from " + getTableName() + " where id <= ?";
        Sql sql = new Sql(sqlStr);
        sql.addParam(id);
        this.executeUpdate(sql);
    }
}
