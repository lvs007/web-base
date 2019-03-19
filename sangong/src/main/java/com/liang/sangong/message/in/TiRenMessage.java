package com.liang.sangong.message.in;

import com.liang.sangong.message.Message;

public class TiRenMessage extends Message {

  private String token;
  private long userId;

  public TiRenMessage() {
    super(MessageType.tiren);
  }

  public String getToken() {
    return token;
  }

  public TiRenMessage setToken(String token) {
    this.token = token;
    return this;
  }

  public long getUserId() {
    return userId;
  }

  public TiRenMessage setUserId(long userId) {
    this.userId = userId;
    return this;
  }
}
