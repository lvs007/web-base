package com.liang.tcp.message;

import com.alibaba.fastjson.JSON;
import com.liang.tcp.message.action.MessageAction;
import com.liang.tcp.peer.PeerChannel;
import io.netty.channel.ChannelHandlerContext;
import java.util.HashMap;
import java.util.Map;
import liang.common.exception.UnExistException;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Component;

@Component
public class MessageFactory {

  private static final Map<MessageTypeEnum, MessageAction<Message>> messageActionMap = new HashMap<>();

  public Message parseMessage(byte[] encoded)
      throws InstantiationException, IllegalAccessException {

    byte type = encoded[0];
    MessageAction<Message> messageAction = select(type);
    Message message = createMessage(encoded, messageAction);
    return message;
  }

  public <T> T action(PeerChannel peerChannel, Message message) {
    MessageAction<Message> messageAction = select(message.getType());
    if (messageAction == null) {
      return null;
    }
    return messageAction.action(peerChannel, message);
  }

  private Message createMessage(byte[] encoded, MessageAction<Message> messageAction)
      throws IllegalAccessException, InstantiationException {
    byte[] rawData = ArrayUtils.subarray(encoded, 1, encoded.length);
    if (rawData == null || rawData.length == 0) {
      return messageAction.getMessageClass().newInstance();
    }
    return JSON.parseObject(Message.bytesToString(rawData), messageAction.getMessageClass());
  }

  private MessageAction<Message> select(byte type) {
    MessageTypeEnum messageTypeEnum = MessageTypeEnum.get(type);
    if (messageTypeEnum == null) {
      return null;
    }
    MessageAction<Message> messageAction = messageActionMap.get(messageTypeEnum);
    if (messageAction == null) {
      throw UnExistException.throwException("不存在这样消息的action（处理器）！");
    }
    return messageAction;
  }

  public static void register(MessageAction messageAction) {
    messageActionMap.put(messageAction.getMessageType(), messageAction);
  }

}
