package com.liang.tcp.message;

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
