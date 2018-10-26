package com.liang.tcp.message.action;

import com.liang.common.message.AbstractMessageAction;
import com.liang.tcp.message.entity.PingMessage;
import com.liang.tcp.message.entity.PongMessage;
import com.liang.tcp.peer.PeerChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PingMessageAction extends AbstractMessageAction<PingMessage> {

  private static final Logger logger = LoggerFactory.getLogger(PingMessageAction.class);

  @Override
  public byte getMessageType() {
    return PingMessage.mType;
  }

  @Override
  public Class<PingMessage> getMessageClass() {
    return PingMessage.class;
  }

  @Override
  public <T> T action(PeerChannel peerChannel, PingMessage message) {
    PongMessage pongMessage = new PongMessage(message.getSendTime(), System.currentTimeMillis());
    peerChannel.sendPingPongMessage(pongMessage);
    return null;
  }
}
