package com.liang.sangong.message.out;

import com.liang.sangong.core.Room;
import com.liang.sangong.message.Message;

public class ReturnRoomMessage extends Message {

  private Room room;

  public ReturnRoomMessage() {
    super(MessageType.returnRoom);
  }

  public Room getRoom() {
    return room;
  }

  public ReturnRoomMessage setRoom(Room room) {
    this.room = room;
    return this;
  }
}
