package com.liang.tcp.message.entity;

import com.liang.common.message.Message;

public class PingMessage extends Message {

  private long sendTime;

  public static final byte mType = 1;

  public PingMessage() {
    this(0L);
  }

  public PingMessage(long sendTime) {
    super(mType);
    this.sendTime = sendTime;
  }

  public long getSendTime() {
    return sendTime;
  }

  public PingMessage setSendTime(long sendTime) {
    this.sendTime = sendTime;
    return this;
  }

  @Override
  public String toString() {
    return "PingMessage{" +
        "sendTime=" + sendTime +
        ", type=" + getType() +
        '}';
  }
}
