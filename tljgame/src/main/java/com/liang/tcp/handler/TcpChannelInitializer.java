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

import com.liang.tcp.peer.PeerChannel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.FixedRecvByteBufAllocator;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.ReadTimeoutHandler;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author Roman Mandeleil
 * @since 01.11.2014
 */
@Component
@Scope("prototype")
public class TcpChannelInitializer extends ChannelInitializer<NioSocketChannel> {

  private static final Logger logger = LoggerFactory.getLogger(TcpChannelInitializer.class);


  @Autowired
  private ApplicationContext ctx;

  private MessageHandler messageHandler;
  private MessageDecoder messageDecoder;
  private MessageEncoder messageEncoder;
  private PeerChannel peerChannel;

  @Override
  public void initChannel(NioSocketChannel ch) throws Exception {
    try {
      logger.info("TcpChannelInitializer,{}", ch.remoteAddress());
      messageHandler = ctx.getBean(MessageHandler.class);
      messageDecoder = ctx.getBean(MessageDecoder.class);
      messageEncoder = ctx.getBean(MessageEncoder.class);
      peerChannel = ctx.getBean(PeerChannel.class);

      messageHandler.setPeerChannel(peerChannel);
      // limit the size of receiving buffer to 1024
      ch.config().setRecvByteBufAllocator(new FixedRecvByteBufAllocator(256 * 1024));
      ch.config().setOption(ChannelOption.SO_RCVBUF, 256 * 1024);
      ch.config().setOption(ChannelOption.SO_BACKLOG, 1024);

      ch.pipeline().addLast("readTimeoutHandler", new ReadTimeoutHandler(60, TimeUnit.SECONDS));
      int maxLength = 1048576;
      int lengthFieldLength = 4;
      int ignoreLength = -4;
      int offset = 0;
      ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(maxLength,
          offset, lengthFieldLength, ignoreLength, lengthFieldLength));
      ch.pipeline().addLast("messageDecoder", messageDecoder);
      ch.pipeline().addLast(new LengthFieldPrepender(4, true));
      ch.pipeline().addLast("messageEncoder", messageEncoder);
      ch.pipeline().addLast("messageHandler", messageHandler);
      // be aware of channel closing
      ch.closeFuture().addListener((ChannelFutureListener) future -> {
        if (!future.isSuccess()) {
          logger.info("Close channel: {}, {}", future.channel().remoteAddress(), future.cause());
        } else {
          messageHandler.close();
        }
      });

    } catch (Exception e) {
      logger.error("Unexpected error: ", e);
    }
  }
}
