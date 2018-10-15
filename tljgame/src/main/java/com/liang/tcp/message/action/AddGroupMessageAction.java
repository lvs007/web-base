package com.liang.tcp.message.action;

import com.liang.tcp.message.entity.AddGroupMessage;
import com.liang.tcp.message.MessageTypeEnum;
import com.liang.tcp.peer.PeerChannel;
import com.liang.tcp.peer.PeerChannelPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AddGroupMessageAction extends AbstractMessageAction<AddGroupMessage> {

  @Autowired
  private PeerChannelPool peerChannelPool;

  @Override
  public MessageTypeEnum getMessageType() {
    return MessageTypeEnum.ADD_GROUP;
  }

  @Override
  public Class<AddGroupMessage> getMessageClass() {
    return AddGroupMessage.class;
  }

  @Override
  public <T> T action(PeerChannel peerChannel, AddGroupMessage message) {
    peerChannelPool.addGroup(message.getGroupId(), peerChannel);
    return null;
  }
}
