package com.liang.sangong.message.action;

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

  public boolean recharge(RechargeMessage rechargeMessage, long userId) {
    return transferService.transferTrx(rechargeMessage.getPk(), rechargeMessage.getCoin(), userId);
//    boolean result = userService
//        .incrCoin(userInfo.getId(), PeopleType.TRX, rechargeMessage.getCoin());
  }

}
