package com.liang.sangong.message.action;

import com.alibaba.fastjson.JSON;
import com.liang.mvc.filter.LoginUtils;
import com.liang.mvc.filter.UserInfo;
import com.liang.sangong.bo.PeopleInfo;
import com.liang.sangong.bo.PeopleInfo.PeopleType;
import com.liang.sangong.controller.GameWebSocket;
import com.liang.sangong.core.PeoplePlay;
import com.liang.sangong.core.Room;
import com.liang.sangong.core.RoomPool;
import com.liang.sangong.core.RoomService;
import com.liang.sangong.message.AddRoomMessage;
import com.liang.sangong.message.ErrorMessage;
import com.liang.sangong.message.InviteMessage;
import com.liang.sangong.message.Message.MessageType;
import com.liang.sangong.message.SendInviteMessage;
import com.liang.sangong.message.in.BeginMessage;
import com.liang.sangong.message.in.ConfirmMessage;
import com.liang.sangong.message.in.GetRoomMessage;
import com.liang.sangong.message.in.LeaveRoomMessage;
import com.liang.sangong.message.in.RechargeMessage;
import com.liang.sangong.message.in.ZuoZhuangMessage;
import com.liang.sangong.message.out.ReturnRechargeMessage;
import com.liang.sangong.message.out.ReturnRoomMessage;
import com.liang.sangong.service.UserService;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageAction {

  @Autowired
  private UserService userService;

  @Autowired
  private RoomPool roomPool;

  @Autowired
  private RoomService roomService;

  public String action(String message, MessageType messageType, GameWebSocket socket) {
    if (messageType == null) {
      return "消息类型错误";
    }
    switch (messageType) {
      case sendInvite: {
        SendInviteMessage sendInviteMessage = JSON.parseObject(message, SendInviteMessage.class);
        PeopleInfo inviteUser = userService
            .findUser(sendInviteMessage.getName(), PeopleType.TRX.code);
        if (inviteUser == null) {
          return ErrorMessage.build("没有这个用户");
        }
        GameWebSocket gameWebSocket = GameWebSocket.webSocketMap.get(inviteUser.getUserId());
        if (gameWebSocket == null) {
          return ErrorMessage.build("用户没有加入游戏");
        }
        UserInfo userInfo = LoginUtils.getUser(sendInviteMessage.getToken());
        PeoplePlay peoplePlay = roomPool.getPeople(userInfo.getId());
        if (peoplePlay == null) {
          return ErrorMessage.build("请先创建房间");
        }
        if (gameWebSocket.getSession().isOpen()) {
          InviteMessage inviteMessage = new InviteMessage();
          inviteMessage.setRoomId(peoplePlay.getRoom().getRoomId());
          inviteMessage.setPeopleType(peoplePlay.getPeopleType());
          gameWebSocket.sendMessage(inviteMessage.toString());
          return ErrorMessage.build("邀请成功");
        } else {
          return ErrorMessage.build("请用户重新加入游戏");
        }
      }
      case add: {
        AddRoomMessage addRoomMessage = JSON.parseObject(message, AddRoomMessage.class);
        UserInfo userInfo = LoginUtils.getUser(addRoomMessage.getToken());
        if (userInfo == null) {
          return new ErrorMessage().setReason("没有登录").toString();
        }
        Room result = roomService
            .comeInRoom(addRoomMessage.getRoomId(), userInfo, addRoomMessage.getPeopleType());
        if (result != null && StringUtils.equals(result.getRoomId(), addRoomMessage.getRoomId())) {
          sendAllGetRoomMessage(result.getPeoplePlayList());
          return new ErrorMessage().setReason("加入成功").toString();
        } else {
          return new ErrorMessage().setReason("加入失败").toString();
        }
      }
      case getRoom: {
        GetRoomMessage getRoomMessage = JSON.parseObject(message, GetRoomMessage.class);
        Room room = roomPool.getRoom(getRoomMessage.getRoomId());
        socket.sendMessage(new ReturnRoomMessage().setRoom(room).toString());
      }
      break;
      case leave: {
        LeaveRoomMessage leaveRoomMessage = JSON.parseObject(message, LeaveRoomMessage.class);
        UserInfo userInfo = LoginUtils.getUser(leaveRoomMessage.getToken());
        boolean result = roomService.leaveRoom(leaveRoomMessage.getRoomId(), userInfo);
        if (result) {
          Room room = roomPool.getRoom(leaveRoomMessage.getRoomId());
          if (room != null) {
            for (PeoplePlay peoplePlay : room.getPeoplePlayList()) {
              if (peoplePlay.getPeopleInfo().getUserId() != userInfo.getId()) {
                GameWebSocket gameWebSocket = GameWebSocket.webSocketMap
                    .get(peoplePlay.getPeopleInfo().getUserId());
                if (gameWebSocket != null && gameWebSocket.getSession().isOpen()) {
                  gameWebSocket.sendMessage(
                      new GetRoomMessage().setRoomId(peoplePlay.getRoom().getRoomId()).toString());
                }
              }
            }
          }
          return ErrorMessage.build("leaveSuccess");
        }
        return ErrorMessage.build("当前游戏中不能离开");
      }
      case confirm: {
        ConfirmMessage confirmMessage = JSON.parseObject(message, ConfirmMessage.class);
        UserInfo userInfo = LoginUtils.getUser(confirmMessage.getToken());
        PeoplePlay peoplePlay = roomPool.getPeople(userInfo.getId());
        if (peoplePlay == null) {
          return ErrorMessage.build("请重新加入游戏");
        }
        boolean result = peoplePlay.confirm(confirmMessage.getCoin());
        if (result) {
          sendAllGetRoomMessage(peoplePlay.getRoom().getPeoplePlayList());
        } else {
          return ErrorMessage.build("准备失败");
        }
      }
      break;
      case zuozhuang: {
        ZuoZhuangMessage zuoZhuangMessage = JSON.parseObject(message, ZuoZhuangMessage.class);
        UserInfo userInfo = LoginUtils.getUser(zuoZhuangMessage.getToken());
        PeoplePlay peoplePlay = roomPool.getPeople(userInfo.getId());
        if (peoplePlay == null) {
          return ErrorMessage.build("请登录加入游戏");
        }
        boolean result = peoplePlay.zuoZhuang();
        if (result) {
          sendAllGetRoomMessage(peoplePlay.getRoom().getPeoplePlayList());
        } else {
          return ErrorMessage.build("不满足坐庄的条件");
        }
      }
      break;
      case recharge: {
        RechargeMessage rechargeMessage = JSON.parseObject(message, RechargeMessage.class);
        UserInfo userInfo = LoginUtils.getUser(rechargeMessage.getToken());
        boolean result = userService
            .incrCoin(userInfo.getId(), PeopleType.TRX, rechargeMessage.getCoin());
        if (result) {
          PeopleInfo peopleInfo = userService
              .findUser(userInfo.getId(), rechargeMessage.getPeopleType().code);
          ReturnRechargeMessage returnRechargeMessage = new ReturnRechargeMessage()
              .setCoin(peopleInfo.getCoin())
              .setName(peopleInfo.getName());
          socket.sendMessage(returnRechargeMessage.toString());
        } else {
          return ErrorMessage.build("充值失败！");
        }
      }
      break;
      case begin: {
        BeginMessage beginMessage = JSON.parseObject(message, BeginMessage.class);
        UserInfo userInfo = LoginUtils.getUser(beginMessage.getToken());
        if (userInfo == null) {
          return ErrorMessage.build("请登录");
        }
        PeoplePlay peoplePlay = roomPool.getPeople(userInfo.getId());
        if (peoplePlay == null) {
          return ErrorMessage.build("请加入游戏");
        }
        if (peoplePlay.begin()) {
          sendAllGetRoomMessage(peoplePlay.getRoom().getPeoplePlayList());
        } else {
          return ErrorMessage.build("当前不满足开始游戏条件");
        }
      }
      break;
      default:
    }
    return new ErrorMessage().setReason("success").toString();
  }

  private void sendAllGetRoomMessage(List<PeoplePlay> peoplePlayList) {
    for (PeoplePlay peoplePlay : peoplePlayList) {
      GameWebSocket gameWebSocket = GameWebSocket.webSocketMap
          .get(peoplePlay.getPeopleInfo().getUserId());
      if (gameWebSocket != null && gameWebSocket.getSession().isOpen()) {
        System.out.println("向" + peoplePlay.getPeopleInfo().getName() + "发送getRoom消息");
        gameWebSocket.sendMessage(
            new GetRoomMessage().setRoomId(peoplePlay.getRoom().getRoomId()).toString());
      }
    }
  }
}
