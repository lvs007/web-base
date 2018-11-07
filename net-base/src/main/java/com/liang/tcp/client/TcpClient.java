package com.liang.tcp.client;

import com.liang.tcp.handler.TcpChannelInitializer;
import com.liang.tcp.peer.PeerChannel;
import com.liang.tcp.peer.PeerChannelPool;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.DefaultMessageSizeEstimator;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.net.InetSocketAddress;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import liang.common.util.ThreadUtils;
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

  @Autowired
  private PeerChannelPool peerChannelPool;

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

  public PeerChannel connectSync(String host, int port) {
    PeerChannel peerChannel = getPeerChannel(host, port);
    if (peerChannel != null) {
      return peerChannel;
    }
    ChannelFuture channelFuture = connectAsync(host, port);
    return getPeerChannel(channelFuture);
  }

  public ChannelFuture connectAsync(String host, int port) {
    PeerChannel peerChannel = getPeerChannel(host, port);
    if (peerChannel != null) {
      return null;
    }
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

  public boolean isConnectFinish(ChannelFuture channelFuture) {
    return channelFuture.channel().isActive();
  }

  public PeerChannel getPeerChannel(String host, int port) {
    return peerChannelPool.getPeerChannel(host, port);
  }

  public PeerChannel getPeerChannel(ChannelFuture future) {
    int tryTimes = 1;
    while (!isConnectFinish(future) && tryTimes <= 30) {
      ThreadUtils.sleep(10 * tryTimes);
      ++tryTimes;
    }
    if (future.channel() == null || future.channel().remoteAddress() == null) {
      return null;
    }
    InetSocketAddress inetSocketAddress = (InetSocketAddress) future.channel().remoteAddress();
    String host = inetSocketAddress.getHostString();
    int port = inetSocketAddress.getPort();
    return peerChannelPool.getPeerChannel(host, port);
  }

  public void close() {
    workerGroup.shutdownGracefully();
    workerGroup.terminationFuture().syncUninterruptibly();
  }
}
