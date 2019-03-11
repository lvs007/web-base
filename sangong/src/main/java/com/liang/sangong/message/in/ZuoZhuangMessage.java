package com.liang.sangong.message.in;

import com.liang.sangong.message.Message;

public class ZuoZhuangMessage extends Message {

  private String token;

  public ZuoZhuangMessage() {
    super(MessageType.zuozhuang);
  }

  public String getToken() {
    return token;
  }

  public ZuoZhuangMessage setToken(String token) {
    this.token = token;
    return this;
  }
}
