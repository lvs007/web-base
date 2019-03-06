package com.liang.sangong.core;

import com.liang.sangong.core.PeoplePlay.GameType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoomService {

  @Autowired
  private RoomPool roomPool;

  public void createRoom(PeoplePlay peoplePlay) {
    Room room = new Room(GameType.ZHUANGJIA);
    room.add(peoplePlay);
    roomPool.add(room);
  }

  public void invite() {

  }

}
