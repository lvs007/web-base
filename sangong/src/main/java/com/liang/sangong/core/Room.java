package com.liang.sangong.core;

import com.liang.sangong.bo.OnePoke;
import com.liang.sangong.core.PeoplePlay.GameType;
import com.liang.sangong.core.PeoplePlay.PeopleState;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class Room {

  private int minPeopleNumber = 2;
  private int maxPeopleNumber = 6;

  private List<PeoplePlay> peoplePlayList = new ArrayList<>();

  private GameType gameType;

  private volatile boolean begin;

  private String roomId;

  private RoomType roomType;

  public enum TableState {
    INIT, ALL_CONFIRM, DOING;
  }

  public enum RoomType {
    PUBLIC, PRIVATE;
  }

  public Room(GameType gameType, RoomType roomType) {
    this.gameType = gameType;
    this.roomType = roomType;
    this.roomId = UUID.randomUUID().toString().replaceAll("-", "");
  }

  public TableState getState() {
    boolean confirm = false;
    if (peoplePlayList.size() < minPeopleNumber) {
      return TableState.INIT;
    }
    for (PeoplePlay peoplePlay : peoplePlayList) {
      if (peoplePlay.getState() == PeopleState.INIT) {
        return TableState.INIT;
      } else if (peoplePlay.getState() == PeopleState.CONFIRM) {
        confirm = true;
      }
    }
    return confirm == true ? TableState.ALL_CONFIRM : TableState.DOING;
  }

  public synchronized boolean add(PeoplePlay peoplePlay) {
    if (isCanAdd() && peoplePlay.getState() == PeopleState.INIT && getState() == TableState.INIT
        && !begin) {
      peoplePlay.setRoom(this);
      peoplePlay.setGameType(gameType);
      peoplePlay.setSite(peoplePlayList.size());
      return peoplePlayList.add(peoplePlay);
    }
    return false;
  }

  public synchronized boolean leave(PeoplePlay peoplePlay) {
    if (peoplePlay.getState() == PeopleState.INIT && getState() == TableState.INIT && !begin) {
      for (Iterator<PeoplePlay> iterator = peoplePlayList.iterator(); iterator.hasNext(); ) {
        PeoplePlay peoplePlayNext = iterator.next();
        if (peoplePlayNext.getPeopleInfo().getId() == peoplePlay.getPeopleInfo().getId()) {
          peoplePlayNext.setRoom(null);
          iterator.remove();
          break;
        }
      }
      for (int i = 0; i < peoplePlayList.size(); i++) {
        peoplePlayList.get(i).setSite(i);
      }
      return true;
    } else {
      return false;
    }
  }

  public void fapai() {
    OnePoke onePoke = new OnePoke();
    for (int i = 0; i < 3; i++) {
      for (PeoplePlay peoplePlay : peoplePlayList) {
        peoplePlay.addPoke(onePoke.pop());
      }
    }
  }

  public void compare() {
    PeoplePlay winner = winner();
    if (gameType == GameType.ZIDONG) {

    } else {

    }
    System.out.println("winner: " + winner.getCurrentPoke());
  }

  private PeoplePlay winner() {
    PeoplePlay winner = peoplePlayList.get(0);
    for (int i = 1; i < peoplePlayList.size(); i++) {
      winner = Rule.compareRetrunWinner(winner, peoplePlayList.get(i));
    }
    return winner;
  }

  public boolean isCanAdd() {
    return peoplePlayList.size() < maxPeopleNumber;
  }

  public List<PeoplePlay> getPeoplePlayList() {
    return peoplePlayList;
  }

  public boolean isBegin() {
    return begin;
  }

  public Room setBegin(boolean begin) {
    this.begin = begin;
    return this;
  }

  public GameType getGameType() {
    return gameType;
  }

  public Room setGameType(GameType gameType) {
    this.gameType = gameType;
    return this;
  }

  public String getRoomId() {
    return roomId;
  }

  public Room setPeoplePlayList(List<PeoplePlay> peoplePlayList) {
    this.peoplePlayList = peoplePlayList;
    return this;
  }

  public Room setRoomId(String roomId) {
    this.roomId = roomId;
    return this;
  }

  public RoomType getRoomType() {
    return roomType;
  }

  public Room setRoomType(RoomType roomType) {
    this.roomType = roomType;
    return this;
  }
}
