package com.liang.sangong.message.in;

import com.liang.sangong.message.Message;

public class UnZuoZhuangMessage extends Message {

  private String token;

  public UnZuoZhuangMessage() {
    super(MessageType.unzuozhuang);
  }

  public String getToken() {
    return token;
  }

  public UnZuoZhuangMessage setToken(String token) {
    this.token = token;
    return this;
  }
}
