package com.liang.tcp.peer;

import com.liang.tcp.ThreadPool;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.apache.commons.lang3.StringUtils;
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

  @Autowired
  private PeerController peerController;

  public void addPeerChannel(PeerChannel peerChannel) {
    synchronized (lock) {
      logger.info("addPeerChannel {},{}", peerChannel.getHost(), peerChannel.getPort());
      String key = buildKey(peerChannel.getHost(), peerChannel.getPort());
      if (peerChannelMap.size() < MAX_NUMBER && !peerChannelMap.containsKey(key)
          && peerController.canAdd(peerChannel)) {
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
    peerController.leave(peerChannel);
  }

  public void addGroup(String groupId, PeerChannel peerChannel) {
    synchronized (lock) {
      if (StringUtils.equals(groupId, peerChannel.getGroupId())) {
        return;
      }
      PeerChannelGroup peerChannelGroup = peerChannelGroupMap.get(peerChannel.getGroupId());
      if (peerChannelGroup != null) {
        peerChannelGroup.removePeerChannel(peerChannel);
        if (peerChannelGroup.size() <= 0) {
          peerChannelGroupMap.remove(peerChannel.getGroupId());
        }
      }
      if (!peerChannelGroupMap.containsKey(groupId)) {
        peerChannelGroupMap.put(groupId, new PeerChannelGroup());
      }
      peerChannel.setGroupId(groupId);
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

  @PreDestroy
  public void destroy() {
    close();
  }

  public void close() {
    if (peerChannelMap.size() <= 0) {
      return;
    }
    for (Iterator<PeerChannel> iterator = peerChannelMap.values().iterator();
        ((Iterator) iterator).hasNext(); ) {
      iterator.next().close();
    }
  }

}
