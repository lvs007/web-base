package com.liang.sangong.controller;

import com.liang.common.util.Encodes;
import com.liang.common.util.PropertiesManager;
import com.liang.mvc.annotation.PcLogin;
import com.liang.mvc.commons.ResponseUtils;
import com.liang.mvc.commons.SpringContextHolder;
import com.liang.mvc.filter.LoginUtils;
import com.liang.mvc.filter.UserInfo;
import com.liang.sangong.bo.PeopleInfo;
import com.liang.sangong.bo.PeopleInfo.PeopleType;
import com.liang.sangong.core.PeoplePlay;
import com.liang.sangong.core.Room;
import com.liang.sangong.core.Room.RoomType;
import com.liang.sangong.core.RoomPool;
import com.liang.sangong.core.RoomService;
import com.liang.sangong.message.in.GetRoomMessage;
import com.liang.sangong.service.UserService;
import com.liang.sangong.trx.tron.TransferService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class DoorController {

  @Autowired
  private UserService userService;

  @Autowired
  private RoomService roomService;

  @Autowired
  private RoomPool roomPool;

  @Autowired
  private PropertiesManager propertiesManager;

  @Autowired
  private TransferService transferService;

  @PcLogin
  public String door(ModelMap modelMap) {
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
    Room result = roomService.createRoom(userInfo, peopleType, RoomType.PRIVATE);
    if (result != null) {
      return "redirect:/v1/door/get-room?roomId=" + result.getRoomId();
    }
    return "redirect:/v1/door/get-dating?type=" + type;
  }

  @PcLogin
  public String quickJoin(@RequestParam(name = "peopleType", required = false,
      defaultValue = "TRX") String type) {
    PeopleType peopleType = PeopleType.valueOf(type);
    if (peopleType == null) {
      return "redirect:/v1/door/get-dating?type=" + type;
    }
    UserInfo userInfo = LoginUtils.getCurrentUser(SpringContextHolder.getRequest());
    Room result = roomService.quickJoin(userInfo, peopleType);
    if (result != null) {
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
    PeopleInfo peopleInfo = userService.setPeopleInfo(userInfo, peopleType);
    modelMap.put("peopleInfo", peopleInfo);
    return "dating";
  }

  @PcLogin
  @ResponseBody
  public Object getRoomPeople() {
    UserInfo userInfo = LoginUtils.getCurrentUser(SpringContextHolder.getRequest());
    PeoplePlay peoplePlay = roomPool.getPeople(userInfo.getId());
    if (peoplePlay != null) {
      List<PeopleInfo> peopleInfoList = new ArrayList<>();
      for (PeoplePlay play : peoplePlay.getRoom().getPeoplePlayList()) {
        peopleInfoList.add(play.getPeopleInfo());
      }
      return peopleInfoList;
    }
    return ResponseUtils.ErrorResponse();
  }

  @PcLogin
  @ResponseBody
  public Object queryTrx(String pk) {
    return transferService.queryTrx(pk);
  }

  @PcLogin
  public String logOut() throws IOException {
    UserInfo userInfo = LoginUtils.getCurrentUser(SpringContextHolder.getRequest());
    boolean result = LoginUtils.logOut(userInfo.getToken());
    if (result) {
      String loginUrl = propertiesManager.getString("account.login.url",
          "http://127.0.0.1:9091/v1/pc-login/login-page");
      String datingUrl = propertiesManager.getString("dating.url",
          "http://127.0.0.1:9095/v1/door/get-dating");
      loginUrl += "?callBackUrl=" + Encodes.urlEncode(datingUrl);
//      SpringContextHolder.getResponse().sendRedirect(loginUrl);
      return "redirect:" + loginUrl;
    }
    return "redirect:/v1/door/get-dating";
  }

  public String error(String message, ModelMap modelMap) {
    modelMap.put("message", message);
    return "error";
  }
}
