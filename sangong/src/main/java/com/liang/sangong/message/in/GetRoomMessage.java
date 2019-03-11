package com.liang.sangong.message.in;

import com.liang.sangong.message.Message;

public class GetRoomMessage extends Message {

  private String roomId;

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
}
