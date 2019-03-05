package com.liang.tcp;

import com.liang.common.message.Message;
import com.liang.common.message.SendMessageService;
import com.liang.tcp.peer.PeerChannel;

public class TcpMessageSender implements SendMessageService {

  private PeerChannel peerChannel;

  public TcpMessageSender(PeerChannel peerChannel) {
    this.peerChannel = peerChannel;
  }

  @Override
  public boolean sendMessage(Message message) {
    return peerChannel.sendMessage(message);
  }

  @Override
  public boolean sendPingPongMessage(Message message) {
    return false;
  }
}
