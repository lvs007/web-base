/*
 * Copyright (c) [2016] [ <ether.camp> ]
 * This file is part of the ethereumJ library.
 *
 * The ethereumJ library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The ethereumJ library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ethereumJ library. If not, see <http://www.gnu.org/licenses/>.
 */
package com.liang.tcp.handler;

import com.liang.tcp.message.Message;
import com.liang.tcp.message.MessageFactory;
import com.liang.tcp.peer.PeerChannel;
import com.liang.tcp.peer.PeerChannelPool;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class MessageHandler extends SimpleChannelInboundHandler<Message> {

  private static final Logger logger = LoggerFactory.getLogger(MessageHandler.class);

  private PeerChannel peerChannel;

  @Autowired
  private MessageFactory messageFactory;

  @Autowired
  private PeerChannelPool peerChannelPool;

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    super.channelActive(ctx);
    peerChannel.channelActive(ctx);
    peerChannelPool.addPeerChannel(peerChannel);
  }

  @Override
  public void channelRead0(final ChannelHandlerContext ctx, Message msg) {
    messageFactory.action(peerChannel, msg);
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    logger.error("MessageHandler read message error!", cause);
  }

  public void close() {
    peerChannel.shutdown();
    peerChannelPool.remove(peerChannel);
  }

  public void setPeerChannel(PeerChannel peerChannel) {
    this.peerChannel = peerChannel;
  }
}