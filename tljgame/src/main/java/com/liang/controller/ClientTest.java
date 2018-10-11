package com.liang.controller;

import com.liang.tcp.client.TcpClient;
import com.liang.tcp.message.AddGroupMessage;
import com.liang.tcp.peer.PeerChannelPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class ClientTest {

  @Autowired
  private TcpClient tcpClient;

  @Autowired
  private PeerChannelPool peerChannelPool;

  public Object testClient() {
    tcpClient.connectAsync("127.0.0.1", 10010);
    peerChannelPool.getPeerChannel("127.0.0.1",10010).sendMessage(new AddGroupMessage("123456"));
    return "list";
  }
}
