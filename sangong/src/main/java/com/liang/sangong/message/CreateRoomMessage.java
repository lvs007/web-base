package com.liang.sangong.message;

public class CreateRoomMessage extends Message {

  private long userId;
  private String name;
  private String address;

  public CreateRoomMessage() {
    super(MessageType.createRoom);
  }

  public long getUserId() {
    return userId;
  }

  public CreateRoomMessage setUserId(long userId) {
    this.userId = userId;
    return this;
  }

  public String getName() {
    return name;
  }

  public CreateRoomMessage setName(String name) {
    this.name = name;
    return this;
  }

  public String getAddress() {
    return address;
  }

  public CreateRoomMessage setAddress(String address) {
    this.address = address;
    return this;
  }
}
