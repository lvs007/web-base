package com.liang.udp.handler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.nio.NioDatagramChannel;

public class UdpChannelInitializer extends ChannelInitializer<NioDatagramChannel> {

  private UdpMessageHandler udpMessageHandler;

  private PacketDecoder packetDecoder;

  public UdpChannelInitializer(UdpMessageHandler udpMessageHandler, PacketDecoder packetDecoder) {
    this.packetDecoder = packetDecoder;
    this.udpMessageHandler = udpMessageHandler;
  }

  @Override
  protected void initChannel(NioDatagramChannel ch) {
    ch.pipeline().addLast(packetDecoder);
    udpMessageHandler.set(ch);
    ch.pipeline().addLast(udpMessageHandler);
  }
}
