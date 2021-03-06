package com.liang.tcp.message.entity;

import com.liang.common.message.Message;

public class PongMessage extends Message {

  private long pingTime;
  private long sendTime;

  public static final byte mType = 2;

  public PongMessage() {
    this(0L, 0L);
  }

  public PongMessage(long pingTime, long sendTime) {
    super(mType);
    this.pingTime = pingTime;
    this.sendTime = sendTime;
  }

  public long getPingTime() {
    return pingTime;
  }

  public PongMessage setPingTime(long pingTime) {
    this.pingTime = pingTime;
    return this;
  }

  public long getSendTime() {
    return sendTime;
  }

  public PongMessage setSendTime(long sendTime) {
    this.sendTime = sendTime;
    return this;
  }

  @Override
  public String toString() {
    return "PongMessage{" +
        "pingTime=" + pingTime +
        ", sendTime=" + sendTime +
        ", type=" + getType() +
        '}';
  }
}
