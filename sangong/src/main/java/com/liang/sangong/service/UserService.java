package com.liang.sangong.service;

import com.liang.common.util.LockUtils;
import com.liang.dao.jdbc.common.SearchFilter.Operator;
import com.liang.dao.jdbc.common.SqlPath;
import com.liang.mvc.filter.UserInfo;
import com.liang.sangong.bo.DataStatistics;
import com.liang.sangong.bo.GameResult;
import com.liang.sangong.bo.PeopleInfo;
import com.liang.sangong.bo.PeopleInfo.PeopleType;
import com.liang.sangong.bo.PeopleInfo.UserState;
import com.liang.sangong.bo.UserResult;
import com.liang.sangong.bo.UserResult.ResultEnum;
import com.liang.sangong.core.PeoplePlay;
import com.liang.sangong.core.RoomPool;
import com.liang.sangong.dao.DataStatisticsDao;
import com.liang.sangong.dao.GameResultDao;
import com.liang.sangong.dao.UserDao;
import com.liang.sangong.dao.UserResultDao;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  @Autowired
  private UserDao userDao;

  @Autowired
  private RoomPool roomPool;

  @Autowired
  private UserResultDao userResultDao;

  @Autowired
  private GameResultDao gameResultDao;

  @Autowired
  private DataStatisticsDao dataStatisticsDao;

  @Transactional
  public PeopleInfo setPeopleInfo(UserInfo userInfo, PeopleType peopleType) {
    synchronized (LockUtils.get(String.valueOf(userInfo.getId()))) {
      PeopleInfo peopleInfo = findUser(userInfo.getId(), peopleType.code);
      if (peopleInfo == null) {
        peopleInfo = new PeopleInfo();
        peopleInfo.setUserId(userInfo.getId());
        peopleInfo.setName(userInfo.getUserName());
        peopleInfo.setCreate_time(System.currentTimeMillis());
        peopleInfo.setUpdate_time(System.currentTimeMillis());
        peopleInfo.setState(UserState.ABLE.code);
        peopleInfo.setType(peopleType.code);
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

  @Transactional
  public boolean insertUserResult(UserResult userResult) {
    userResult.setCreateTime(System.currentTimeMillis());
    insertOrUpdateDataStatistics(userResult);
    return userResultDao.insert(userResult);
  }

  @Transactional
  public boolean insertGameResult(GameResult gameResult) {
    gameResult.setCreateTime(System.currentTimeMillis());
    return gameResultDao.insert(gameResult);
  }

  @Transactional
  public boolean insertOrUpdateDataStatistics(UserResult userResult) {
    DataStatistics dataStatistics = dataStatisticsDao.findOne(SqlPath.where("user_id",
        Operator.EQ, userResult.getUserId()).and("type", Operator.EQ, userResult.getType()));
    if (dataStatistics == null) {
      dataStatistics = new DataStatistics();
      dataStatistics.setCreateTime(System.currentTimeMillis())
          .setUpdateTime(System.currentTimeMillis()).setUserId(userResult.getUserId())
          .setType(userResult.getType()).setShuAmount(0).setShuCount(0).setWinAmount(0)
          .setWinCount(0);
      if (userResult.getResult() == ResultEnum.fail.code) {
        dataStatistics.setShuAmount(userResult.getCoin()).setShuCount(1);
      } else if (userResult.getResult() == ResultEnum.win.code) {
        dataStatistics.setWinAmount(userResult.getCoin()).setWinCount(1);
      }
      return dataStatisticsDao.insert(dataStatistics);
    } else {
      if (userResult.getResult() == ResultEnum.fail.code) {
        dataStatisticsDao.updateData(userResult.getUserId(), userResult.getType(),
            userResult.getCoin(), false);
      } else if (userResult.getResult() == ResultEnum.win.code) {
        dataStatisticsDao.updateData(userResult.getUserId(), userResult.getType(),
            userResult.getCoin(), true);
      }
      return true;
    }
  }

  @Transactional
  public boolean insertOrUpdateDataStatistics(DataStatistics dataStatistics) {
    if (dataStatistics.isNews()) {
      dataStatisticsDao.insert(dataStatistics);
      dataStatistics.setNews(false);
    } else {
      dataStatisticsDao.update(dataStatistics);
    }
    return true;
  }

  public List<DataStatistics> queryStatistics(long userId) {
    return dataStatisticsDao.findAll(SqlPath.where("user_id", Operator.EQ, userId));
  }

}
