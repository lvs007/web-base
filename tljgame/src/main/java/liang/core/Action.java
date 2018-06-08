package liang.core;

import java.util.List;
import liang.bo.PokersBo.Poker;
import liang.bo.PokersBo.PokerType;

public interface Action {

  void leaveTable();
  boolean confirm();
  boolean unConfirm();
  boolean gameOver();
  boolean call(PokerType pokerType, int number);
  boolean play(List<Poker> pokerList);

}
