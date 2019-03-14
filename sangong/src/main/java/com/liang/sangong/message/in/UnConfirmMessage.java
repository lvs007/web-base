package com.liang.sangong.message.in;

import com.liang.sangong.message.Message;

public class UnConfirmMessage extends Message {

  private String token;

  public UnConfirmMessage() {
    super(MessageType.unconfirm);
  }

  public String getToken() {
    return token;
  }

  public UnConfirmMessage setToken(String token) {
    this.token = token;
    return this;
  }
}
