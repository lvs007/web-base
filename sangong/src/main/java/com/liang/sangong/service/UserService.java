package com.liang.sangong.service;

import com.liang.common.util.LockUtils;
import com.liang.dao.jdbc.common.SearchFilter.Operator;
import com.liang.dao.jdbc.common.SqlPath;
import com.liang.mvc.filter.UserInfo;
import com.liang.sangong.bo.PeopleInfo;
import com.liang.sangong.bo.PeopleInfo.PeopleType;
import com.liang.sangong.bo.PeopleInfo.UserState;
import com.liang.sangong.core.PeoplePlay;
import com.liang.sangong.core.RoomPool;
import com.liang.sangong.dao.UserDao;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  @Autowired
  private UserDao userDao;

  @Autowired
  private RoomPool roomPool;

  @Transactional
  public PeopleInfo setPeopleInfo(UserInfo userInfo) {
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
      return peopleInfo;
    }
  }

  @Transactional
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

  @Transactional
  public boolean updatePeopleInfo(PeopleInfo peopleInfo) {
    return updateUser(peopleInfo);
  }

  @Transactional
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
        return updateUser(peopleInfo);
      }
    }
  }

  @Transactional
  public boolean decrCoin(long userId, PeopleType type, long coin) {
    synchronized (LockUtils.get(String.valueOf(userId))) {
      PeopleInfo peopleInfo = findUser(userId, type.code);
      if (peopleInfo == null) {
        return false;
      } else {
        if (coin <= 0 || coin > peopleInfo.getCoin()) {
          return false;
        }
        peopleInfo.setCoin(peopleInfo.getCoin() - coin);
        return updateUser(peopleInfo);
      }
    }
  }

  private boolean updateUser(PeopleInfo peopleInfo) {
    peopleInfo.setUpdate_time(System.currentTimeMillis());
    if (userDao.update(peopleInfo)) {
      PeoplePlay peoplePlay = roomPool.getPeople(peopleInfo.getUserId());
      if (peoplePlay != null) {
        peoplePlay.setPeopleInfo(peopleInfo);
      }
      return true;
    }
    return false;
  }

  public PeopleInfo findUser(long userId, int type) {
    return userDao.findOne(SqlPath.where("user_id", Operator.EQ, userId).
        and("type", Operator.EQ, type));
  }

  public PeopleInfo findUser(String name, int type) {
    return userDao.findOne(SqlPath.where("name", Operator.EQ, name).
        and("type", Operator.EQ, type));
  }

}
