package com.liang.udp;

import com.liang.common.message.Message;
import com.liang.common.message.SendMessage;
import liang.common.exception.NotSupportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UdpMessageSender implements SendMessage {

  private static final Logger logger = LoggerFactory.getLogger(UdpMessageSender.class);

  @Autowired
  private UdpMessageQueue udpMessageQueue;

  @Override
  public boolean sendMessage(Message message) {
    return udpMessageQueue.sendMessage(message);
  }

  @Override
  public boolean sendPingPongMessage(Message message) {
    throw NotSupportException.throwException("Udp not support ping pong message");
  }
}
