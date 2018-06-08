package liang.bo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 一副牌
 */
public class PokersBo {

  private List<Poker> pokerList = new ArrayList<>(54);

  public enum PokerType {
    FANGP,//方片
    MEIH,//梅花
    HONGT,//红桃
    HEIT,//黑桃
    GUI//鬼（大小王）
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
      if (poker.getValue() > getValue()) {
        return 1;
      } else if (poker.getValue() < getValue()) {
        return -1;
      }
      return 0;
    }
  }

  public PokersBo() {
    init(PokerType.FANGP);
    init(PokerType.MEIH);
    init(PokerType.HONGT);
    init(PokerType.HEIT);
    pokerList.add(new Poker(PokerType.GUI, 0));//大王
    pokerList.add(new Poker(PokerType.GUI, 1));//小王
  }

  private void init(PokerType pokerType) {
    for (int j = 1; j <= 13; j++) {
      pokerList.add(new Poker(pokerType, j));
    }
  }

  public List<Poker> getPokerList() {
    return pokerList;
  }

  public void setPokerList(List<Poker> pokerList) {
    this.pokerList = pokerList;
  }
}
