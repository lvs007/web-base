package com.liang.tcp.handler;

import com.liang.tcp.message.Message;
import com.liang.tcp.message.MessageFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class MessageDecoder extends ByteToMessageDecoder {

  private static final Logger logger = LoggerFactory.getLogger(MessageDecoder.class);

  @Autowired
  private MessageFactory messageFactory;

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
    return messageFactory.parseMessage(encoded);
  }

}