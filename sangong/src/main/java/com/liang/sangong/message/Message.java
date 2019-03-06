package com.liang.sangong.message;

public class Message {

  private MessageType messageType;

  public enum MessageType {
    login(1, "登录"), logout(2, "登出"),
    add(3, "进房间"), leave(4, "离开房间"),
    confirm(5, "准备"), unconfirm(6, "取消准备"),
    begin(7, "开始"), createRoom(8, "创建房间"),
    selectPeopleType(9, "选择币类型");

    MessageType(int code, String desc) {
      this.code = code;
      this.desc = desc;
    }

    public int code;
    public String desc;
  }

  public Message(MessageType messageType) {
    this.messageType = messageType;
  }

  public MessageType getMessageType() {
    return messageType;
  }

  public Message setMessageType(MessageType messageType) {
    this.messageType = messageType;
    return this;
  }
}
