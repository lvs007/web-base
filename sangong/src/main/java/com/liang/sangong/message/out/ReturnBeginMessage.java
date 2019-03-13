package com.liang.sangong.message.out;

import com.liang.sangong.core.PeoplePlay;
import com.liang.sangong.message.Message;
import java.util.List;

public class ReturnBeginMessage extends Message {

  private List<PeoplePlay> peoplePlayList;

  private long winner;

  public ReturnBeginMessage() {
    super(MessageType.beginReturn);
  }

  public List<PeoplePlay> getPeoplePlayList() {
    return peoplePlayList;
  }

  public ReturnBeginMessage setPeoplePlayList(
      List<PeoplePlay> peoplePlayList) {
    this.peoplePlayList = peoplePlayList;
    return this;
  }

  public long getWinner() {
    return winner;
  }

  public ReturnBeginMessage setWinner(long winner) {
    this.winner = winner;
    return this;
  }
}
