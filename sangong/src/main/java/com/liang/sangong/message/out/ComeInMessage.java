package com.liang.sangong.message.out;

import com.liang.sangong.core.PeoplePlay;
import com.liang.sangong.message.Message;

public class ComeInMessage extends Message {

  private PeoplePlay peoplePlay;

  public ComeInMessage() {
    super(MessageType.add);
  }

  public PeoplePlay getPeoplePlay() {
    return peoplePlay;
  }

  public ComeInMessage setPeoplePlay(PeoplePlay peoplePlay) {
    this.peoplePlay = peoplePlay;
    return this;
  }
}
