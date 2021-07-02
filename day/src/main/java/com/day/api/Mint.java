package com.day.api;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;

@Controller
public class Mint {

  public String mint(ModelMap modelMap) {
    return "mint";
  }

}
