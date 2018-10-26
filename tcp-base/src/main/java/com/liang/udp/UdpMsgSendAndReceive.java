package com.liang.udp;

import com.liang.common.message.Message;
import com.liang.common.message.SendAndReceiveMessage;
import io.netty.channel.Channel;
import liang.common.exception.NotSupportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UdpMsgSendAndReceive implements SendAndReceiveMessage {

  private static final Logger logger = LoggerFactory.getLogger(UdpMsgSendAndReceive.class);

  private volatile boolean start = false;

  @Autowired
  private UdpMessageQueue udpMessageQueue;

  public void activate(Channel channel) {
    udpMessageQueue.activate(channel);
    start = true;
  }

  @Override
  public boolean sendMessage(Message message) {
    validStart();
    return udpMessageQueue.sendMessage(message);
  }

  @Override
  public boolean sendPingPongMessage(Message message) {
    throw NotSupportException.throwException("Udp not support ping pong message");
  }

  @Override
  public void receiveMessage(Message message) {
    validStart();
    udpMessageQueue.receivedMessage(message);
  }

  public void validStart() {
    if (!start) {
      throw NotSupportException.throwException("Udp server not start!");
    }
  }
}
