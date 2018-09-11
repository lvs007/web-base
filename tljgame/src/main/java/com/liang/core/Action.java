package com.liang.core;

import com.liang.bo.PokersBo.Poker;
import com.liang.bo.PokersBo.PokerType;
import java.util.List;

public interface Action {

  void leaveTable();

  boolean confirm();

  boolean unConfirm();

  boolean gameOver();

  boolean call(PokerType pokerType, int number);

  boolean play(List<Poker> pokerList);

}
