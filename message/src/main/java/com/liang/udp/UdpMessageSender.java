package com.liang.udp;

import com.liang.common.exception.NotSupportException;
import com.liang.common.message.Message;
import com.liang.common.message.SendMessageService;
import java.net.InetSocketAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UdpMessageSender implements SendMessageService {

  private static final Logger logger = LoggerFactory.getLogger(UdpMessageSender.class);

  private UdpMessageQueue udpMessageQueue;

  private InetSocketAddress address;

  public UdpMessageSender(UdpMessageQueue udpMessageQueue) {
    this.udpMessageQueue = udpMessageQueue;
  }

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
