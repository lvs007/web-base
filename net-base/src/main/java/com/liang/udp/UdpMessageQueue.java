package com.liang.udp;

import com.liang.common.message.Message;
import com.liang.common.message.MessageFactory;
import com.liang.tcp.MessageQueue;
import com.liang.tcp.ThreadPool;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.socket.DatagramPacket;
import java.net.InetSocketAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UdpMessageQueue {

  private static final Logger logger = LoggerFactory.getLogger(MessageQueue.class);

  private volatile boolean continueFlag = true;

  private BlockingQueue<Message> msgSendQueue = new LinkedBlockingQueue<>();

  private BlockingQueue<Message> receiveMsgQueue = new LinkedBlockingQueue<>();

  private Channel channel;

  @Autowired
  private ThreadPool threadPool;

  @Autowired
  private MessageFactory messageFactory;

  public void activate(Channel channel) {
    this.channel = channel;
    threadPool.executeSendMessage(() -> {
      while (continueFlag) {
        send(msgSendQueue);
      }
    });
    threadPool.executeSendMessage(() -> {
      while (continueFlag) {
        try {
          Message msg = receiveMsgQueue.take();
          messageFactory.action(null, msg);
        } catch (InterruptedException e) {
          logger.error("receive message have a error!", e);
          Thread.currentThread().interrupt();
        }
      }
    });
  }

  private void send(BlockingQueue<Message> queue) {
    InetSocketAddress address = null;
    try {
      Message message = queue.take();
      logger.debug("Send udp msg type {}, len {} from {} ",
          message.getType(), message.sendData().length, message.getAddress());
      address = message.getAddress();
      sendPacket(message.sendData(), address);
    } catch (Exception e) {
      logger.error("Fail send to {}, error info: {}", address.getAddress(), e);
    }
  }

  private void sendPacket(byte[] wire, InetSocketAddress address) {
    DatagramPacket packet = new DatagramPacket(Unpooled.copiedBuffer(wire), address);
    channel.write(packet);
    channel.flush();
  }

  public boolean sendMessage(Message msg) {
    return msgSendQueue.offer(msg);
  }

  public void receivedMessage(Message msg) {
    logger.info("Receive from {}, {}", msg.getAddress().getAddress(), msg);
    receiveMsgQueue.offer(msg);
  }

  public void close() {
    continueFlag = false;
  }

}
