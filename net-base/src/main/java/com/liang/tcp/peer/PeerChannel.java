package com.liang.tcp.peer;

import com.liang.common.message.Message;
import com.liang.common.message.SendMessage;
import com.liang.tcp.MessageQueue;
import io.netty.channel.ChannelHandlerContext;
import java.net.InetSocketAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
public class PeerChannel implements SendMessage {

  private static final Logger logger = LoggerFactory.getLogger(PeerChannel.class);

  private volatile boolean hasPong = true;

  private ChannelHandlerContext ctx;

  private int failTimes = 0;

  private String groupId;

  private long connectTime;

  @Autowired
  private MessageQueue messageQueue;

  public void channelActive(ChannelHandlerContext ctx) {
    this.ctx = ctx;
    messageQueue.activate(this);
    connectTime = System.nanoTime();
  }

  public boolean sendMessage(Message message) {
    return messageQueue.sendMessage(message);
  }

  public boolean sendPingPongMessage(Message message) {
    return messageQueue.sendPingPongMessage(message);
  }

  public void receiveMessage(Message message) {
    messageQueue.receivedMessage(message);
  }

  public MessageQueue getMessageQueue() {
    return messageQueue;
  }

  public PeerChannel setMessageQueue(MessageQueue messageQueue) {
    this.messageQueue = messageQueue;
    return this;
  }

  public ChannelHandlerContext getCtx() {
    return ctx;
  }

  public void close() {
    messageQueue.close();
    if (ctx != null) {
      ctx.close();
    }
  }

  public void setHasPong(boolean hasPong) {
    this.hasPong = hasPong;
  }

  public String getHost() {
    if (ctx.channel() != null) {
      InetSocketAddress inetSocketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
      return inetSocketAddress.getHostString();
    }
    return null;
  }

  public int getPort() {
    InetSocketAddress inetSocketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
    return inetSocketAddress.getPort();
  }

  public String getGroupId() {
    return groupId;
  }

  public PeerChannel setGroupId(String groupId) {
    this.groupId = groupId;
    return this;
  }

  public boolean isHasPong() {
    return hasPong;
  }

  public int getFailTimes() {
    return failTimes;
  }

  public PeerChannel setFailTimes(int failTimes) {
    this.failTimes = failTimes;
    return this;
  }

  public long getConnectTime() {
    return connectTime;
  }

  public PeerChannel setConnectTime(long connectTime) {
    this.connectTime = connectTime;
    return this;
  }

  @Override
  public String toString() {
    return "PeerChannel{" +
        ctx.channel().remoteAddress() +
        '}';
  }
}
