package com.liang.sangong.core;

import com.liang.mvc.filter.UserInfo;
import com.liang.sangong.bo.PeopleInfo;
import com.liang.sangong.bo.PeopleInfo.PeopleType;
import com.liang.sangong.core.PeoplePlay.GameType;
import com.liang.sangong.core.Room.RoomType;
import com.liang.sangong.message.action.ComeInMessageAction;
import com.liang.sangong.service.UserService;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
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

  public Room createRoom(UserInfo userInfo, PeopleType peopleType, RoomType roomType) {
    PeopleInfo peopleInfo = userService.findUser(userInfo.getId(), peopleType.code);
    if (peopleInfo == null || peopleType == null) {
      return null;
    }
    PeoplePlay peoplePlay = roomPool.getPeople(userInfo.getId());
    if (peoplePlay != null) {
      return peoplePlay.getRoom();
    }
    peoplePlay = new PeoplePlay(peopleInfo);
    Room room = new Room(GameType.ZHUANGJIA, roomType);
    room.add(peoplePlay);
    roomPool.addRoom(room);
    roomPool.addPeople(peoplePlay);
    peoplePlay.zuoZhuang();
    return room;
  }

  public Room quickJoin(UserInfo userInfo, PeopleType peopleType) {
    for (Entry<String, Room> entry : roomPool.getRoomPool().entrySet()) {
      Room room = entry.getValue();
      if (room.getRoomType() == RoomType.PUBLIC && room.isCanAdd() && !room.isBegin()) {
        return comeInRoom(room.getRoomId(), userInfo, peopleType);
      }
    }
    return createRoom(userInfo, peopleType, RoomType.PUBLIC);
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

  public boolean leaveRoom(long userId) {
    PeoplePlay peoplePlay = roomPool.getPeople(userId);
    if (peoplePlay == null) {
      return false;
    }
    Room room = peoplePlay.getRoom();
    if (room == null) {
      return false;
    }
    synchronized (room) {
      if (room.leave(peoplePlay)) {
        roomPool.removePeople(peoplePlay);
        if (room.getPeoplePlayList().size() == 0) {
          roomPool.removeRoom(room.getRoomId());
        }
      }
      return true;
    }
  }

  public boolean tiRen(PeoplePlay play, long userId) {
    if (play.isZhuangjia()) {
      return leaveRoom(userId);
    }
    return false;
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
    long coin = zhuangjia.getPeopleInfo().getCoin();
    for (Iterator<PeoplePlay> iterator = comparePeoples.iterator(); iterator.hasNext(); ) {
      PeoplePlay peoplePlay = iterator.next();
      winner = Rule.compareRetrunWinner(zhuangjia, peoplePlay);
      //庄家赢
      if (winner.getPeopleInfo().getUserId() == zhuangjia.getPeopleInfo().getUserId()) {
        long decrCoin = 0;
        if (zhuangjia.getPeopleInfo().getCoin() >= peoplePlay.getPlayCoin()) {
          coin += peoplePlay.getPlayCoin();
          decrCoin = peoplePlay.getPlayCoin();
        } else {
          coin += zhuangjia.getPeopleInfo().getCoin();
          decrCoin = zhuangjia.getPeopleInfo().getCoin();
        }
        userService.decrCoin(peoplePlay.getPeopleInfo().getUserId(),
            peoplePlay.getPeopleType(), decrCoin);
        iterator.remove();
      }
    }
    long allCoin = 0;
    for (PeoplePlay peoplePlay : comparePeoples) {//都是赢家
      allCoin += peoplePlay.getPlayCoin();
    }
    for (PeoplePlay peoplePlay : comparePeoples) {
      if (allCoin > coin) {
        long value = (peoplePlay.getPlayCoin() / allCoin) * coin;
        userService.incrCoin(peoplePlay.getPeopleInfo().getUserId(), peoplePlay.getPeopleType(),
            value);
      } else {
        userService.incrCoin(peoplePlay.getPeopleInfo().getUserId(), peoplePlay.getPeopleType(),
            peoplePlay.getPlayCoin());
      }
    }
    if (allCoin >= coin) {
      userService.decrCoin(zhuangjia.getPeopleInfo().getUserId(), zhuangjia.getPeopleType(),
          zhuangjia.getPeopleInfo().getCoin());
    } else {
      userService.updatePeopleInfo(zhuangjia.getPeopleInfo().setCoin(coin - allCoin));
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
