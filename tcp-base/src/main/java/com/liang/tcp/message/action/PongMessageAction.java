package com.liang.tcp.message.action;

import com.liang.tcp.message.AbstractMessageAction;
import com.liang.tcp.message.entity.PongMessage;
import com.liang.tcp.peer.PeerChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PongMessageAction extends AbstractMessageAction<PongMessage> {

  private static final Logger logger = LoggerFactory.getLogger(PongMessageAction.class);

  @Override
  public byte getMessageType() {
    return 2;
  }

  @Override
  public Class<PongMessage> getMessageClass() {
    return PongMessage.class;
  }

  @Override
  public <T> T action(PeerChannel peerChannel, PongMessage message) {
    peerChannel.setHasPong(true);
    return null;
  }

}
