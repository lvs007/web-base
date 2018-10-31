package com.liang.tcp.handler;

import com.liang.common.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

//@Component
//@Scope(value = "prototype", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class MessageEncoder extends MessageToByteEncoder<Message> {

  private static final Logger logger = LoggerFactory.getLogger(MessageEncoder.class);

  @Override
  protected void encode(ChannelHandlerContext channelHandlerContext, Message message,
      ByteBuf byteBuf) {
    byteBuf.writeBytes(message.sendData());
  }
}
