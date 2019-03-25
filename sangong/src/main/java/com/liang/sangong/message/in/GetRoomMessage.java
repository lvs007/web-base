package com.liang.sangong.message.in;

import com.liang.sangong.message.Message;

public class GetRoomMessage extends Message {

  private String roomId;
  private String token;

  public GetRoomMessage() {
    super(MessageType.getRoom);
  }

  public String getRoomId() {
    return roomId;
  }

  public GetRoomMessage setRoomId(String roomId) {
    this.roomId = roomId;
    return this;
  }

  public String getToken() {
    return token;
  }

  public GetRoomMessage setToken(String token) {
    this.token = token;
    return this;
  }
}
