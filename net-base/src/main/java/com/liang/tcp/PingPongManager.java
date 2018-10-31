package com.liang.tcp;

import com.liang.tcp.message.entity.PingMessage;
import com.liang.tcp.peer.PeerChannel;
import com.liang.tcp.peer.PeerChannelPool;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PingPongManager {

  private static final Logger logger = LoggerFactory.getLogger(PingPongManager.class);

  private ScheduledExecutorService pingTimer =
      Executors.newSingleThreadScheduledExecutor(r -> new Thread(r, "TcpPingTimer"));

  private ScheduledFuture<?> pingTask;

  private volatile boolean run = true;

  private static final int MAX_TIME_OUT_TIMES = 3;

  @Autowired
  private PeerChannelPool peerChannelPool;

  public void pingPongCheck() {
    pingTask = pingTimer.scheduleAtFixedRate(() -> {
      if (run) {
        for (PeerChannel peerChannel : peerChannelPool.getPeerChannelMap().values()) {
          if (peerChannel.isHasPong() || peerChannel.getFailTimes() <= MAX_TIME_OUT_TIMES) {
            if (peerChannel.isHasPong()) {
              peerChannel.setFailTimes(0);
            } else {
              peerChannel.setFailTimes(peerChannel.getFailTimes() + 1);
            }
            peerChannel.setHasPong(false);
            peerChannel.getMessageQueue()
                .sendPingPongMessage(new PingMessage(System.currentTimeMillis()));
          } else {
            logger.info("Send to {} ping time out", peerChannel.getCtx().channel().remoteAddress());
            peerChannel.close();
          }
        }
      }
    }, 10, 10, TimeUnit.SECONDS);
  }

  @PreDestroy
  public void close() {
    if (pingTask != null) {
      pingTask.cancel(true);
    }
    pingTimer.shutdown();
  }
}
