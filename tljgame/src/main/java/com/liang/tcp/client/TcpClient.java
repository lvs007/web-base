package com.liang.tcp.client;

import com.liang.tcp.handler.TcpChannelInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.DefaultMessageSizeEstimator;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class TcpClient {

  private static final Logger logger = LoggerFactory.getLogger("PeerClient");

  private EventLoopGroup workerGroup;

  @Autowired
  private ApplicationContext ctx;

  public TcpClient() {
    workerGroup = new NioEventLoopGroup(0, new ThreadFactory() {
      AtomicInteger cnt = new AtomicInteger(0);

      @Override
      public Thread newThread(Runnable r) {
        return new Thread(r, "TcpClientWorker-" + cnt.getAndIncrement());
      }
    });
  }

  public void connect(String host, int port) {
    try {
      connectAsync(host, port).sync().channel().closeFuture().sync();
    } catch (Exception e) {
      logger.info("TcpClient: Can't connect to " + host + ":" + port + " (" + e.getMessage() + ")");
    }
  }

  public ChannelFuture connectAsync(String host, int port) {
    return connectImpl(host, port).addListener((ChannelFutureListener) future -> {
      if (!future.isSuccess()) {
        logger.error("connect to {}:{} fail,cause:{}", host, port,
            future.cause().getMessage());
        future.channel().close();
      }
    });
  }

  public ChannelFuture connectImpl(String host, int port) {

    logger.info("connect peer {} {}", host, port);

    TcpChannelInitializer tronChannelInitializer = ctx.getBean(TcpChannelInitializer.class);

    Bootstrap b = new Bootstrap();
    b.group(workerGroup);
    b.channel(NioSocketChannel.class);

    b.option(ChannelOption.SO_KEEPALIVE, true);
    b.option(ChannelOption.MESSAGE_SIZE_ESTIMATOR, DefaultMessageSizeEstimator.DEFAULT);
    b.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 60 * 1000);
    b.remoteAddress(host, port);

    b.handler(tronChannelInitializer);

    // Start the client.
    return b.connect();
  }

  public void close() {
    workerGroup.shutdownGracefully();
    workerGroup.terminationFuture().syncUninterruptibly();
  }
}
