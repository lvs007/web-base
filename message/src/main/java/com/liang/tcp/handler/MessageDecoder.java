package com.liang.tcp.handler;

import com.liang.common.message.Message;
import com.liang.common.message.MessageFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageDecoder extends ByteToMessageDecoder {

  private static final Logger logger = LoggerFactory.getLogger(MessageDecoder.class);

  @Override
  protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) {
    int length = buffer.readableBytes();
    byte[] encoded = new byte[length];
    buffer.readBytes(encoded);
    try {
      Message msg = createMessage(encoded);
      out.add(msg);
    } catch (Exception e) {
      logger.error("Decode message fail!", e);
    }
  }

  private Message createMessage(byte[] encoded)
      throws IllegalAccessException, InstantiationException {
    return MessageFactory.parseMessage(encoded);
  }

}