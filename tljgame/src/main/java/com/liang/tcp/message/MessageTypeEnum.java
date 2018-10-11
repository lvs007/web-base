package com.liang.tcp.message;

import liang.common.exception.NotSupportException;

public enum MessageTypeEnum {
  PING(1),
  PONG(2),
  ADD_GROUP(3);

  MessageTypeEnum(int value) {
    if (value > 127 || value < -128) {
      throw NotSupportException.throwException("value must between -128 to 127");
    }
    this.value = (byte) value;
  }

  public static MessageTypeEnum get(byte value) {
    for (MessageTypeEnum typeEnum : values()) {
      if (typeEnum.value == value) {
        return typeEnum;
      }
    }
    return null;
  }

  public byte value;
}
