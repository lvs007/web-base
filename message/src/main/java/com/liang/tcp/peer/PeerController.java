package com.liang.tcp.peer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Component;

public class PeerController {

  private static final int SAME_IP_MAX_COUNT = 10;

  private Map<String, AtomicInteger> ipCounts = new ConcurrentHashMap<>();

  private Object lock = new Object();

  public boolean canAdd(PeerChannel peerChannel) {
    synchronized (lock) {
      AtomicInteger atomicInteger = ipCounts.get(peerChannel.getHost());
      if (atomicInteger == null) {
        atomicInteger = new AtomicInteger(1);
      } else if (atomicInteger.get() >= SAME_IP_MAX_COUNT) {
        return false;
      } else {
        atomicInteger.incrementAndGet();
      }
      ipCounts.put(peerChannel.getHost(), atomicInteger);
    }
    return true;
  }

  public void leave(PeerChannel peerChannel) {
    synchronized (lock) {
      AtomicInteger atomicInteger = ipCounts.get(peerChannel.getHost());
      if (atomicInteger != null && atomicInteger.get() > 0) {
        atomicInteger.decrementAndGet();
      }
    }
  }

}
