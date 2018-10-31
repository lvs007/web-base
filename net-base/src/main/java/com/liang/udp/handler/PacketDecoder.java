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
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PacketDecoder extends MessageToMessageDecoder<DatagramPacket> {

  private static final Logger logger = LoggerFactory.getLogger(PacketDecoder.class);

  private static final int MAXSIZE = 2048;

  @Override
  public void decode(ChannelHandlerContext ctx, DatagramPacket packet, List<Object> out) {
    ByteBuf buf = packet.content();
    int length = buf.readableBytes();
//    if (length <= 1 || length >= MAXSIZE) {
//      logger.error("UDP rcv bad packet, from {} length = {}", ctx.channel().remoteAddress(),
//          length);
//      return;
//    }
    byte[] encoded = new byte[length];
    buf.readBytes(encoded);
    try {
      Message message = MessageFactory.parseMessage(encoded);
      message.setAddress(packet.sender());
      out.add(message);
    } catch (Exception e) {
      logger.error("Parse msg failed, type {}, len {}, address {}", encoded[0], encoded.length,
          packet.sender());
    }
  }
}
