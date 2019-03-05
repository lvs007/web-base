package com.liang.common;

import com.liang.common.message.SendMessageService;
import com.liang.tcp.TcpMessageSender;
import com.liang.tcp.client.TcpClient;
import com.liang.tcp.peer.PeerChannel;
import com.liang.udp.UdpMessageSender;
import liang.common.exception.TypeErrorException;

public class MessageSendFactory {

  private static UdpMessageSender udpMessageSender;
  private static TcpClient tcpClient;

  public enum PROTO_TYPE {
    UDP, TCP
  }

  public static SendMessageService create(PROTO_TYPE protoType, String host, int port) {
    SendMessageService sendMessageService = null;
    switch (protoType) {
      case UDP:
        sendMessageService = udpMessageSender.setAddress(host, port);
        break;
      case TCP: {
        PeerChannel peerChannel = tcpClient.connectSync(host, port);
        sendMessageService = new TcpMessageSender(peerChannel);
      }
      break;
      default:
        throw TypeErrorException.throwException("error type : " + protoType);
    }
    return sendMessageService;
  }

  public static void setUdpMessageSender(UdpMessageSender udpMessageSender) {
    MessageSendFactory.udpMessageSender = udpMessageSender;
  }

  public static UdpMessageSender getUdpMessageSender() {
    return udpMessageSender;
  }

  public static void setTcpClient(TcpClient tcpClient) {
    MessageSendFactory.tcpClient = tcpClient;
  }

  public static TcpClient getTcpClient() {
    return tcpClient;
  }
}
