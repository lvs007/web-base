package com.liang.tcp.message.action;

import com.liang.common.message.AbstractMessageAction;
import com.liang.tcp.message.entity.AddGroupMessage;
import com.liang.tcp.peer.PeerChannel;
import com.liang.tcp.peer.PeerChannelPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

public class AddGroupMessageAction extends AbstractMessageAction<AddGroupMessage> {

  private PeerChannelPool peerChannelPool;

  public AddGroupMessageAction(PeerChannelPool peerChannelPool) {
    this.peerChannelPool = peerChannelPool;
  }

  @Override
  public byte getMessageType() {
    return AddGroupMessage.mType;
  }

  @Override
  public Class<AddGroupMessage> getMessageClass() {
    return AddGroupMessage.class;
  }

  @Override
  public void action(PeerChannel peerChannel, AddGroupMessage message) {
    peerChannelPool.addGroup(message.getGroupId(), peerChannel);
  }
}
