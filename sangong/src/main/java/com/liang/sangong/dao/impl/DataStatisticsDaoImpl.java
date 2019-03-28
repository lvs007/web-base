package com.liang.sangong.dao.impl;

import com.liang.dao.jdbc.common.Sql;
import com.liang.dao.jdbc.impl.AbstractDao;
import com.liang.sangong.bo.DataStatistics;
import com.liang.sangong.dao.DataStatisticsDao;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class DataStatisticsDaoImpl extends AbstractDao<DataStatistics> implements
    DataStatisticsDao {

  @Override
  public boolean updateData(long userId, int type, long amount, boolean shuOrYing) {
    StringBuilder sb = new StringBuilder("update " + getTableName() + " set ");
    if (shuOrYing) {
      sb.append(" win_amount = win_amount + ? , win_count = win_count + 1 , update_time = ? ");
    } else {
      sb.append(" shu_amount = shu_amount + ? , shu_count = shu_count + 1 , update_time = ? ");
    }
    sb.append(" where user_id = ? and type = ?");
    Sql sql = new Sql(sb.toString());
    List<Object> list = new ArrayList<>();
    list.add(amount);
    list.add(System.currentTimeMillis());
    list.add(userId);
    list.add(type);
    sql.addAllParams(list);
    return this.executeUpdate(sql) == 1;
  }
}
