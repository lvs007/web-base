package com.liang.dao.impl;

import com.liang.bo.PeopleInfo;
import com.liang.dao.UserDao;
import liang.dao.jdbc.impl.AbstractDao;
import org.springframework.stereotype.Repository;

@Repository
public class UserDaoImpl extends AbstractDao<PeopleInfo> implements UserDao {

}
