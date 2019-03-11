package com.liang.sangong.message;

import com.alibaba.fastjson.JSON;

public class Message {

  private MessageType messageType;

  public enum MessageType {
    login(1, "登录"), logout(2, "登出"),
    add(3, "进房间"), leave(4, "离开房间"),
    confirm(5, "准备"), unconfirm(6, "取消准备"),
    begin(7, "开始"), createRoom(8, "创建房间"),
    selectPeopleType(9, "选择币类型"), invite(10, "邀请用户加入房间"),
    sendInvite(11, "发送邀请"), getRoom(12, "获取房间信息"),
    returnRoom(13, "返回房间信息"), error(14, "请求失败"),
    zuozhuang(15, "坐庄"), recharge(16, "充值"), returnRecharge(17, "返回充值消息");

    MessageType(int code, String desc) {
      this.code = code;
      this.desc = desc;
    }

    public int code;
    public String desc;
  }

  public Message(MessageType messageType) {
    this.messageType = messageType;
  }

  public MessageType getMessageType() {
    return messageType;
  }

  public Message setMessageType(MessageType messageType) {
    this.messageType = messageType;
    return this;
  }

  @Override
  public String toString() {
    return JSON.toJSONString(this);
  }
}
