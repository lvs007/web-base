package com.liang.sangong.message;

public class SendInviteMessage extends Message {

  private String name;

  public SendInviteMessage() {
    super(MessageType.sendInvite);
  }

  public String getName() {
    return name;
  }

  public SendInviteMessage setName(String name) {
    this.name = name;
    return this;
  }
}
