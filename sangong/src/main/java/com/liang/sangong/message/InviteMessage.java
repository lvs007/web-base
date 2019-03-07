package com.liang.sangong.message;

import com.liang.sangong.bo.PeopleInfo.PeopleType;

public class InviteMessage extends Message {

  private String roomId;
  private PeopleType peopleType;

  public InviteMessage() {
    super(MessageType.invite);
  }

  public String getRoomId() {
    return roomId;
  }

  public InviteMessage setRoomId(String roomId) {
    this.roomId = roomId;
    return this;
  }

  public PeopleType getPeopleType() {
    return peopleType;
  }

  public InviteMessage setPeopleType(PeopleType peopleType) {
    this.peopleType = peopleType;
    return this;
  }
}
