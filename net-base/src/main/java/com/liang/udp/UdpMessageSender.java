package com.liang.udp;

import com.liang.common.exception.NotSupportException;
import com.liang.common.message.Message;
import com.liang.common.message.SendMessageService;
import java.net.InetSocketAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UdpMessageSender implements SendMessageService {

  private static final Logger logger = LoggerFactory.getLogger(UdpMessageSender.class);

  @Autowired
  private UdpMessageQueue udpMessageQueue;

  private InetSocketAddress address;

  @Override
  public boolean sendMessage(Message message) {
    if (address != null) {
      message.setAddress(address);
    }
    return udpMessageQueue.sendMessage(message);
  }

  @Override
  public boolean sendPingPongMessage(Message message) {
    throw NotSupportException.throwException("Udp not support ping pong message");
  }

  public UdpMessageSender setAddress(String host, int port) {
    this.address = new InetSocketAddress(host, port);
    return this;
  }
}
