package com.liang.sangong.service;

import com.liang.common.util.LockUtils;
import com.liang.dao.jdbc.common.SearchFilter.Operator;
import com.liang.dao.jdbc.common.SqlPath;
import com.liang.mvc.filter.UserInfo;
import com.liang.sangong.bo.PeopleInfo;
import com.liang.sangong.bo.PeopleInfo.PeopleType;
import com.liang.sangong.bo.PeopleInfo.UserState;
import com.liang.sangong.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  @Autowired
  private UserDao userDao;

  public void setPeopleInfo(UserInfo userInfo) {
    synchronized (LockUtils.get(String.valueOf(userInfo.getId()))) {
      PeopleInfo peopleInfo = findUser(userInfo.getId(), PeopleType.TRX.code);
      if (peopleInfo == null) {
        peopleInfo = new PeopleInfo();
        peopleInfo.setUserId(userInfo.getId());
        peopleInfo.setName(userInfo.getUserName());
        peopleInfo.setCreate_time(System.currentTimeMillis());
        peopleInfo.setUpdate_time(System.currentTimeMillis());
        peopleInfo.setState(UserState.ABLE.code);
        peopleInfo.setType(PeopleType.TRX.code);
        userDao.insert(peopleInfo);
      }
    }
  }

  public boolean setAddress(UserInfo userInfo, String address, PeopleType type) {
    synchronized (LockUtils.get(String.valueOf(userInfo.getId()))) {
      PeopleInfo peopleInfo = findUser(userInfo.getId(), type.code);
      if (peopleInfo == null) {
        peopleInfo = new PeopleInfo();
        peopleInfo.setUserId(userInfo.getId());
        peopleInfo.setName(userInfo.getUserName());
        peopleInfo.setCreate_time(System.currentTimeMillis());
        peopleInfo.setUpdate_time(System.currentTimeMillis());
        peopleInfo.setState(UserState.ABLE.code);
        peopleInfo.setType(type.code);
        peopleInfo.setAddress(address);
        return userDao.insert(peopleInfo);
      } else {
        return userDao.updateByUserIdAndType(peopleInfo.getUserId(), type.code, address);
      }
    }
  }

  public boolean incrCoin(long userId, PeopleType type, long coin) {
    if (coin <= 0 || coin > 100000000000L) {
      return false;
    }
    synchronized (LockUtils.get(String.valueOf(userId))) {
      PeopleInfo peopleInfo = findUser(userId, type.code);
      if (peopleInfo == null) {
        return false;
      } else {
        peopleInfo.setCoin(peopleInfo.getCoin() + coin);
        return userDao.update(peopleInfo);
      }
    }
  }

  public boolean decrCoin(long userId, PeopleType type, long coin) {
    synchronized (LockUtils.get(String.valueOf(userId))) {
      PeopleInfo peopleInfo = findUser(userId, type.code);
      if (peopleInfo == null) {
        return false;
      } else {
        if (coin <= 0 || coin > peopleInfo.getCoin()) {
          return false;
        }
        peopleInfo.setCoin(peopleInfo.getCoin() + coin);
        return userDao.update(peopleInfo);
      }
    }

  }

  private PeopleInfo findUser(long userId, int type) {
    return userDao.findOne(SqlPath.where("user_id", Operator.EQ, userId).
        and("type", Operator.EQ, PeopleType.TRX.code));
  }

}
