package com.liang.sangong.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class RoomPool {

  private final Map<String, Room> roomPool = new ConcurrentHashMap<>();

  public void add(Room room) {
    roomPool.put(room.getRoomId(), room);
  }

}
