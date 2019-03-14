package com.liang.sangong.bo;

public class UserResult {

  private long id;
  private long userId;
  private int result;
  private String poke;
  private long coin;
  private long createTime;

  public long getId() {
    return id;
  }

  public UserResult setId(long id) {
    this.id = id;
    return this;
  }

  public long getUserId() {
    return userId;
  }

  public UserResult setUserId(long userId) {
    this.userId = userId;
    return this;
  }

  public int getResult() {
    return result;
  }

  public UserResult setResult(int result) {
    this.result = result;
    return this;
  }

  public String getPoke() {
    return poke;
  }

  public UserResult setPoke(String poke) {
    this.poke = poke;
    return this;
  }

  public long getCoin() {
    return coin;
  }

  public UserResult setCoin(long coin) {
    this.coin = coin;
    return this;
  }

  public long getCreateTime() {
    return createTime;
  }

  public UserResult setCreateTime(long createTime) {
    this.createTime = createTime;
    return this;
  }

}
