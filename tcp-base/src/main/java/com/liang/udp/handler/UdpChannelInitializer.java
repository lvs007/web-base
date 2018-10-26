package com.liang.udp.handler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UdpChannelInitializer extends ChannelInitializer<NioDatagramChannel> {

  @Autowired
  private UdpMessageHandler udpMessageHandler;

  @Autowired
  private PacketDecoder packetDecoder;

  @Override
  protected void initChannel(NioDatagramChannel ch) {
    ch.pipeline().addLast(packetDecoder);
    udpMessageHandler.set(ch);
    ch.pipeline().addLast(udpMessageHandler);
  }
}
