package com.liang.udp.message.action;

import com.liang.common.message.AbstractMessageAction;
import com.liang.tcp.peer.PeerChannel;
import com.liang.udp.message.UdpMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class UdpMessageAction extends AbstractMessageAction<UdpMessage> {

  private static final Logger logger = LoggerFactory.getLogger(UdpMessageAction.class);

  @Override
  public byte getMessageType() {
    return UdpMessage.mType;
  }

  @Override
  public Class<UdpMessage> getMessageClass() {
    return UdpMessage.class;
  }

  @Override
  public <T> T action(PeerChannel peerChannel, UdpMessage message) {
    logger.info("UdpMessageAction : {}", message);
    return null;
  }
}
