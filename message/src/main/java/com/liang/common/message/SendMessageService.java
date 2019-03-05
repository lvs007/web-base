package com.liang.common.message;

public interface SendMessageService {

  boolean sendMessage(Message message);

  boolean sendPingPongMessage(Message message);
}
