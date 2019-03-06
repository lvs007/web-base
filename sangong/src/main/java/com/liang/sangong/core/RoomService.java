package com.liang.sangong.core;

import com.liang.mvc.filter.UserInfo;
import com.liang.sangong.bo.PeopleInfo;
import com.liang.sangong.bo.PeopleInfo.PeopleType;
import com.liang.sangong.core.PeoplePlay.GameType;
import com.liang.sangong.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoomService {

  @Autowired
  private RoomPool roomPool;

  @Autowired
  private UserService userService;

  public boolean createRoom(UserInfo userInfo, PeopleType peopleType) {
    PeopleInfo peopleInfo = userService.findUser(userInfo.getId(), peopleType.code);
    if (peopleInfo == null || peopleType == null) {
      return false;
    }
    PeoplePlay peoplePlay = new PeoplePlay(peopleInfo);
    Room room = new Room(GameType.ZHUANGJIA);
    room.add(peoplePlay);
    roomPool.add(room);
    return true;
  }

  public void invite() {

  }

}
