package com.liang.sangong.message.action;

import com.liang.mvc.filter.LoginUtils;
import com.liang.mvc.filter.UserInfo;
import com.liang.sangong.message.in.RechargeMessage;
import com.liang.sangong.service.UserService;
import com.liang.sangong.trx.tron.TransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransferAction {

  @Autowired
  private UserService userService;

  @Autowired
  private TransferService transferService;

  public boolean recharge(RechargeMessage rechargeMessage) {
    return transferService.transferTrx(rechargeMessage.getPk(), rechargeMessage.getCoin());
//    boolean result = userService
//        .incrCoin(userInfo.getId(), PeopleType.TRX, rechargeMessage.getCoin());
  }

}
