package com.liang.start;

import com.liang.tcp.TcpServer;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StartTcpService {

  @Autowired
  private TcpServer tcpServer;

  @PostConstruct
  public void init() {
    tcpServer.startAsync(10011);
  }

}
