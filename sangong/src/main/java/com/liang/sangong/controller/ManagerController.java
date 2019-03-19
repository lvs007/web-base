package com.liang.sangong.controller;

import com.liang.common.exception.NotSupportException;
import com.liang.mvc.annotation.PcLogin;
import com.liang.mvc.commons.ResponseUtils;
import com.liang.mvc.commons.SpringContextHolder;
import com.liang.mvc.filter.LoginUtils;
import com.liang.mvc.filter.UserInfo;
import com.liang.sangong.common.SystemState;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ManagerController {

  @PcLogin
  @ResponseBody
  public Object systemMaintain(boolean maintain) {
    valid();
    SystemState.maintain = maintain;
    return ResponseUtils.SuccessResponse();
  }

  @PcLogin
  @ResponseBody
  public Object setUserLimit(int number) {
    valid();
    if (number <= 0 || number > 10000000) {
      return ResponseUtils.ErrorResponse();
    }
    SystemState.userLimit = number;
    return ResponseUtils.SuccessResponse();
  }

  private void valid() {
    UserInfo userInfo = LoginUtils.getCurrentUser(SpringContextHolder.getRequest());
    if (!StringUtils.equals(userInfo.getUserName(), "master-go")) {
      throw NotSupportException.throwException("没有权限");
    }
  }

}
