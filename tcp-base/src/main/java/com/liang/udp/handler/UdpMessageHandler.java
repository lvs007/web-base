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
package com.liang.udp.handler;

import com.liang.common.message.Message;
import com.liang.common.message.MessageFactory;
import com.liang.udp.UdpMsgSendAndReceive;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class UdpMessageHandler extends SimpleChannelInboundHandler<Message> {

  private static final org.slf4j.Logger logger = LoggerFactory.getLogger("MessageHandler");

  @Autowired
  private MessageFactory messageFactory;

  @Autowired
  private UdpMsgSendAndReceive udpMsgSendAndReceive;

  public UdpMessageHandler() {
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
  }

  @Override
  public void channelRead0(ChannelHandlerContext ctx, Message message) {
    udpMsgSendAndReceive.receiveMessage(message);
  }

  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) {
    ctx.flush();
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    logger.info("exception caught, {} {}", ctx.channel().remoteAddress(), cause.getMessage());
    ctx.close();
  }

  public void set(NioDatagramChannel channel) {
    udpMsgSendAndReceive.activate(channel);
  }
}
