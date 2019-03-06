package com.liang.sangong.bo;

import java.util.ArrayList;
import java.util.List;

/**
 * 一副牌,不包含大小王
 */
public class PokesBo {

  private List<Poke> pokerList = new ArrayList<>(52);

  public enum PokeType {
    FANGP(1),//方片
    MEIH(2),//梅花
    HONGT(3),//红桃
    HEIT(4);//黑桃

    PokeType(int value) {
      this.value = value;
    }

    public int value;
  }

  public class Poke implements Comparable<Poke> {

    private PokeType pokerType;
    private int value;

    public Poke(PokeType pokerType, int value) {
      this.pokerType = pokerType;
      this.value = value;
    }

    public PokeType getPokerType() {
      return pokerType;
    }

    public void setPokerType(PokeType pokerType) {
      this.pokerType = pokerType;
    }

    public int getValue() {
      return value;
    }

    public void setValue(int value) {
      this.value = value;
    }

    @Override
    public int compareTo(Poke poke) {
      if (poke.getValue() > getValue()) {
        return 1;
      } else if (poke.getValue() < getValue()) {
        return -1;
      } else {
        if (poke.getPokerType() == getPokerType()) {
          return 0;
        } else if (poke.getPokerType().value > getPokerType().value) {
          return 1;
        } else {
          return -1;
        }
      }
    }

    @Override
    public String toString() {
      return "Poke{" +
          "pokerType=" + pokerType +
          ", value=" + value +
          '}';
    }
  }

  public PokesBo() {
    init(PokeType.FANGP);
    init(PokeType.MEIH);
    init(PokeType.HONGT);
    init(PokeType.HEIT);
  }

  private void init(PokeType pokerType) {
    for (int j = 1; j <= 13; j++) {
      pokerList.add(new Poke(pokerType, j));
    }
  }

  public List<Poke> getPokerList() {
    return pokerList;
  }
}
