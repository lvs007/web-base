package com.liang.sangong.controller;

import com.alibaba.fastjson.JSON;
import com.liang.mvc.commons.SpringContextHolder;
import com.liang.mvc.filter.LoginUtils;
import com.liang.mvc.filter.UserInfo;
import com.liang.sangong.message.Message.MessageType;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import org.springframework.stereotype.Component;

@ServerEndpoint(value = "/gamesocket")
@Component
public class GameController {

  //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
  private static final AtomicInteger onlineCount = new AtomicInteger(0);

  //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
  private static final Map<Long, GameController> webSocketSet = new ConcurrentHashMap<>();

  //与某个客户端的连接会话，需要通过它来给客户端发送数据
  private Session session;

  /**
   * 连接建立成功调用的方法
   */
  @OnOpen
  public void onOpen(Session session) throws IOException {
    this.session = session;
    UserInfo userInfo = LoginUtils.getCurrentUser(SpringContextHolder.getRequest());
    if (userInfo == null) {
      session.close();
      return;
    }
    webSocketSet.put(userInfo.getId(), this);     //加入set中
    addOnlineCount();           //在线数加1
    System.out.println("有新连接加入！当前在线人数为" + getOnlineCount());
    try {
      sendMessage("");
    } catch (IOException e) {
      System.out.println("IO异常");
    }
  }

  /**
   * 连接关闭调用的方法
   */
  @OnClose
  public void onClose() {
    UserInfo userInfo = LoginUtils.getCurrentUser(SpringContextHolder.getRequest());
    if (userInfo == null) {
      return;
    }
    webSocketSet.remove(userInfo.getId());  //从set中删除
    subOnlineCount();           //在线数减1
    System.out.println("有一连接关闭！当前在线人数为" + getOnlineCount());
  }

  /**
   * 收到客户端消息后调用的方法
   *
   * @param message 客户端发送过来的消息
   */
  @OnMessage
  public void onMessage(String message, Session session) {
    System.out.println("来自客户端的消息:" + message);
    String type = JSON.parseObject(message).getString("messageType");
    MessageType messageType = MessageType.valueOf(type);
    //群发消息
    for (GameController item : webSocketSet.values()) {
      try {
        item.sendMessage(message);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * 发生错误时调用
   */
  @OnError
  public void onError(Session session, Throwable error) {
    System.out.println("发生错误");
    error.printStackTrace();
  }


  public void sendMessage(String message) throws IOException {
    this.session.getBasicRemote().sendText(message);
    //this.session.getAsyncRemote().sendText(message);
  }


  /**
   * /** 群发自定义消息
   */
  public static void sendInfo(String message) throws IOException {
    for (GameController item : webSocketSet.values()) {
      try {
        item.sendMessage(message);
      } catch (IOException e) {
        continue;
      }
    }
  }

  public static synchronized int getOnlineCount() {
    return onlineCount.get();
  }

  public static synchronized void addOnlineCount() {
    onlineCount.incrementAndGet();
  }

  public static synchronized void subOnlineCount() {
    onlineCount.decrementAndGet();
  }
}