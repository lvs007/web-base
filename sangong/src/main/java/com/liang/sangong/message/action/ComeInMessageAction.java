package com.liang.sangong.message.action;

import com.liang.sangong.controller.GameWebSocket;
import com.liang.sangong.core.PeoplePlay;
import com.liang.sangong.core.RoomPool;
import com.liang.sangong.message.out.ComeInMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ComeInMessageAction {

  @Autowired
  private RoomPool roomPool;

  public void action(PeoplePlay peoplePlay) {
    for (PeoplePlay people : peoplePlay.getRoom().getPeoplePlayList()) {
      GameWebSocket gameWebSocket = GameWebSocket.webSocketMap
          .get(people.getPeopleInfo().getUserId());
      if (gameWebSocket == null || !gameWebSocket.getSession().isOpen()
          || people.getPeopleInfo().getUserId() == peoplePlay.getPeopleInfo().getUserId()) {
        continue;
      }
      gameWebSocket.sendMessage(new ComeInMessage().setPeoplePlay(peoplePlay).toString());
    }
  }

}
