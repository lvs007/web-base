package com.liang.sangong.dao;

import com.liang.dao.jdbc.BaseDao;
import com.liang.sangong.bo.PeopleInfo;

public interface UserDao extends BaseDao<PeopleInfo> {

  boolean updateByUserIdAndType(long userId, int type, String address);
}
