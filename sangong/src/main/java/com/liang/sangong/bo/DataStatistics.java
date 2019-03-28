package com.liang.sangong.bo;

import com.liang.dao.jdbc.annotation.Transient;

public class DataStatistics {

  private long id;
  private long userId;
  private long shuCount;
  private long shuAmount;
  private long winCount;
  private long winAmount;
  private int type;
  private long createTime;
  private long updateTime;

  @Transient
  private boolean news;

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

  public long getShuAmount() {
    return shuAmount;
  }

  public DataStatistics setShuAmount(long shuAmount) {
    this.shuAmount = shuAmount;
    return this;
  }

  public long getWinAmount() {
    return winAmount;
  }

  public DataStatistics setWinAmount(long winAmount) {
    this.winAmount = winAmount;
    return this;
  }

  public int getType() {
    return type;
  }

  public DataStatistics setType(int type) {
    this.type = type;
    return this;
  }

  public long getUpdateTime() {
    return updateTime;
  }

  public DataStatistics setUpdateTime(long updateTime) {
    this.updateTime = updateTime;
    return this;
  }

  public boolean isNews() {
    return news;
  }

  public DataStatistics setNews(boolean news) {
    this.news = news;
    return this;
  }

  public static DataStatistics build(long userId, int type) {
    DataStatistics data = new DataStatistics();
    data.setCreateTime(System.currentTimeMillis())
        .setUpdateTime(System.currentTimeMillis()).setUserId(userId)
        .setType(type).setShuAmount(0).setShuCount(0).setWinAmount(0)
        .setWinCount(0).setNews(true);
    return data;
  }
}
