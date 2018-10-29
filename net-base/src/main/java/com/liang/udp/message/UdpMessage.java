package com.liang.udp.message;

import com.liang.common.message.Message;

public class UdpMessage extends Message {

  private String content;

  public static final byte mType = -1;

  public UdpMessage() {
    super(mType);
  }

  public String getContent() {
    return content;
  }

  public UdpMessage setContent(String content) {
    this.content = content;
    return this;
  }

  @Override
  public String toString() {
    return "UdpMessage{" +
        "content='" + content + '\'' +
        ", type=" + type +
        ", address=" + address.toString() +
        '}';
  }
}
