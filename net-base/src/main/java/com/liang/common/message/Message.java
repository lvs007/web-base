package com.liang.common.message;

import com.alibaba.fastjson.JSON;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * type = 1（ping消息）,2（pong消息）,3（add group 加入群组消息）已经被使用，不能占用
 */
public abstract class Message {

  protected static final Logger logger = LoggerFactory.getLogger("Message");

  protected byte type;

  protected InetSocketAddress address;

  public Message(byte type) {
    this.type = type;
  }

  public byte[] sendData() {
    return ArrayUtils.add(data(), 0, type);
  }

  public byte[] data() {
    return JSON.toJSONString(this).getBytes(Charset.forName("UTF-8"));
  }

  public byte getType() {
    return type;
  }

  public static byte[] stringToBytes(String str) {
    return str.getBytes(Charset.forName("UTF-8"));
  }

  public static String bytesToString(byte[] bytes) {
    return new String(bytes, Charset.forName("UTF-8"));
  }

  public InetSocketAddress getAddress() {
    return address;
  }

  public Message setAddress(InetSocketAddress address) {
    this.address = address;
    return this;
  }

  public Message buildInetSocketAddress(String host, int port) {
    this.address = new InetSocketAddress(host, port);
    return this;
  }
}
