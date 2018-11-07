package com.liang.bo;

import com.liang.bo.PokersBo.Poker;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class RandomPoker {

  private List<Poker> allPoker = new ArrayList<>();

  public enum NumberType {
    TWO(2),
    THREE(3),
    FOUR(4),
    UNKNOWN(100);


    NumberType(int number) {
      this.number = number;
    }

    public static NumberType getNumberType(int number) {
      for (NumberType numberType : values()) {
        if (numberType.number == number) {
          return numberType;
        }
      }
      return UNKNOWN;
    }

    private int number;

    public int getNumber() {
      return number;
    }
  }

  public RandomPoker(int number) {
    if (number <= 1 || number > 4) {
      number = 4;
    }
    for (int i = 0; i < number; i++) {
      PokersBo pokersBo = new PokersBo();
      allPoker.addAll(pokersBo.getPokerList());
    }
    //打乱顺序
    Collections.shuffle(allPoker);
  }

  public List<Poker> getAllPoker() {
    return allPoker;
  }

  public Poker pop() {
    if (allPoker.size() > 0) {
      return allPoker.remove(0);
    }
    return null;
  }

}
