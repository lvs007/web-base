package com.liang.core;

import com.liang.bo.OnePoke;
import com.liang.core.PeoplePlay.GameType;
import com.liang.core.PeoplePlay.PeopleState;
import java.util.ArrayList;
import java.util.List;

public class Room {

  private int minPeopleNumber = 2;
  private int maxPeopleNumber = 6;

  private List<PeoplePlay> peoplePlayList = new ArrayList<>();

  private GameType gameType;

  private volatile boolean begin;

  public enum TableState {
    INIT, ALL_CONFIRM, DOING;
  }

  public Room(GameType gameType) {
    this.gameType = gameType;
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
    if (isCanAdd() && getState() == TableState.INIT && !begin) {
      peoplePlay.setRoom(this);
      peoplePlay.setGameType(gameType);
      return peoplePlayList.add(peoplePlay);
    }
    return false;
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
}
