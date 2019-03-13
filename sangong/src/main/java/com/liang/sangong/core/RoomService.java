package com.liang.sangong.core;

import com.liang.mvc.filter.UserInfo;
import com.liang.sangong.bo.PeopleInfo;
import com.liang.sangong.bo.PeopleInfo.PeopleType;
import com.liang.sangong.core.PeoplePlay.GameType;
import com.liang.sangong.core.Room.RoomType;
import com.liang.sangong.message.action.ComeInMessageAction;
import com.liang.sangong.service.UserService;
import java.util.ArrayList;
import java.util.List;
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

  public void zidong(Room room) {
    PeoplePlay winner = winner(room.getPeoplePlayList());
    room.setWinner(winner);
    System.out.println("winner: " + winner.getCurrentPoke());
  }

  public void zhuangjia(Room room) {
    PeoplePlay zhuangjia = null;
    List<PeoplePlay> comparePeoples = new ArrayList<>();
    for (PeoplePlay peoplePlay : room.getPeoplePlayList()) {
      if (peoplePlay.isZhuangjia()) {
        zhuangjia = peoplePlay;
      } else {
        comparePeoples.add(peoplePlay);
      }
    }
    PeoplePlay winner = null;
    for (PeoplePlay peoplePlay : comparePeoples) {
      winner = Rule.compareRetrunWinner(zhuangjia, peoplePlay);
    }
  }

  private PeoplePlay winner(List<PeoplePlay> peoplePlayList) {
    PeoplePlay winner = peoplePlayList.get(0);
    for (int i = 1; i < peoplePlayList.size(); i++) {
      winner = Rule.compareRetrunWinner(winner, peoplePlayList.get(i));
    }
    return winner;
  }

}
