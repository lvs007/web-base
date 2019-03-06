package com.liang.sangong.core;

import static com.liang.sangong.common.Constants.MIN_ZUOZHUANG_COIN;

import com.liang.sangong.bo.PeopleInfo;
import com.liang.sangong.bo.PeopleInfo.PeopleType;
import com.liang.sangong.bo.PokesBo.Poke;
import com.liang.sangong.common.Constants;
import com.liang.sangong.common.ThreadUtils;
import com.liang.sangong.core.Room.TableState;
import java.util.ArrayList;
import java.util.List;

public class PeoplePlay {

  private PeopleInfo peopleInfo;
  private List<Poke> currentPoke = new ArrayList<>();
  private boolean isZhuangjia;
  private PeopleState state = PeopleState.INIT;
  private Room room;
  private long playCoin;
  private GameType gameType = GameType.ZIDONG;
  private PeopleType peopleType = PeopleType.TRX;

  public enum PeopleState {
    INIT, CONFIRM, PLAY
  }

  public enum GameType {
    ZHUANGJIA, ZIDONG;
  }

  public PeoplePlay(PeopleInfo peopleInfo) {
    this.peopleInfo = peopleInfo;
  }

  public boolean isCanBegin() {
    if (gameType == GameType.ZHUANGJIA && !isZhuangjia) {
      return false;
    }
    if (room != null && room.getState() == TableState.ALL_CONFIRM) {
      return true;
    }
    return false;
  }

  public boolean isCanConfirm(long coin) {
    if (room == null) {
      return false;
    }
    if (state != PeopleState.INIT) {
      return false;
    }
    if (gameType == GameType.ZHUANGJIA && isZhuangjia) {
      return true;
    }
    if (coin < Constants.MIN_PLAY_COIN || coin > peopleInfo.getCoin()) {
      return false;
    }
    return true;
  }

  /**
   * 下注
   */
  public synchronized boolean confirm(long coin) {
    if (!isCanConfirm(coin)) {
      return false;
    }
    if (gameType == GameType.ZHUANGJIA && isZhuangjia) {
      coin = 0;
    }
    currentPoke.clear();
    state = PeopleState.CONFIRM;
    peopleInfo.decr(coin);
    setPlayCoin(coin);
    return true;
  }

  public synchronized boolean unConfirm() {
    if (state == PeopleState.CONFIRM) {
      state = PeopleState.INIT;
      peopleInfo.incr(playCoin);
      playCoin = 0L;
      return true;
    }
    return false;
  }

  public synchronized void begin() {
    if (isCanBegin()) {
      room.setBegin(true);
      ThreadUtils.sleep(Constants.WAIT_TIME_TO_BEGIN);
      if (isCanBegin()) {
        room.getPeoplePlayList().forEach(peoplePlay -> {
          peoplePlay.setState(PeopleState.PLAY);
        });
        room.fapai();
        room.compare();
        end();
      } else {
        room.setBegin(false);
      }
    }
  }

  public void end() {
    state = PeopleState.INIT;
    playCoin = 0;
  }

  public boolean zuoZhuang() {
    synchronized (room) {
      if (room == null) {
        return false;
      }
      if (gameType != GameType.ZHUANGJIA) {
        return false;
      }
      if (peopleInfo.getCoin() < MIN_ZUOZHUANG_COIN) {
        return false;
      }
      for (PeoplePlay peoplePlay : room.getPeoplePlayList()) {
        if (peoplePlay.isZhuangjia()) {
          return false;
        }
      }
      setZhuangjia(true);
      return true;
    }
  }

  public boolean unZuoZhuang() {
    synchronized (room) {
      if (isZhuangjia) {
        setZhuangjia(false);
        return true;
      }
      return false;
    }
  }

  public void addPoke(Poke poke) {
    currentPoke.add(poke);
  }

  public PeopleInfo getPeopleInfo() {
    return peopleInfo;
  }

  public PeoplePlay setPeopleInfo(PeopleInfo peopleInfo) {
    this.peopleInfo = peopleInfo;
    return this;
  }

  public List<Poke> getCurrentPoke() {
    return currentPoke;
  }

  public PeoplePlay setCurrentPoke(List<Poke> currentPoke) {
    this.currentPoke = currentPoke;
    return this;
  }

  public boolean isZhuangjia() {
    return isZhuangjia;
  }

  public PeoplePlay setZhuangjia(boolean zhuangjia) {
    isZhuangjia = zhuangjia;
    return this;
  }

  public PeopleState getState() {
    return state;
  }

  public PeoplePlay setState(PeopleState state) {
    this.state = state;
    return this;
  }

  public Room getRoom() {
    return room;
  }

  public PeoplePlay setRoom(Room room) {
    this.room = room;
    return this;
  }

  public long getPlayCoin() {
    return playCoin;
  }

  public PeoplePlay setPlayCoin(long playCoin) {
    this.playCoin = playCoin;
    return this;
  }

  public GameType getGameType() {
    return gameType;
  }

  public PeoplePlay setGameType(GameType gameType) {
    this.gameType = gameType;
    return this;
  }

  public PeopleType getPeopleType() {
    return peopleType;
  }

  public PeoplePlay setPeopleType(PeopleType peopleType) {
    this.peopleType = peopleType;
    return this;
  }
}
