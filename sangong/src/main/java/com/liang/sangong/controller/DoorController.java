package com.liang.sangong.controller;

import com.liang.mvc.annotation.PcLogin;
import com.liang.mvc.commons.SpringContextHolder;
import com.liang.mvc.filter.LoginUtils;
import com.liang.mvc.filter.UserInfo;
import com.liang.sangong.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class DoorController {

  @Autowired
  private UserService userService;

  @PcLogin
  public String door() {
    UserInfo userInfo = LoginUtils.getCurrentUser(SpringContextHolder.getRequest());
    userService.setPeopleInfo(userInfo);
    return "dating";
  }

}
