package com.liang.tcp;

import com.liang.common.message.Message;
import com.liang.common.message.MessageFactory;
import com.liang.tcp.peer.PeerChannel;
import com.liang.tcp.peer.PeerChannelPool;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import java.util.concurrent.BlockingQueue;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

public class MessageQueueConsumer {

  private static final Logger logger = LoggerFactory.getLogger(MessageQueueConsumer.class);

  private volatile boolean continueFlag = true;

  private PeerChannelPool peerChannelPool;

  private ThreadPool threadPool;

  public MessageQueueConsumer(PeerChannelPool peerChannelPool, ThreadPool threadPool) {
    this.peerChannelPool = peerChannelPool;
    this.threadPool = threadPool;
  }

  public void start() {
    Thread sendThread = new Thread(() -> {
      while (continueFlag) {
        for (PeerChannel peerChannel : peerChannelPool.getPeerChannelMap().values()) {
          MessageQueue messageQueue = peerChannel.getMessageQueue();
          if (messageQueue.getMsgSendQueue().size() > 0) {
            send(peerChannel, messageQueue.getMsgSendQueue());
          }
          if (messageQueue.getPingPongMsgSendQueue().size() > 0) {
            send(peerChannel, messageQueue.getPingPongMsgSendQueue());
          }
        }
      }
    });
    sendThread.setName("Send-Msg-Thread");
    sendThread.setDaemon(true);
    sendThread.start();

    Thread receiveThread = new Thread(() -> {
      while (continueFlag) {
        for (PeerChannel peerChannel : peerChannelPool.getPeerChannelMap().values()) {
          MessageQueue messageQueue = peerChannel.getMessageQueue();
          if (messageQueue.getReceiveMsgQueue().size() > 0) {
            receive(peerChannel, messageQueue.getReceiveMsgQueue());
          }
        }
      }
    });
    receiveThread.setName("Receive-Msg-Thread");
    receiveThread.setDaemon(true);
    receiveThread.start();
  }

  private void send(PeerChannel peerChannel, BlockingQueue<Message> queue) {
    threadPool.executeSendMessage(() -> {
      ChannelHandlerContext ctx = peerChannel.getCtx();
      try {
        Message msg = queue.poll();
        if (ctx.channel().isActive() && msg != null) {
          logger.info("Send to {}, {} ", ctx.channel().remoteAddress(), msg);
          if (peerChannel.getMessageQueue().getPingPongMsgSendQueue().size() > 0) {
            peerChannel.setHasPong(true);
            peerChannel.getMessageQueue().getPingPongMsgSendQueue().clear();
          }
          ctx.writeAndFlush(msg).addListener((ChannelFutureListener) future -> {
            if (!future.isSuccess()) {
              logger
                  .error("Fail send to {}, {}", ctx.channel().remoteAddress(), msg, future.cause());
            }
          });
        }
      } catch (Exception e) {
        logger.error("Fail send to {}, error info: {}", ctx.channel().remoteAddress(),
            e.getMessage());
      }
    });
  }

  private void receive(PeerChannel peerChannel, BlockingQueue<Message> queue) {
    threadPool.executeReceiveMessage(() -> {
      try {
        Message msg = queue.poll();
        if (msg != null) {
          logger.info("Receive from {}, {}", peerChannel.getCtx().channel().remoteAddress(), msg);
          MessageFactory.action(peerChannel, msg);
        }
      } catch (Exception e) {
        logger.error("receive message have a error!", e);
      }
    });
  }

  public void close() {
    continueFlag = false;
  }

}
