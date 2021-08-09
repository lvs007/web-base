package com.day.api;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class Mint {

  public String mint(ModelMap modelMap) {
    return "mint";
  }

  public String mintDetail(@RequestParam(name = "type", required = true) String type,
      ModelMap modelMap) {

    modelMap.put("type", type);
    return "mintdetail";
  }

}
