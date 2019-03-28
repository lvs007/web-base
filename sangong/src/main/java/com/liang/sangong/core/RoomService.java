package com.liang.sangong.core;

import com.liang.mvc.filter.UserInfo;
import com.liang.sangong.bo.GameResult;
import com.liang.sangong.bo.PeopleInfo;
import com.liang.sangong.bo.PeopleInfo.PeopleType;
import com.liang.sangong.bo.UserResult;
import com.liang.sangong.common.Constants;
import com.liang.sangong.common.SystemState;
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

  @Autowired
  private CounterManager counterManager;

  public Room createRoom(UserInfo userInfo, PeopleType peopleType, RoomType roomType) {
    if (peopleType == null) {
      return null;
    }
    PeopleInfo peopleInfo = userService.findUser(userInfo.getId(), peopleType.code);
    if (peopleInfo == null || peopleInfo.getCoin() < Constants.MIN_PLAY_COIN) {
      return null;
    }
    if (roomType == RoomType.PRIVATE && peopleInfo.getCoin() < Constants.MIN_ZUOZHUANG_COIN) {
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
    if (peopleType == null) {
      return null;
    }
    synchronized (room) {
      room = roomPool.getRoom(roomId);
      if (room == null) {
        return null;
      }
      PeopleInfo peopleInfo = userService.findUser(userInfo.getId(), peopleType.code);
      if (peopleInfo == null || peopleInfo.getCoin() < Constants.MIN_PLAY_COIN) {
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
        userService.insertUserResult(UserResult.build(-decrCoin,
            peoplePlay.getPeopleInfo().getUserId(),
            peoplePlay.getCurrentPoke().toString(), room.getRoomId(),
            peoplePlay.getPeopleType().code));
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
      long value = peoplePlay.getPlayCoin();
      if (allCoin > coin) {
        value = (peoplePlay.getPlayCoin() / allCoin) * coin;
      }
      long tmp = (long) (value * (1 - (double) SystemState.RATE / 100));
      if (tmp > 0) {
        counterManager.incr(peoplePlay.getPeopleType(), value - tmp);
        value = tmp;
      }
      UserResult userResult = UserResult.build(value, peoplePlay.getPeopleInfo().getUserId(),
          peoplePlay.getCurrentPoke().toString(), room.getRoomId(),
          peoplePlay.getPeopleType().code);
      userService.insertUserResult(userResult);
      userService.incrCoin(peoplePlay.getPeopleInfo().getUserId(), peoplePlay.getPeopleType(),
          value);
    }
    long zhuangjiaWin = 0;
    if (allCoin >= coin) {
      userService.decrCoin(zhuangjia.getPeopleInfo().getUserId(), zhuangjia.getPeopleType(),
          zhuangjia.getPeopleInfo().getCoin());
      zhuangjiaWin = -zhuangjia.getPeopleInfo().getCoin();
    } else {
      zhuangjiaWin = coin - allCoin - zhuangjia.getPeopleInfo().getCoin();
      if (zhuangjiaWin > 0) {
        long tmp = (long) (zhuangjiaWin * (1 - (double) SystemState.RATE / 100));
        if (tmp > 0) {
          counterManager.incr(zhuangjia.getPeopleType(), zhuangjiaWin - tmp);
          zhuangjiaWin = tmp;
        }
        userService.incrCoin(zhuangjia.getPeopleInfo().getUserId(), zhuangjia.getPeopleType(),
            zhuangjiaWin);
      } else {
        userService.updatePeopleInfo(zhuangjia.getPeopleInfo().setCoin(coin - allCoin));
      }
    }
    UserResult userResult = UserResult.build(zhuangjiaWin, zhuangjia.getPeopleInfo().getUserId(),
        zhuangjia.getCurrentPoke().toString(), room.getRoomId(), zhuangjia.getPeopleType().code);
    userService.insertUserResult(userResult);
    userService.insertGameResult(GameResult.build(room.getRoomId(),
        room.getPeoplePlayList().toString()));
  }

  private PeoplePlay winner(List<PeoplePlay> peoplePlayList) {
    PeoplePlay winner = peoplePlayList.get(0);
    for (int i = 1; i < peoplePlayList.size(); i++) {
      winner = Rule.compareRetrunWinner(winner, peoplePlayList.get(i));
    }
    return winner;
  }

}
