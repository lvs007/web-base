package com.liang.wakuang.controller;

import com.liang.mvc.commons.SpringContextHolder;
import com.liang.mvc.filter.LoginUtils;
import com.liang.mvc.filter.UserInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;

@Controller
public class DoorController {

  public String door(ModelMap modelMap) {
    return "redirect:/v1/door/home";
  }


  public String home(ModelMap modelMap) {
    UserInfo userInfo = LoginUtils.getCurrentUser(SpringContextHolder.getRequest());
    if (userInfo != null) {
      modelMap.put("dataStatistics", "");
    }
    return "home";
  }

  public String error(String message, ModelMap modelMap) {
    modelMap.put("message", message);
    return "error";
  }
}
