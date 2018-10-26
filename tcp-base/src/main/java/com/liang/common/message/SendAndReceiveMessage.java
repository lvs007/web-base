package com.liang.common.message;

public interface SendAndReceiveMessage {

  boolean sendMessage(Message message);

  boolean sendPingPongMessage(Message message);

  void receiveMessage(Message message);
}
