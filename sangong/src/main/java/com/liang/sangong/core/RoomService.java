package com.liang.sangong.core;

import com.liang.mvc.filter.UserInfo;
import com.liang.sangong.bo.PeopleInfo;
import com.liang.sangong.bo.PeopleInfo.PeopleType;
import com.liang.sangong.core.PeoplePlay.GameType;
import com.liang.sangong.core.Room.RoomType;
import com.liang.sangong.message.action.ComeInMessageAction;
import com.liang.sangong.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoomService {

  @Autowired
  private RoomPool roomPool;

  @Autowired
  private UserService userService;

  @Autowired
  private ComeInMessageAction comeInMessageAction;

  public Room createRoom(UserInfo userInfo, PeopleType peopleType) {
    PeopleInfo peopleInfo = userService.findUser(userInfo.getId(), peopleType.code);
    if (peopleInfo == null || peopleType == null) {
      return null;
    }
    PeoplePlay peoplePlay = roomPool.getPeople(userInfo.getId());
    if (peoplePlay != null) {
      return peoplePlay.getRoom();
    }
    peoplePlay = new PeoplePlay(peopleInfo);
    Room room = new Room(GameType.ZHUANGJIA, RoomType.PRIVATE);
    room.add(peoplePlay);
    roomPool.addRoom(room);
    roomPool.addPeople(peoplePlay);
    return room;
  }

  public void invite() {

  }

  public Room comeInRoom(String roomId, UserInfo userInfo, PeopleType peopleType) {
    Room room = roomPool.getRoom(roomId);
    if (room == null) {
      return null;
    }
    synchronized (room) {
      room = roomPool.getRoom(roomId);
      if (room == null) {
        return null;
      }
      PeopleInfo peopleInfo = userService.findUser(userInfo.getId(), peopleType.code);
      if (peopleInfo == null || peopleType == null) {
        return null;
      }
      PeoplePlay peoplePlay = roomPool.getPeople(userInfo.getId());
      if (peoplePlay != null) {
        return peoplePlay.getRoom();
      }
      peoplePlay = new PeoplePlay(peopleInfo);
      if (room.add(peoplePlay)) {
        roomPool.addPeople(peoplePlay);
        //通知其他人员，有人进入房间
        comeInMessageAction.action(peoplePlay);
        return room;
      }
      return null;
    }
  }

  public boolean leaveRoom(String roomId, UserInfo userInfo) {
    Room room = roomPool.getRoom(roomId);
    if (room == null) {
      return false;
    }
    synchronized (room) {
      PeoplePlay peoplePlay = roomPool.getPeople(userInfo.getId());
      if (peoplePlay == null) {
        return false;
      }
      if (room.leave(peoplePlay)) {
        roomPool.removePeople(peoplePlay);
        if (room.getPeoplePlayList().size() == 0) {
          roomPool.removeRoom(roomId);
        }
      }
      return true;
    }
  }

  public Object getRoomData(String roomId) {
    return roomPool.getRoom(roomId);
  }

}
