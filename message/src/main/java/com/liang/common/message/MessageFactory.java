package com.liang.common.message;

import com.liang.common.EnDecoder;
import com.liang.common.exception.UnExistException;
import com.liang.tcp.peer.PeerChannel;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.ArrayUtils;

public class MessageFactory {

  private static final Map<Byte, MessageAction<Message>> messageActionMap = new HashMap<>();

  public static Message parseMessage(byte[] encoded)
      throws InstantiationException, IllegalAccessException {

    byte type = encoded[0];
    MessageAction<Message> messageAction = select(type);
    Message message = createMessage(encoded, messageAction);
    return message;
  }

  public static void action(PeerChannel peerChannel, Message message) {
    MessageAction<Message> messageAction = select(message.getType());
    if (messageAction == null) {
      return;
    }
    messageAction.action(peerChannel, message);
  }

  private static Message createMessage(byte[] encoded, MessageAction<Message> messageAction)
      throws IllegalAccessException, InstantiationException {
    byte[] rawData = ArrayUtils.subarray(encoded, 1, encoded.length);
    if (rawData == null || rawData.length == 0) {
      return messageAction.getMessageClass().newInstance();
    }
    return EnDecoder.decoder(rawData, messageAction.getMessageClass());
//    return JSON.parseObject(Message.bytesToString(rawData), messageAction.getMessageClass());
  }

  private static MessageAction<Message> select(byte type) {
    MessageAction<Message> messageAction = messageActionMap.get(type);
    if (messageAction == null) {
      throw UnExistException.throwException("不存在这样消息的action（处理器）！");
    }
    return messageAction;
  }

  public static void register(MessageAction messageAction) {
    messageActionMap.put(messageAction.getMessageType(), messageAction);
  }

}
