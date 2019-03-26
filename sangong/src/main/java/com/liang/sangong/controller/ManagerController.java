package com.liang.sangong.controller;

import com.liang.common.exception.NotSupportException;
import com.liang.mvc.annotation.PcLogin;
import com.liang.mvc.commons.ResponseUtils;
import com.liang.mvc.commons.SpringContextHolder;
import com.liang.mvc.filter.LoginUtils;
import com.liang.mvc.filter.UserInfo;
import com.liang.sangong.common.SystemState;
import com.liang.sangong.trx.tron.TransferService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ManagerController {

  @Autowired
  private TransferService transferService;

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

  @PcLogin
  @ResponseBody
  public Object updateFullNode(String ip) {
    valid();
    SystemState.FULLNODE_IP = ip;
    return transferService.updateStub(ip) ? ResponseUtils.SuccessResponse()
        : ResponseUtils.ErrorResponse();
  }

  @PcLogin
  @ResponseBody
  public Object updateToAddress(String toAddress) {
    valid();
    SystemState.TO_ADDRESS = toAddress;
    return ResponseUtils.SuccessResponse();
  }

  @PcLogin
  @ResponseBody
  public Object updateTimeOut(long timeOut) {
    valid();
    SystemState.TX_TIME_OUT = timeOut;
    return ResponseUtils.SuccessResponse();
  }

  private void valid() {
    UserInfo userInfo = LoginUtils.getCurrentUser(SpringContextHolder.getRequest());
    if (!StringUtils.equals(userInfo.getUserName(), "master-go")) {
      throw NotSupportException.throwException("没有权限");
    }
  }

}
