package com.nft.api;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;

@Controller
public class Detail {

  public String detail(ModelMap modelMap) {
    return "detail";
  }

}
