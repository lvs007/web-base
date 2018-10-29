package liang.dezhou.bo;

import java.util.ArrayList;
import java.util.List;

/**
 * 一副牌,不包含大小王
 */
public class PokersBo {

  private List<Poker> pokerList = new ArrayList<>(52);

  public enum PokerType {
    FANGP(1),//方片
    MEIH(2),//梅花
    HONGT(3),//红桃
    HEIT(4);//黑桃

    PokerType(int value) {
      this.value = value;
    }

    public int value;
  }

  public class Poker implements Comparable<Poker> {

    private PokerType pokerType;
    private int value;

    public Poker(PokerType pokerType, int value) {
      this.pokerType = pokerType;
      this.value = value;
    }

    public PokerType getPokerType() {
      return pokerType;
    }

    public void setPokerType(PokerType pokerType) {
      this.pokerType = pokerType;
    }

    public int getValue() {
      return value;
    }

    public void setValue(int value) {
      this.value = value;
    }

    @Override
    public int compareTo(Poker poker) {
      if (poker.getPokerType().value > getPokerType().value) {
        return 1;
      } else if (poker.getPokerType().value < getPokerType().value) {
        return -1;
      } else {
        if (poker.getValue() > getValue()) {
          return 1;
        } else if (poker.getValue() < getValue()) {
          return -1;
        }
      }
      return 0;
    }
  }

  public PokersBo() {
    init(PokerType.FANGP);
    init(PokerType.MEIH);
    init(PokerType.HONGT);
    init(PokerType.HEIT);
  }

  private void init(PokerType pokerType) {
    for (int j = 1; j <= 13; j++) {
      pokerList.add(new Poker(pokerType, j));
    }
  }

  public List<Poker> getPokerList() {
    return pokerList;
  }
}
