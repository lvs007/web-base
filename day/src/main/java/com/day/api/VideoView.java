package com.day.api;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;

@Controller
public class VideoView {

  public String view(ModelMap modelMap) {
    return "videoview";
  }

}
