package com.liang.tcp;

import com.liang.tcp.message.Message;
import com.liang.tcp.message.MessageFactory;
import com.liang.tcp.message.entity.PingMessage;
import com.liang.tcp.message.entity.PongMessage;
import com.liang.tcp.peer.PeerChannel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class MessageQueue {

  private static final Logger logger = LoggerFactory.getLogger(MessageQueue.class);

  private volatile boolean continueFlag = true;

  private ChannelHandlerContext ctx = null;

  private BlockingQueue<Message> msgSendQueue = new LinkedBlockingQueue<>();
  private BlockingQueue<Message> pingPongMsgSendQueue = new LinkedBlockingQueue<>();

  private BlockingQueue<Message> receiveMsgQueue = new LinkedBlockingQueue<>();

  private PeerChannel peerChannel;

  @Autowired
  private ThreadPool threadPool;

  @Autowired
  private MessageFactory messageFactory;


  public void activate(ChannelHandlerContext ctx, PeerChannel peerChannel) {
    this.ctx = ctx;
    this.peerChannel = peerChannel;
    threadPool.executeMessage(() -> {
      while (continueFlag) {
        send(msgSendQueue);
      }
    });
    threadPool.executeMessage(() -> {
      while (continueFlag) {
        send(pingPongMsgSendQueue);
      }
    });
    threadPool.executeMessage(() -> {
      while (continueFlag) {
        try {
          Message msg = receiveMsgQueue.take();
          messageFactory.action(peerChannel, msg);
        } catch (InterruptedException e) {
          logger.error("receive message have a error!", e);
          Thread.currentThread().interrupt();
        }
      }
    });
  }

  private void send(BlockingQueue<Message> queue) {
    try {
      Message msg = queue.take();
      ctx.writeAndFlush(msg).addListener((ChannelFutureListener) future -> {
        if (!future.isSuccess()) {
          logger.error("Fail send to {}, {}", ctx.channel().remoteAddress(),
              msg, future.cause());
        }
      });
    } catch (Exception e) {
      logger.error("Fail send to {}, error info: {}", ctx.channel().remoteAddress(),
          e.getMessage());
    }
  }

  public boolean sendMessage(Message msg) {
    logger.info("Send to {}, {} ", ctx.channel().remoteAddress(), msg);
    return msgSendQueue.offer(msg);
  }

  public boolean sendPingPongMessage(Message msg) {
    logger.info("Send to {}, {} ", ctx.channel().remoteAddress(), msg);
    return pingPongMsgSendQueue.offer(msg);
  }

  public void receivedMessage(Message msg) {
    logger.info("Receive from {}, {}", ctx.channel().remoteAddress(), msg);
    if (msg instanceof PingMessage || msg instanceof PongMessage) {
      messageFactory.action(peerChannel, msg);
    } else {
      receiveMsgQueue.offer(msg);
    }
  }

  public void close() {
    continueFlag = false;
  }

}
