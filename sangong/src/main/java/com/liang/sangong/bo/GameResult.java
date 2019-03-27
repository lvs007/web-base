package com.liang.sangong.bo;

public class GameResult {

  private long id;
  private String roomId;
  private String userPoke;
  private long createTime;

  public String getRoomId() {
    return roomId;
  }

  public GameResult setRoomId(String roomId) {
    this.roomId = roomId;
    return this;
  }

  public String getUserPoke() {
    return userPoke;
  }

  public GameResult setUserPoke(String userPoke) {
    this.userPoke = userPoke;
    return this;
  }

  public long getCreateTime() {
    return createTime;
  }

  public GameResult setCreateTime(long createTime) {
    this.createTime = createTime;
    return this;
  }

  public static GameResult build(String roomId, String userPoke) {
    GameResult gameResult = new GameResult();
    return gameResult.setRoomId(roomId).setUserPoke(userPoke)
        .setCreateTime(System.currentTimeMillis());
  }
}
