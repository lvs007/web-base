package com.liang.sangong.service;

import com.liang.sangong.controller.GameWebSocket;
import com.liang.sangong.core.RoomService;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CheckService {


  private final ScheduledExecutorService scheduledExecutor = Executors
      .newSingleThreadScheduledExecutor(r -> new Thread(r, "clean-disconnect-socket"));

  @Autowired
  private RoomService roomService;

  @PostConstruct
  public void init() {
    scheduledExecutor.scheduleWithFixedDelay(() -> {
      for (Entry<Long, GameWebSocket> entry : GameWebSocket.disconnectSocket.entrySet()) {
        if (System.currentTimeMillis() - entry.getValue().getAccessTime() > 30000) {
          GameWebSocket.disconnectSocket.remove(entry.getKey());
          roomService.leaveRoom(entry.getKey());
        }
      }
    }, 0, 30, TimeUnit.SECONDS);
  }
}
