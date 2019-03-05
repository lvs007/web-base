package com.liang.tcp;

import com.liang.common.message.Message;
import com.liang.common.message.MessageFactory;
import com.liang.tcp.message.entity.PingMessage;
import com.liang.tcp.message.entity.PongMessage;
import com.liang.tcp.peer.PeerChannel;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

public class MessageQueue {

  private static final Logger logger = LoggerFactory.getLogger(MessageQueue.class);

  private BlockingQueue<Message> msgSendQueue = new LinkedBlockingQueue<>();
  private BlockingQueue<Message> pingPongMsgSendQueue = new LinkedBlockingQueue<>();

  private BlockingQueue<Message> receiveMsgQueue = new LinkedBlockingQueue<>();

  private PeerChannel peerChannel;

  public void activate(PeerChannel peerChannel) {
    this.peerChannel = peerChannel;
  }

  public boolean sendMessage(Message msg) {
    return msgSendQueue.offer(msg);
  }

  public boolean sendPingPongMessage(Message msg) {
    return pingPongMsgSendQueue.offer(msg);
  }

  public void receivedMessage(Message msg) {
    if (msg instanceof PingMessage || msg instanceof PongMessage) {
      MessageFactory.action(peerChannel, msg);
    } else {
      receiveMsgQueue.offer(msg);
    }
  }

  public void close() {
    msgSendQueue.clear();
    pingPongMsgSendQueue.clear();
  }

  public BlockingQueue<Message> getMsgSendQueue() {
    return msgSendQueue;
  }

  public BlockingQueue<Message> getPingPongMsgSendQueue() {
    return pingPongMsgSendQueue;
  }

  public BlockingQueue<Message> getReceiveMsgQueue() {
    return receiveMsgQueue;
  }
}
