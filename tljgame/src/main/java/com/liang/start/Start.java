package com.liang.start;

import com.liang.tcp.TcpServer;
import com.liang.udp.UdpServer;
import com.liang.udp.message.action.UdpMessageAction;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * Created by mc-050 on 2016/11/15.
 */
@Configuration//配置控制
@EnableAutoConfiguration
@ComponentScan//组件扫描
@ImportResource({"classpath:applicationContext-base.xml", "classpath:applicationContext.xml"})
public class Start {

  private TcpServer tcpServer;

  private UdpServer udpServer;

  @PostConstruct
  public void init() {
    tcpServer = new TcpServer();
    tcpServer.startAsync(10011);
    udpServer = new UdpServer();
    udpServer.register(new UdpMessageAction());
    udpServer.startAsync(10011);
  }

  @PreDestroy
  public void destory(){
    tcpServer.close();
  }

  public static void main(String[] args) {
    SpringApplication.run(Start.class, args);
  }
}
