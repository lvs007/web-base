package com.day.dao.impl;

import com.day.bo.NFTDesc;
import com.day.dao.UserDao;
import com.liang.dao.jdbc.common.Sql;
import com.liang.dao.jdbc.impl.AbstractDao;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class UserDaoImpl extends AbstractDao<NFTDesc> implements UserDao {
}
