package com.liang.tcp;

import com.liang.tcp.handler.TcpChannelInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.DefaultMessageSizeEstimator;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class TcpServer {

  private static final Logger logger = LoggerFactory.getLogger(TcpServer.class);

  private EventLoopGroup bossGroup;
  private EventLoopGroup workerGroup;
  private ChannelFuture channelFuture;

  private TcpChannelInitializer tcpChannelInitializer;

  @Autowired
  private ApplicationContext ctx;

  @Autowired
  private MessageQueueConsumer messageQueueConsumer;

  @Autowired
  private PingPongManager pingPongManager;

  public void startAsync(int port) {
    new Thread(() -> start(port), "tcp-server").start();
    messageQueueConsumer.start();
    pingPongManager.pingPongCheck();
  }

  public void start(int port) {

    bossGroup = new NioEventLoopGroup(1);
    workerGroup = new NioEventLoopGroup(16);

    try {
      tcpChannelInitializer = ctx.getBean(TcpChannelInitializer.class);

      ServerBootstrap b = new ServerBootstrap();
      b.group(bossGroup, workerGroup);
      b.channel(NioServerSocketChannel.class);
      b.option(ChannelOption.SO_KEEPALIVE, true);
      b.option(ChannelOption.MESSAGE_SIZE_ESTIMATOR, DefaultMessageSizeEstimator.DEFAULT);
      b.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3600);

      b.handler(new LoggingHandler());
      b.childHandler(tcpChannelInitializer);

      // Start the client.
      logger.info("TCP listener started, bind port {}", port);

      channelFuture = b.bind(port).sync();

      // Wait until the connection is closed.
      channelFuture.channel().closeFuture().sync();

      logger.info("TCP listener is closed");

    } catch (Exception e) {
      logger.error("Start TCP server failed.", e);
    } finally {
      workerGroup.shutdownGracefully();
      bossGroup.shutdownGracefully();
    }
  }
}
