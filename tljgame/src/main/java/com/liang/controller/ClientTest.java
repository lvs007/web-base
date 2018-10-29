package com.liang.controller;

import com.liang.tcp.client.TcpClient;
import com.liang.tcp.message.entity.AddGroupMessage;
import com.liang.udp.UdpMessageSender;
import com.liang.udp.message.UdpMessage;
import liang.common.util.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ClientTest {

  @Autowired
  private TcpClient tcpClient;

  @Autowired
  private UdpMessageSender udpMsgSendAndReceive;

  public Object testClient(@RequestParam(required = true, defaultValue = "123456") String groupId) {
    tcpClient.connectSync("127.0.0.1", 10011).sendMessage(new AddGroupMessage(groupId));
    ThreadUtils.sleep(100);
    tcpClient.getPeerChannel("127.0.0.1", 10011).close();
    return "list";
  }

  public Object testUdp(
      @RequestParam(required = true, defaultValue = "nihao world") String content) {
    UdpMessage udpMessage = new UdpMessage();
    udpMessage.setContent(content);
    udpMessage.buildInetSocketAddress("127.0.0.1", 10011);
    udpMsgSendAndReceive.sendMessage(udpMessage);
    return "list";
  }
}
