package com.liang.tcp.message.entity;

import com.liang.tcp.message.Message;

public class AddGroupMessage extends Message {

  private String groupId;

  public AddGroupMessage() {
    this(null);
  }

  public AddGroupMessage(String groupId) {
    super((byte) 3);
    this.groupId = groupId;
  }

  public String getGroupId() {
    return groupId;
  }

  public AddGroupMessage setGroupId(String groupId) {
    this.groupId = groupId;
    return this;
  }

  @Override
  public String toString() {
    return "AddGroupMessage{" +
        "groupId=" + groupId +
        ", type=" + type +
        '}';
  }
}
