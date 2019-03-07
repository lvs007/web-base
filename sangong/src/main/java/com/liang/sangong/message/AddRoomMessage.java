package com.liang.sangong.message;

import com.liang.sangong.bo.PeopleInfo;
import com.liang.sangong.message.Message;

/**
 * Created by kiven on 2019/3/8.
 */
public class AddRoomMessage extends Message {
    private String roomId;
    private PeopleInfo.PeopleType peopleType;
    private String token;

    public AddRoomMessage() {
        super(MessageType.add);
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public PeopleInfo.PeopleType getPeopleType() {
        return peopleType;
    }

    public void setPeopleType(PeopleInfo.PeopleType peopleType) {
        this.peopleType = peopleType;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
