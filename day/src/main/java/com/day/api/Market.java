package com.day.api;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;

@Controller
public class Market {

  public String market(ModelMap modelMap) {
    return "market";
  }

}
