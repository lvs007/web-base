package com.liang.common;

import com.liang.common.exception.TypeErrorException;
import com.liang.common.message.SendMessageService;
import com.liang.mvc.commons.SpringContextHolder;
import com.liang.tcp.TcpMessageSender;
import com.liang.tcp.client.TcpClient;
import com.liang.tcp.peer.PeerChannel;
import com.liang.udp.UdpMessageSender;

public class MessageSendFactory {

  public enum PROTO_TYPE {
    UDP, TCP
  }

  public static SendMessageService create(PROTO_TYPE protoType, String host, int port) {
    SendMessageService sendMessageService = null;
    switch (protoType) {
      case UDP:
        sendMessageService = SpringContextHolder.getBean(UdpMessageSender.class)
            .setAddress(host, port);
        break;
      case TCP: {
        TcpClient tcpClient = SpringContextHolder.getBean(TcpClient.class);
        PeerChannel peerChannel = tcpClient.connectSync(host, port);
        sendMessageService = new TcpMessageSender(peerChannel);
      }
      break;
      default:
        throw TypeErrorException.throwException("error type : " + protoType);
    }
    return sendMessageService;
  }

}
