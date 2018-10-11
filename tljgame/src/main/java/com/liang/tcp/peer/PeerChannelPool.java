package com.liang.tcp.peer;

import com.liang.tcp.ThreadPool;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PeerChannelPool {

  private static final Logger logger = LoggerFactory.getLogger(PeerChannelPool.class);

  private static final int MAX_NUMBER = 100;

  private Map<String, PeerChannel> peerChannelMap = new HashMap<>();
  private Map<String, PeerChannelGroup> peerChannelGroupMap = new HashMap<>();

  private Object lock = new Object();

  @Autowired
  private ThreadPool threadPool;

  public void addPeerChannel(PeerChannel peerChannel) {
    synchronized (lock) {
      logger.info("addPeerChannel {},{}", peerChannel.getHost(), peerChannel.getPort());
      String key = buildKey(peerChannel.getHost(), peerChannel.getPort());
      if (peerChannelMap.size() < MAX_NUMBER && !peerChannelMap.containsKey(key)) {
        peerChannelMap.put(key, peerChannel);
      } else {
        //do disconnect
        peerChannel.close();
      }
    }
  }

  public void remove(PeerChannel peerChannel) {
    peerChannelMap.values().remove(peerChannel);
    PeerChannelGroup peerChannelGroup = peerChannelGroupMap.get(peerChannel.getGroupId());
    if (peerChannelGroup != null) {
      peerChannelGroup.removePeerChannel(peerChannel);
    }
  }

  public void addGroup(String groupId, PeerChannel peerChannel) {
    synchronized (lock) {
      if (!peerChannelGroupMap.containsKey(groupId)) {
        peerChannelGroupMap.put(groupId, new PeerChannelGroup());
      }
      peerChannelGroupMap.get(groupId).addPeerChannel(peerChannel);
    }
  }

  public PeerChannelGroup getPeerChannelGroup(PeerChannel peerChannel) {
    return peerChannelGroupMap.get(peerChannel.getGroupId());
  }

  public PeerChannel getPeerChannel(String host, int port) {
    return peerChannelMap.get(buildKey(host, port));
  }

  private String buildKey(String host, int port) {
    return host + ":" + port;
  }

  @PostConstruct
  public void printLog() {
    threadPool.executeLog(new Runnable() {
      @Override
      public void run() {
        logger.info("{},{}", peerChannelMap.values(), peerChannelGroupMap.keySet());
      }
    });
  }

}
