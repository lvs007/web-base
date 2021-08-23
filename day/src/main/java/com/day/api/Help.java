package com.day.api;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;

@Controller
public class Help {

  public String help(ModelMap modelMap) {
    return "help";
  }

}
