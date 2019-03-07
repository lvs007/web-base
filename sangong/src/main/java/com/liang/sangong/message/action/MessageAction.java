package com.liang.sangong.message.action;

import com.alibaba.fastjson.JSON;
import com.liang.mvc.commons.SpringContextHolder;
import com.liang.mvc.filter.LoginUtils;
import com.liang.mvc.filter.UserInfo;
import com.liang.sangong.bo.PeopleInfo;
import com.liang.sangong.bo.PeopleInfo.PeopleType;
import com.liang.sangong.controller.GameWebSocket;
import com.liang.sangong.core.PeoplePlay;
import com.liang.sangong.core.RoomPool;
import com.liang.sangong.core.RoomService;
import com.liang.sangong.message.AddRoomMessage;
import com.liang.sangong.message.InviteMessage;
import com.liang.sangong.message.Message.MessageType;
import com.liang.sangong.message.SendInviteMessage;
import com.liang.sangong.service.UserService;
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

    public String action(String message, MessageType messageType) {
        if (messageType == null) {
            return "消息类型错误";
        }
        switch (messageType) {
            case sendInvite: {
                SendInviteMessage sendInviteMessage = JSON.parseObject(message, SendInviteMessage.class);
                PeopleInfo inviteUser = userService
                        .findUser(sendInviteMessage.getName(), PeopleType.TRX.code);
                if (inviteUser == null) {
                    return "没有这个用户";
                }
                GameWebSocket gameWebSocket = GameWebSocket.webSocketMap.get(inviteUser.getUserId());
                if (gameWebSocket == null) {
                    return "用户没有加入游戏";
                }
                UserInfo userInfo = LoginUtils.getCurrentUser(SpringContextHolder.getRequest());
                PeoplePlay peoplePlay = roomPool.getPeople(userInfo.getId());
                if (peoplePlay == null) {
                    return "请先创建房间";
                }
                if (gameWebSocket.getSession().isOpen()) {
                    InviteMessage inviteMessage = new InviteMessage();
                    inviteMessage.setRoomId(peoplePlay.getRoom().getRoomId());
                    inviteMessage.setPeopleType(peoplePlay.getPeopleType());
                    gameWebSocket.sendMessage(inviteMessage.toString());
                    return "邀请成功";
                } else {
                    return "请用户重新加入游戏";
                }
            }
            case add: {
                AddRoomMessage addRoomMessage = JSON.parseObject(message, AddRoomMessage.class);
                UserInfo userInfo = LoginUtils.getUser(addRoomMessage.getToken());
                if (userInfo == null) {
                    return "没有登录";
                }
                boolean result = roomService.comeInRoom(addRoomMessage.getRoomId(), userInfo, addRoomMessage.getPeopleType());
                return result ? "加入成功" : "加入失败";
            }
            default:
        }
        return "成功";
    }
}
