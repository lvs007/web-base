package com.liang.sangong.message.in;

import com.liang.sangong.message.Message;

public class BeginMessage extends Message {

  private String token;

  public BeginMessage() {
    super(MessageType.begin);
  }

  public String getToken() {
    return token;
  }

  public BeginMessage setToken(String token) {
    this.token = token;
    return this;
  }
}
