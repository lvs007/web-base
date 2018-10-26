package com.liang.udp;

import com.liang.udp.handler.UdpChannelInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UdpServer {

  private static final Logger logger = LoggerFactory.getLogger(UdpServer.class);

  private Channel channel;

  @Autowired
  private UdpChannelInitializer udpChannelInitializer;

  public void startAsync(int port) {
    new Thread(() -> {
      try {
        start(port);
      } catch (Exception e) {
        logger.error("Start udp server fail!", e);
      }
    }, "udp-server").start();
  }

  public void start(int port) throws Exception {
    NioEventLoopGroup group = new NioEventLoopGroup(1);
    try {
      Bootstrap b = new Bootstrap();
      b.group(group)
          .channel(NioDatagramChannel.class)
          .handler(udpChannelInitializer)
          .option(ChannelOption.SO_BROADCAST, true)// 支持广播
          .option(ChannelOption.SO_BACKLOG, 128)
          .option(ChannelOption.SO_RCVBUF, 1024 * 1024)// 设置UDP读缓冲区为1M
          .option(ChannelOption.SO_SNDBUF, 1024 * 1024);// 设置UDP写缓冲区为1M
      channel = b.bind(port).sync().channel();
      logger.info("Udp server started, bind port {}", port);
      channel.closeFuture().sync();
    } catch (Exception e) {
      logger.error("Start discovery server with port {} failed.", port, e);
    } finally {
      group.shutdownGracefully().sync();
    }
  }

}
