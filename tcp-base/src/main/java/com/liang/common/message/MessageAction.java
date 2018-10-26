package com.liang.common.message;

import com.liang.common.message.Message;
import com.liang.tcp.peer.PeerChannel;

public interface MessageAction<M extends Message> {

  byte getMessageType();

  Class<M> getMessageClass();

  <T> T action(PeerChannel peerChannel, M message);
}
