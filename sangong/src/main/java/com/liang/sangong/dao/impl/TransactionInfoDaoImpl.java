package com.liang.sangong.dao.impl;

import com.liang.dao.jdbc.impl.AbstractDao;
import com.liang.sangong.bo.TransactionInfo;
import com.liang.sangong.dao.TransactionInfoDao;
import org.springframework.stereotype.Repository;

@Repository
public class TransactionInfoDaoImpl extends AbstractDao<TransactionInfo> implements
    TransactionInfoDao {

}
