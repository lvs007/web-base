package com.liang.sangong.dao;

import com.liang.dao.jdbc.BaseDao;
import com.liang.sangong.bo.DataStatistics;

public interface DataStatisticsDao extends BaseDao<DataStatistics> {

  boolean updateData(long userId, int type, long amount, boolean shuOrYing);
}
