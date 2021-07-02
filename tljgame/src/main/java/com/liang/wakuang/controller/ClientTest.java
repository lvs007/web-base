package com.liang.wakuang.controller;

import com.liang.common.MessageSendFactory;
import com.liang.common.MessageSendFactory.PROTO_TYPE;
import com.liang.common.message.SendMessageService;
import com.liang.tcp.client.TcpClient;
import com.liang.tcp.message.entity.AddGroupMessage;
import com.liang.tcp.message.entity.FileMessage;
import com.liang.udp.message.UdpMessage;
import java.io.IOException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ClientTest {

  private String host = "127.0.0.1";
  private int port = 10011;

  private TcpClient tcpClient;

  public Object testClient(@RequestParam(required = true, defaultValue = "123456") String groupId) {
    MessageSendFactory.getTcpClient().connectSync(host, port)
        .sendMessage(new AddGroupMessage(groupId));
//    ThreadUtils.sleep(100);
//    tcpClient.getPeerChannel("127.0.0.1", 10011).close();
    return "list";
  }

  public Object test1() {
    MessageSendFactory.getTcpClient().getPeerChannel(host, port)
        .sendMessage(new AddGroupMessage("123"));
    return "list";
  }

  public Object test(int count) {
    for (int i = 0; i < count; i++) {
      MessageSendFactory.getTcpClient().connectAsync(host, port);
    }
    return "list";
  }

  public Object testUdp(
      @RequestParam(required = true, defaultValue = "nihao world") String content) {
    UdpMessage udpMessage = new UdpMessage();
    udpMessage.setContent(content);
    udpMessage.buildInetSocketAddress(host, port);
    MessageSendFactory.getUdpMessageSender().sendMessage(udpMessage);
    return "list";
  }

  public Object testSender() {
    MessageSendFactory.create(PROTO_TYPE.TCP, host, port).sendMessage(new AddGroupMessage("123"));
    MessageSendFactory.create(PROTO_TYPE.UDP, host, port)
        .sendMessage(new UdpMessage().setContent("nihao world"));
    return "list";
  }

  public Object testImg() throws IOException {
    SendMessageService sendMessageService = MessageSendFactory.create(PROTO_TYPE.TCP, host, port);
    sendMessageService.sendMessage(new FileMessage("", ""));
    return "list";
  }
}
