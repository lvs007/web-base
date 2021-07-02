package com.day.api;

import com.liang.mvc.commons.SpringContextHolder;
import com.liang.mvc.filter.LoginUtils;
import com.liang.mvc.filter.UserInfo;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;

@Controller
public class Home {

  public String home(ModelMap modelMap) {
    return "home";
  }

}
