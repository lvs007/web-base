package com.liang.tcp.message.action;

import com.liang.common.message.AbstractMessageAction;
import com.liang.tcp.message.entity.PongMessage;
import com.liang.tcp.peer.PeerChannel;

public class PongMessageAction extends AbstractMessageAction<PongMessage> {

  @Override
  public byte getMessageType() {
    return PongMessage.mType;
  }

  @Override
  public Class<PongMessage> getMessageClass() {
    return PongMessage.class;
  }

  @Override
  public void action(PeerChannel peerChannel, PongMessage message) {
    peerChannel.setHasPong(true);
  }

}
