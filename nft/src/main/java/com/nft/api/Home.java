package com.nft.api;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;

@Controller
public class Home {

  public String home(ModelMap modelMap) {
    return "home";
  }

}
