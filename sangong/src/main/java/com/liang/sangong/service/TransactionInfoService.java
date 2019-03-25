package com.liang.sangong.service;

import com.liang.sangong.bo.TransactionInfo;
import com.liang.sangong.dao.TransactionInfoDao;
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
    transactionInfo.setUpdate_time(System.currentTimeMillis());
    return transactionInfoDao.update(transactionInfo);
  }

}
