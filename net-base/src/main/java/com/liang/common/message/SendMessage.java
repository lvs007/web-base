package com.liang.common.message;

public interface SendMessage {

  boolean sendMessage(Message message);

  boolean sendPingPongMessage(Message message);
}
