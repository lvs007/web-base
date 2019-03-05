package com.liang.message;

public class Message {

  private MessageType messageType;

  public enum MessageType {
    login, logout;
  }

  public Message(MessageType messageType) {
    this.messageType = messageType;
  }
}
