package com.liang.sangong.message.out;

import com.liang.sangong.message.Message;

public class ReturnRechargeMessage extends Message {

  private String name;
  private long coin;

  public ReturnRechargeMessage() {
    super(MessageType.returnRecharge);
  }

  public String getName() {
    return name;
  }

  public ReturnRechargeMessage setName(String name) {
    this.name = name;
    return this;
  }

  public long getCoin() {
    return coin;
  }

  public ReturnRechargeMessage setCoin(long coin) {
    this.coin = coin;
    return this;
  }
}
