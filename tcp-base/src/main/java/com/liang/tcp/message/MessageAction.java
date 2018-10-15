package com.liang.tcp.message;

import com.liang.tcp.peer.PeerChannel;

public interface MessageAction<M extends Message> {

  byte getMessageType();

  Class<M> getMessageClass();

  <T> T action(PeerChannel peerChannel, M message);
}
