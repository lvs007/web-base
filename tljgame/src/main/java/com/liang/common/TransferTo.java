package com.liang.common;

import com.liang.bo.PeopleInfo;
import liang.mvc.filter.UserInfo;

public class TransferTo {

  public static PeopleInfo transferTo(UserInfo userInfo) {
    PeopleInfo peopleInfo = new PeopleInfo(userInfo.getId(), userInfo.getNickName());
    return peopleInfo;
  }
}
