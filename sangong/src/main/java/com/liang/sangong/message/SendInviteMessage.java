package com.liang.sangong.message;

public class SendInviteMessage extends Message {

  private String name;
  private String token;

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

  public String getToken() {
    return token;
  }

  public SendInviteMessage setToken(String token) {
    this.token = token;
    return this;
  }
}
