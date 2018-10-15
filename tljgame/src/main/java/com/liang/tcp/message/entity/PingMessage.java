package com.liang.tcp.message.entity;

import com.liang.tcp.message.Message;
import com.liang.tcp.message.MessageTypeEnum;

public class PingMessage extends Message {

  private long sendTime;

  public PingMessage() {
    super(MessageTypeEnum.PING.value);
  }

  public PingMessage(long sendTime) {
    super(MessageTypeEnum.PING.value);
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
        ", type=" + getMessageType() +
        '}';
  }
}
