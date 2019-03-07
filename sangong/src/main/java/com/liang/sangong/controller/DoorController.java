package com.liang.sangong.controller;

import com.liang.mvc.annotation.PcLogin;
import com.liang.mvc.commons.SpringContextHolder;
import com.liang.mvc.filter.LoginUtils;
import com.liang.mvc.filter.UserInfo;
import com.liang.sangong.bo.PeopleInfo.PeopleType;
import com.liang.sangong.core.RoomPool;
import com.liang.sangong.core.RoomService;
import com.liang.sangong.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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
  public String door() {
    UserInfo userInfo = LoginUtils.getCurrentUser(SpringContextHolder.getRequest());
    userService.setPeopleInfo(userInfo);
    return "dating";
  }

  @PcLogin
  public String createRoom(@RequestParam(name = "peopleType", required = false,
      defaultValue = "TRX") String type) {
    UserInfo userInfo = LoginUtils.getCurrentUser(SpringContextHolder.getRequest());
    roomService.createRoom(userInfo, PeopleType.valueOf(type));
    return "room";
  }

  @PcLogin
  public String comeInRoom(@RequestParam(name = "peopleType", required = false,
          defaultValue = "TRX") String type) {
    UserInfo userInfo = LoginUtils.getCurrentUser(SpringContextHolder.getRequest());
    roomService.createRoom(userInfo, PeopleType.valueOf(type));
    return "room";
  }
}
