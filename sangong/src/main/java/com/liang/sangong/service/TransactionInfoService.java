package com.liang.sangong.service;

import com.liang.dao.jdbc.common.SearchFilter.Operator;
import com.liang.dao.jdbc.common.SqlPath;
import com.liang.sangong.bo.TransactionInfo;
import com.liang.sangong.bo.TransactionInfo.TxState;
import com.liang.sangong.dao.TransactionInfoDao;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionInfoService {

  @Autowired
  private TransactionInfoDao transactionInfoDao;

  public boolean insert(TransactionInfo transactionInfo) {
    return transactionInfoDao.insert(transactionInfo);
  }

  public boolean update(TransactionInfo transactionInfo) {
    transactionInfo.setUpdateTime(System.currentTimeMillis());
    return transactionInfoDao.update(transactionInfo);
  }

  public List<TransactionInfo> queryInit() {
    return transactionInfoDao.findAll(SqlPath.where("state", Operator.EQ, TxState.init.code));
  }

}
