package com.liang.tcp.message;

public class AddGroupMessage extends Message {

  private String groupId;

  public AddGroupMessage() {
    this(null);
  }

  public AddGroupMessage(String groupId) {
    super(MessageTypeEnum.ADD_GROUP.value);
    this.groupId = groupId;
  }

  public String getGroupId() {
    return groupId;
  }

  public AddGroupMessage setGroupId(String groupId) {
    this.groupId = groupId;
    return this;
  }
}
