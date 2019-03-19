package com.liang.sangong.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class RoomPool {

  private final Map<String, Room> roomPool = new ConcurrentHashMap<>();

  private final Map<Long, PeoplePlay> peoplePlayMap = new ConcurrentHashMap<>();

  public void addRoom(Room room) {
    roomPool.put(room.getRoomId(), room);
  }

  public Room getRoom(String roomId) {
    return roomPool.get(roomId);
  }

  public void removeRoom(String roomId) {
    roomPool.remove(roomId);
  }

  public void addPeople(PeoplePlay peoplePlay) {
    peoplePlayMap.put(peoplePlay.getPeopleInfo().getUserId(), peoplePlay);
  }

  public PeoplePlay getPeople(long userId) {
    return peoplePlayMap.get(userId);
  }

  public void removePeople(PeoplePlay peoplePlay) {
    peoplePlayMap.remove(peoplePlay.getPeopleInfo().getUserId());
  }

  public int getUserNumber() {
    return peoplePlayMap.size();
  }
}
