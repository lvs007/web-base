package com.liang.sangong.message.in;

import com.liang.sangong.message.Message;

public class ConfirmMessage extends Message {

  private String token;
  private long coin;

  public ConfirmMessage() {
    super(MessageType.confirm);
  }

  public String getToken() {
    return token;
  }

  public ConfirmMessage setToken(String token) {
    this.token = token;
    return this;
  }

  public long getCoin() {
    return coin;
  }

  public ConfirmMessage setCoin(long coin) {
    this.coin = coin;
    return this;
  }
}
