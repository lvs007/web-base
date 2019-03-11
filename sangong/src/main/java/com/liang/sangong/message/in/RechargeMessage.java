package com.liang.sangong.message.in;

import com.liang.sangong.bo.PeopleInfo.PeopleType;
import com.liang.sangong.message.Message;

public class RechargeMessage extends Message {

  private String token;
  private long coin;
  private PeopleType peopleType;

  public RechargeMessage() {
    super(MessageType.recharge);
  }

  public String getToken() {
    return token;
  }

  public RechargeMessage setToken(String token) {
    this.token = token;
    return this;
  }

  public long getCoin() {
    return coin;
  }

  public RechargeMessage setCoin(long coin) {
    this.coin = coin;
    return this;
  }

  public PeopleType getPeopleType() {
    return peopleType;
  }

  public RechargeMessage setPeopleType(PeopleType peopleType) {
    this.peopleType = peopleType;
    return this;
  }
}
