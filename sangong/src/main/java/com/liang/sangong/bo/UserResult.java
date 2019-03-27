package com.liang.sangong.bo;

public class UserResult {

  private long id;
  private long userId;
  private String roomId;
  private int type;
  private int result;
  private String poke;
  private long coin;
  private long createTime;

  public enum ResultEnum {
    win(0), fail(1), ping(2);

    ResultEnum(int code) {
      this.code = code;
    }

    public int code;
  }

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

  public String getRoomId() {
    return roomId;
  }

  public UserResult setRoomId(String roomId) {
    this.roomId = roomId;
    return this;
  }

  public int getType() {
    return type;
  }

  public UserResult setType(int type) {
    this.type = type;
    return this;
  }

  public static UserResult build(long coin, long userId, String poke, String roomId, int type) {
    UserResult userResult = new UserResult();
    ResultEnum resultEnum;
    if (coin > 0) {
      resultEnum = ResultEnum.win;
    } else if (coin < 0) {
      coin = -coin;
      resultEnum = ResultEnum.fail;
    } else {
      resultEnum = ResultEnum.ping;
    }
    return userResult.setCoin(coin).setPoke(poke).setUserId(userId).setResult(resultEnum.code)
            .setRoomId(roomId).setType(type);
  }

}
