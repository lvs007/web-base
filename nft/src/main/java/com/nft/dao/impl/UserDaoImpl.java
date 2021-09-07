package com.nft.dao.impl;

import com.liang.dao.jdbc.impl.AbstractDao;
import com.nft.bo.NFTDesc;
import com.nft.dao.UserDao;
import org.springframework.stereotype.Repository;

@Repository
public class UserDaoImpl extends AbstractDao<NFTDesc> implements UserDao {
}
