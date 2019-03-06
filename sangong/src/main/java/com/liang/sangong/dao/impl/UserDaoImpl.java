package com.liang.sangong.dao.impl;

import com.liang.dao.jdbc.common.Sql;
import com.liang.dao.jdbc.impl.AbstractDao;
import com.liang.sangong.bo.PeopleInfo;
import com.liang.sangong.dao.UserDao;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class UserDaoImpl extends AbstractDao<PeopleInfo> implements UserDao {

  @Override
  public boolean updateByUserIdAndType(long userId, int type, String address) {
    Sql sql = new Sql(
        "update " + getTableName() + " set address = ? where userId = ? and type = ?");
    List<Object> params = new ArrayList<>();
    params.add(address);
    params.add(userId);
    params.add(type);
    sql.addAllParams(params);
    return this.executeUpdate(sql) > 0;
  }
}
