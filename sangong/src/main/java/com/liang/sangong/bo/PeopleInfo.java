package com.liang.sangong.bo;

import com.liang.common.exception.NotSupportException;

public class PeopleInfo {

  private long id;
  private long userId;
  private String name;
  private long coin;
  private String address;
  private int type;
  private int state;
  private long create_time;
  private long update_time;

  public enum UserState {
    ABLE(1);

    UserState(int code) {
      this.code = code;
    }

    public int code;
  }

  public enum PeopleType {
    TRX(1), JI_FEN(2);

    PeopleType(int code) {
      this.code = code;
    }

    public static PeopleType getType(int code) {
      for (PeopleType peopleType : values()) {
        if (code == peopleType.code) {
          return peopleType;
        }
      }
      throw NotSupportException.throwException("不支持的类型");
    }

    public int code;
  }

  public PeopleInfo() {
  }

  public PeopleInfo(String name, long coin) {
    this.name = name;
    this.coin = coin;
  }

  public PeopleInfo(long id, String name, long coin) {
    this.id = id;
    this.name = name;
    this.coin = coin;
  }

  public void decr(long coin) {
    this.coin = this.coin - coin;
  }

  public void incr(long coin) {
    this.coin += coin;
  }

  public long getId() {
    return id;
  }

  public PeopleInfo setId(long id) {
    this.id = id;
    return this;
  }

  public long getUserId() {
    return userId;
  }

  public PeopleInfo setUserId(long userId) {
    this.userId = userId;
    return this;
  }

  public String getName() {
    return name;
  }

  public PeopleInfo setName(String name) {
    this.name = name;
    return this;
  }

  public long getCoin() {
    return coin;
  }

  public PeopleInfo setCoin(long coin) {
    this.coin = coin;
    return this;
  }

  public String getAddress() {
    return address;
  }

  public PeopleInfo setAddress(String address) {
    this.address = address;
    return this;
  }

  public int getType() {
    return type;
  }

  public PeopleInfo setType(int type) {
    this.type = type;
    return this;
  }

  public int getState() {
    return state;
  }

  public PeopleInfo setState(int state) {
    this.state = state;
    return this;
  }

  public long getCreate_time() {
    return create_time;
  }

  public PeopleInfo setCreate_time(long create_time) {
    this.create_time = create_time;
    return this;
  }

  public long getUpdate_time() {
    return update_time;
  }

  public PeopleInfo setUpdate_time(long update_time) {
    this.update_time = update_time;
    return this;
  }

  @Override
  public String toString() {
    return "PeopleInfo{" +
        "id=" + id +
        ", userId=" + userId +
        ", name='" + name + '\'' +
        ", coin=" + coin +
        ", type=" + type +
        ", state=" + state +
        '}';
  }
}
