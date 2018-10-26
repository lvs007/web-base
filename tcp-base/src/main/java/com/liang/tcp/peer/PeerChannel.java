package com.liang.tcp.peer;

import com.liang.common.message.Message;
import com.liang.common.message.SendAndReceiveMessage;
import com.liang.tcp.MessageQueue;
import com.liang.tcp.message.entity.PingMessage;
import io.netty.channel.ChannelHandlerContext;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class PeerChannel implements SendAndReceiveMessage {

  private ScheduledExecutorService pingTimer =
      Executors.newSingleThreadScheduledExecutor(r -> new Thread(r, "TcpPingTimer"));

  private ScheduledFuture<?> pingTask;

  private static final int MAX_TIME_OUT_TIMES = 3;

  private volatile boolean hasPong = true;

  private volatile boolean run = true;

  private ChannelHandlerContext ctx;

  private int failTimes = 0;

  private String groupId;

  @Autowired
  private MessageQueue messageQueue;

  public void channelActive(ChannelHandlerContext ctx) {
    this.ctx = ctx;
    messageQueue.activate(ctx, this);
    pingPongCheck();
  }

  private void pingPongCheck() {
    pingTask = pingTimer.scheduleAtFixedRate(() -> {
      if (run) {
        if (hasPong || failTimes <= MAX_TIME_OUT_TIMES) {
          if (hasPong) {
            failTimes = 0;
          } else {
            ++failTimes;
          }
          hasPong = false;
          messageQueue.sendPingPongMessage(new PingMessage(System.currentTimeMillis()));
        } else {//disconnect the peer
          close();
        }
      }
    }, 10, 10, TimeUnit.SECONDS);
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

  public void shutdown() {
    if (pingTask != null && !pingTask.isCancelled()) {
      pingTask.cancel(false);
    }
  }

  public void close() {
    run = false;
    if (ctx != null) {
      ctx.close();
    }
  }

  public void setHasPong(boolean hasPong) {
    this.hasPong = hasPong;
  }

  public String getHost() {
    InetSocketAddress inetSocketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
    return inetSocketAddress.getHostString();
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

  @Override
  public String toString() {
    return "PeerChannel{" +
        ctx.channel().remoteAddress() +
        '}';
  }
}
