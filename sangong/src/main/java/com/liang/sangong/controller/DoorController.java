package com.liang.sangong.controller;

import com.liang.mvc.annotation.PcLogin;
import com.liang.mvc.commons.SpringContextHolder;
import com.liang.mvc.filter.LoginUtils;
import com.liang.mvc.filter.UserInfo;
import com.liang.sangong.bo.PeopleInfo;
import com.liang.sangong.bo.PeopleInfo.PeopleType;
import com.liang.sangong.core.PeoplePlay;
import com.liang.sangong.core.Room;
import com.liang.sangong.core.RoomPool;
import com.liang.sangong.core.RoomService;
import com.liang.sangong.message.in.GetRoomMessage;
import com.liang.sangong.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class DoorController {

  @Autowired
  private UserService userService;

  @Autowired
  private RoomService roomService;

  @Autowired
  private RoomPool roomPool;

  @PcLogin
  public String door(ModelMap modelMap) {
    UserInfo userInfo = LoginUtils.getCurrentUser(SpringContextHolder.getRequest());
    PeopleInfo peopleInfo = userService.setPeopleInfo(userInfo);
    modelMap.put("peopleInfo", peopleInfo);
    return "redirect:/v1/door/get-dating";
  }

  @PcLogin
  public String createRoom(@RequestParam(name = "peopleType", required = false,
      defaultValue = "TRX") String type, ModelMap modelMap) {
    PeopleType peopleType = PeopleType.valueOf(type);
    if (peopleType == null) {
      return "redirect:/v1/door/get-dating?type=" + type;
    }
    UserInfo userInfo = LoginUtils.getCurrentUser(SpringContextHolder.getRequest());
    Room result = roomService.createRoom(userInfo, peopleType);
    if (result != null) {
//      modelMap.put("room", result);
//      modelMap.put("userInfo", userInfo);
//      GameWebSocket gameWebSocket = GameWebSocket.webSocketMap.get(userInfo.getId());
//      if (gameWebSocket != null) {
//        gameWebSocket.sendMessage(new GetRoomMessage().setRoomId(result.getRoomId()).toString());
//      }
//      return "room";
      return "redirect:/v1/door/get-room?roomId=" + result.getRoomId();
    }
    return "redirect:/v1/door/get-dating?type=" + type;
  }

  @PcLogin
  public String comeInRoom(@RequestParam(name = "peopleType", required = false,
      defaultValue = "TRX") String type, @RequestParam(name = "roomId", required = true)
      String roomId, ModelMap modelMap) {
    UserInfo userInfo = LoginUtils.getCurrentUser(SpringContextHolder.getRequest());
    Room result = roomService.comeInRoom(roomId, userInfo, PeopleType.valueOf(type));
    if (result != null) {
//      modelMap.put("room", result);
//      modelMap.put("userInfo", userInfo);
//      GameWebSocket gameWebSocket = GameWebSocket.webSocketMap.get(userInfo.getId());
//      if (gameWebSocket != null) {
//        gameWebSocket.sendMessage(new GetRoomMessage().setRoomId(roomId).toString());
//      }
//      return "room";
      return "redirect:/v1/door/get-room?roomId=" + result.getRoomId();
    }
    return "redirect:/v1/door/get-dating?type=" + type;
  }

  @PcLogin
  public String getRoom(@RequestParam(name = "roomId", required = true) String roomId,
      ModelMap modelMap) {
    UserInfo userInfo = LoginUtils.getCurrentUser(SpringContextHolder.getRequest());
    Room room = roomPool.getRoom(roomId);
    PeoplePlay peoplePlay = roomPool.getPeople(userInfo.getId());
    if (room != null && peoplePlay != null && StringUtils
        .equals(roomId, peoplePlay.getRoom().getRoomId())) {
      modelMap.put("room", room);
      modelMap.put("userInfo", userInfo);
      GameWebSocket gameWebSocket = GameWebSocket.webSocketMap.get(userInfo.getId());
      if (gameWebSocket != null) {
        gameWebSocket.sendMessage(new GetRoomMessage().setRoomId(roomId).toString());
      }
      return "room";
    } else {
      return "redirect:/v1/door/get-dating";
    }
  }

  @PcLogin
  public String getDating(@RequestParam(name = "peopleType", required = false,
      defaultValue = "TRX") String type, ModelMap modelMap) {
    UserInfo userInfo = LoginUtils.getCurrentUser(SpringContextHolder.getRequest());
    PeopleType peopleType = PeopleType.valueOf(type);
    peopleType = peopleType == null ? PeopleType.TRX : peopleType;
    PeopleInfo peopleInfo = userService.findUser(userInfo.getId(), peopleType.code);
    modelMap.put("peopleInfo", peopleInfo);
    return "dating";
  }
}
