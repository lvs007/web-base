package com.liang.tcp;

import com.liang.tcp.message.Message;
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

  private volatile boolean sendMsgFlag = true;
  
  private ChannelHandlerContext ctx = null;

  private BlockingQueue<Message> msgQueue = new LinkedBlockingQueue<>();
  private BlockingQueue<Message> pingPongMsgQueue = new LinkedBlockingQueue<>();

  @Autowired
  private ThreadPool threadPool;

  public void activate(ChannelHandlerContext ctx) {
    this.ctx = ctx;
    threadPool.executeSendMessage(() -> {
      while (sendMsgFlag) {
        send(msgQueue);
      }
    });
    threadPool.executeSendMessage(() -> {
      while (sendMsgFlag) {
        send(pingPongMsgQueue);
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
    return msgQueue.offer(msg);
  }

  public boolean sendPingPongMessage(Message msg) {
    logger.info("Send to {}, {} ", ctx.channel().remoteAddress(), msg);
    return pingPongMsgQueue.offer(msg);
  }

  public void receivedMessage(Message msg) {
    logger.info("Receive from {}, {}", ctx.channel().remoteAddress(), msg);
  }

  public void close() {
    sendMsgFlag = false;
  }

}
