package com.liang.sangong.message.in;

import com.liang.sangong.message.Message;

public class LeaveRoomMessage extends Message {

  private String roomId;
  private String token;

  public LeaveRoomMessage() {
    super(MessageType.leave);
  }

  public String getRoomId() {
    return roomId;
  }

  public LeaveRoomMessage setRoomId(String roomId) {
    this.roomId = roomId;
    return this;
  }

  public String getToken() {
    return token;
  }

  public LeaveRoomMessage setToken(String token) {
    this.token = token;
    return this;
  }
}
