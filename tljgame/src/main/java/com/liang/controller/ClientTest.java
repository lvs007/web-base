package com.liang.controller;

import com.liang.tcp.client.TcpClient;
import com.liang.tcp.message.entity.AddGroupMessage;
import com.liang.udp.UdpMessageSender;
import com.liang.udp.message.UdpMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ClientTest {

  private String ip = "127.0.0.1";
  private int port = 10011;

  @Autowired
  private TcpClient tcpClient;

  @Autowired
  private UdpMessageSender udpMsgSendAndReceive;

  public Object testClient(@RequestParam(required = true, defaultValue = "123456") String groupId) {
    tcpClient.connectSync(ip, port).sendMessage(new AddGroupMessage(groupId));
//    ThreadUtils.sleep(100);
//    tcpClient.getPeerChannel("127.0.0.1", 10011).close();
    return "list";
  }

  public Object test1() {
    tcpClient.getPeerChannel(ip, port).sendMessage(new AddGroupMessage("123"));
    return "list";
  }

  public Object test(int count) {
    for (int i = 0; i < count; i++) {
      tcpClient.connectAsync(ip, port);
    }
    return "list";
  }

  public Object testUdp(
      @RequestParam(required = true, defaultValue = "nihao world") String content) {
    UdpMessage udpMessage = new UdpMessage();
    udpMessage.setContent(content);
    udpMessage.buildInetSocketAddress(ip, port);
    udpMsgSendAndReceive.sendMessage(udpMessage);
    return "list";
  }
}
