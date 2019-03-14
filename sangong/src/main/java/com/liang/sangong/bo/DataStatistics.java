package com.liang.sangong.bo;

public class DataStatistics {

  private long id;
  private long userId;
  private long shuCount;
  private long winCount;
  private long createTime;

  public long getId() {
    return id;
  }

  public DataStatistics setId(long id) {
    this.id = id;
    return this;
  }

  public long getUserId() {
    return userId;
  }

  public DataStatistics setUserId(long userId) {
    this.userId = userId;
    return this;
  }

  public long getShuCount() {
    return shuCount;
  }

  public DataStatistics setShuCount(long shuCount) {
    this.shuCount = shuCount;
    return this;
  }

  public long getWinCount() {
    return winCount;
  }

  public DataStatistics setWinCount(long winCount) {
    this.winCount = winCount;
    return this;
  }

  public long getCreateTime() {
    return createTime;
  }

  public DataStatistics setCreateTime(long createTime) {
    this.createTime = createTime;
    return this;
  }
}
