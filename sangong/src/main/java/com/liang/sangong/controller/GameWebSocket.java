package com.liang.sangong.controller;

import com.alibaba.fastjson.JSON;
import com.liang.mvc.commons.SpringContextHolder;
import com.liang.mvc.filter.LoginUtils;
import com.liang.mvc.filter.UserInfo;
import com.liang.sangong.message.Message.MessageType;
import com.liang.sangong.message.action.MessageAction;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@ServerEndpoint(value = "/gamesocket")
@Component
public class GameWebSocket {

  //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
  private static final AtomicInteger onlineCount = new AtomicInteger(0);

  //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。key:userId
  public static final Map<Long, GameWebSocket> webSocketMap = new ConcurrentHashMap<>();

  //与某个客户端的连接会话，需要通过它来给客户端发送数据
  private Session session;

  private MessageAction messageAction;

  /**
   * 连接建立成功调用的方法
   */
  @OnOpen
  public void onOpen(Session session) throws IOException {
    this.session = session;
    String queryString = session.getQueryString();
    if (StringUtils.isEmpty(queryString) || !StringUtils.contains(queryString, "token=")) {
      return;
    }
    String token = StringUtils.split(queryString, "=")[1];
    UserInfo userInfo = LoginUtils.getUser(token);
    if (userInfo == null) {
      session.close();
      return;
    }
    GameWebSocket gameWebSocket = webSocketMap.get(userInfo.getId());
    if (gameWebSocket != null) {
      gameWebSocket.getSession().close();
    }
    webSocketMap.put(userInfo.getId(), this);     //加入set中
    addOnlineCount();           //在线数加1
    System.out.println("有新连接加入！当前在线人数为" + getOnlineCount());
  }

  /**
   * 连接关闭调用的方法
   */
  @OnClose
  public void onClose() {
    for (Iterator<Entry<Long, GameWebSocket>> iterator = webSocketMap.entrySet().iterator();
        iterator.hasNext(); ) {
      if (iterator.next().getValue().equals(this)) {
        iterator.remove();
        subOnlineCount();           //在线数减1
      }
    }

    System.out.println("有一连接关闭！当前在线人数为" + getOnlineCount());
  }

  /**
   * 收到客户端消息后调用的方法
   *
   * @param message 客户端发送过来的消息
   */
  @OnMessage
  public void onMessage(String message, Session session) throws IOException {
    System.out.println("来自客户端的消息:" + message);
    messageAction = SpringContextHolder.getBean(MessageAction.class);
    MessageType messageType = null;
    try {
      String type = JSON.parseObject(message).getString("messageType");
      messageType = MessageType.valueOf(type);
    } catch (Exception e) {
      session.close();
      return;
    }

    String value = messageAction.action(message, messageType, this);
    sendMessage(value);
  }

  /**
   * 发生错误时调用
   */
  @OnError
  public void onError(Session session, Throwable error) {
    System.out.println("发生错误");
    error.printStackTrace();
  }


  public void sendMessage(String message) {
    try {
      this.session.getBasicRemote().sendText(message);
    } catch (IOException e) {
      e.printStackTrace();
    }
    //this.session.getAsyncRemote().sendText(message);
  }


  /**
   * /** 群发自定义消息
   */
  public static void sendInfo(String message) {
    for (GameWebSocket item : webSocketMap.values()) {
      item.sendMessage(message);
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

  public Session getSession() {
    return session;
  }
}