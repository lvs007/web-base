package com.liang.tcp;

import com.liang.common.MessageSendFactory;
import com.liang.common.message.MessageAction;
import com.liang.tcp.client.TcpClient;
import com.liang.tcp.handler.TcpChannelInitializer;
import com.liang.tcp.message.action.AddGroupMessageAction;
import com.liang.tcp.message.action.FileMessageAction;
import com.liang.tcp.message.action.PingMessageAction;
import com.liang.tcp.message.action.PongMessageAction;
import com.liang.tcp.peer.PeerChannelPool;
import com.liang.tcp.peer.PeerController;
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

public class TcpServer {

  private static final Logger logger = LoggerFactory.getLogger(TcpServer.class);

  private EventLoopGroup bossGroup;
  private EventLoopGroup workerGroup;
  private ChannelFuture channelFuture;

  private TcpChannelInitializer tcpChannelInitializer;

  private MessageQueueConsumer messageQueueConsumer;
  private PingPongManager pingPongManager;
  private PeerChannelPool peerChannelPool;
  private ThreadPool threadPool;
  private PeerController peerController;
  private TcpClient tcpClient;

  public TcpServer() {
    //
    threadPool = new ThreadPool();
    peerController = new PeerController();
    peerChannelPool = new PeerChannelPool(threadPool, peerController);
    messageQueueConsumer = new MessageQueueConsumer(peerChannelPool, threadPool);
    pingPongManager = new PingPongManager(peerChannelPool);
    tcpChannelInitializer = new TcpChannelInitializer(peerChannelPool);
    tcpClient = new TcpClient(peerChannelPool);
    MessageSendFactory.setTcpClient(tcpClient);
    //
    register(new AddGroupMessageAction(peerChannelPool));
    register(new FileMessageAction());
    register(new PingMessageAction());
    register(new PongMessageAction());
  }

  public TcpServer register(MessageAction messageAction) {
    return this;
  }

  public void startAsync(int port) {
    new Thread(() -> start(port), "tcp-server").start();
    messageQueueConsumer.start();
    pingPongManager.pingPongCheck();
  }

  public void start(int port) {
    bossGroup = new NioEventLoopGroup(1);
    workerGroup = new NioEventLoopGroup(16);

    try {

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

  public void close() {
    messageQueueConsumer.close();
    pingPongManager.close();
    peerChannelPool.close();
    threadPool.destroy();
  }
}
