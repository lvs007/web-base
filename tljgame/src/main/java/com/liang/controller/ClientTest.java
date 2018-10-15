package com.liang.controller;

import com.liang.tcp.client.TcpClient;
import com.liang.tcp.message.entity.AddGroupMessage;
import io.netty.channel.ChannelFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ClientTest {

  @Autowired
  private TcpClient tcpClient;

  public Object testClient(@RequestParam(required = true, defaultValue = "123456") String groupId) {
    ChannelFuture channelFuture = tcpClient.connectAsync("127.0.0.1", 10010);
    tcpClient.getPeerChannel(channelFuture).sendMessage(new AddGroupMessage(groupId));
    return "list";
  }
}
