package com.day.api;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;

@Controller
public class Create {

  public String createnft(ModelMap modelMap) {
    return "createnft";
  }

}
