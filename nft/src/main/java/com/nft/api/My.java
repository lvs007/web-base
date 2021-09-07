package com.nft.api;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;

@Controller
public class My {

  public String my(ModelMap modelMap) {
    return "my";
  }

}
