package com.liang.wakuang.controller;

import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class TljApiController {

  @SendTo("/topic/add")
  public Object add(){
    return "";
  }
}
