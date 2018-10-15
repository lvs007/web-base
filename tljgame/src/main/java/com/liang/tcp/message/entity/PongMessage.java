package com.liang.tcp.message.entity;

import com.liang.tcp.message.Message;
import com.liang.tcp.message.MessageTypeEnum;

public class PongMessage extends Message {

  private long pingTime;
  private long sendTime;

  public PongMessage() {
    super(MessageTypeEnum.PONG.value);
  }

  public PongMessage(long pingTime, long sendTime) {
    super(MessageTypeEnum.PONG.value);
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
        ", type=" + getMessageType() +
        '}';
  }
}
