package com.liang.tcp.message.action;

import com.liang.tcp.message.Message;
import com.liang.tcp.message.MessageTypeEnum;
import com.liang.tcp.peer.PeerChannel;
import io.netty.channel.ChannelHandlerContext;

public interface MessageAction<M extends Message> {

  MessageTypeEnum getMessageType();

  Class<M> getMessageClass();

  <T> T action(PeerChannel peerChannel, M message);
}
