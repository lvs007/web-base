package com.liang.bo;

import com.liang.bo.Table.Zhu;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;

/**
 * 一副牌
 */
public class PokersBo {

  private List<Poker> pokerList = new ArrayList<>(54);

  public enum PokerType {
    FANGP(1),//方片
    MEIH(2),//梅花
    HONGT(3),//红桃
    HEIT(4),//黑桃
    GUI(5);//鬼（大小王）

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

    public boolean eq(Poker poker) {
      if (poker.getPokerType() == pokerType && poker.getValue() == value) {
        return true;
      }
      return false;
    }
  }

  public PokersBo() {
    init(PokerType.FANGP);
    init(PokerType.MEIH);
    init(PokerType.HONGT);
    init(PokerType.HEIT);
    pokerList.add(new Poker(PokerType.GUI, 200));//大王
    pokerList.add(new Poker(PokerType.GUI, 100));//小王
  }

  private void init(PokerType pokerType) {
    for (int j = 1; j <= 13; j++) {
      pokerList.add(new Poker(pokerType, j));
    }
  }

  public static List<Poker> sortTlj(List<Poker> pokerList) {
    if (CollectionUtils.isEmpty(pokerList) || pokerList.size() < 2) {
      return pokerList;
    }
    Collections.sort(pokerList);
    Poker start = pokerList.get(0);
    if (start.getValue() == 1) {
      int remark = 0;
      for (int i = 1; i < pokerList.size(); i++) {
        if (pokerList.get(i).getValue() == 2) {
          break;
        }
        if (pokerList.get(i).getValue() == 1) {
          remark = i;
        }
      }
      for (int j = 0; j <= remark; j++) {
        pokerList.add(pokerList.remove(0));
      }
    }
    return pokerList;
  }

  public static List<Poker> sortClassify(List<Poker> pokerList) {
    if (CollectionUtils.isEmpty(pokerList) || pokerList.size() < 2) {
      return pokerList;
    }
    Collections.sort(pokerList);
    Poker start = pokerList.get(0);
    if (start.getValue() == 1) {
      int remark = 0;
      for (int i = 1; i < pokerList.size(); i++) {
        if (pokerList.get(i).getValue() == 1) {
          remark = i;
        } else {
          break;
        }
      }
      for (int j = 0; j <= remark; j++) {
        pokerList.add(pokerList.remove(0));
      }
    }
    return pokerList;
  }

  public static void sortFormMaxToMin(List<Poker> pokerList, Zhu zhu) {
    Collections.sort(pokerList, new Comparator<Poker>() {
      @Override
      public int compare(Poker o1, Poker o2) {
        return -compareTwo(o1, o2, zhu);
      }
    });
  }

  public static Poker findMinPoker(List<Poker> pokerList, Zhu zhu) {
    Poker min = pokerList.get(0);
    for (Poker value : pokerList) {
      min = compareTwoReturnMin(min, value, zhu);
    }
    return min;
  }

  public static Poker findMaxPoker(List<Poker> pokerList, Zhu zhu) {
    Poker max = pokerList.get(0);
    for (Poker poker : pokerList) {
      max = compareTwoReturnMax(max, poker, zhu);
    }
    return max;
  }

  /**
   * 相等返回0，source 大于 target返回1，小于返回-1
   */
  public static int compareTwo(Poker source, Poker target, Zhu zhu) {
    if (source.eq(target)) {
      return 0;
    }
    Poker poker = compareTwoReturnMin(source, target, zhu);
    if (poker == source) {
      return -1;
    } else {
      return 1;
    }
  }

  public static Poker compareTwoReturnMin(Poker source, Poker target, Zhu zhu) {
    if (source.eq(target)) {
      return target;
    }
    int playNumber = zhu.getPlayNumber();
    if (source.value == 2) {
      if ((target.value == 2 && target.pokerType == zhu.getPokerType())
          || target.value == playNumber || target.value == 100 || target.value == 200) {
        return source;
      } else {
        return target;
      }
    }
    if (source.value == playNumber) {
      if (target.value == 100 || target.value == 200) {
        return source;
      }
      if (target.value == playNumber && target.pokerType == zhu.getPokerType()
          && source.pokerType != zhu.getPokerType()) {
        return source;
      }
      return target;
    }
    if (source.value == 100 || source.value == 200) {
      return source.value >= target.value ? target : source;
    }
    if (target.value == 2 || target.value == playNumber || target.value == 100
        || target.value == 200) {
      return source;
    }
    //
    boolean s = zhu.validPokerIsZhu(source);
    boolean t = zhu.validPokerIsZhu(target);
    if (s && !t) {
      return target;
    } else if (!s && t) {
      return source;
    }
    //都不是明主
    if (source.value == 1) {
      return target;
    }
    if (target.value == 1) {
      return source;
    }
    return source.value >= target.value ? target : source;
  }

  public static Poker compareTwoReturnMax(Poker source, Poker target, Zhu zhu) {
    if (source.eq(target)) {
      return source;
    }
    int playNumber = zhu.getPlayNumber();
    if (source.value == 2) {
      if ((target.value == 2 && target.pokerType == zhu.getPokerType())
          || target.value == playNumber || target.value == 100 || target.value == 200) {
        return target;
      } else {
        return source;
      }
    }
    if (source.value == playNumber) {
      if (target.value == 100 || target.value == 200) {
        return target;
      }
      if (target.value == playNumber && target.pokerType == zhu.getPokerType()
          && source.pokerType != zhu.getPokerType()) {
        return target;
      }
      return source;
    }
    if (source.value == 100 || source.value == 200) {
      return source.value >= target.value ? source : target;
    }
    if (target.value == 2 || target.value == playNumber || target.value == 100
        || target.value == 200) {
      return target;
    }
    //
    boolean s = zhu.validPokerIsZhu(source);
    boolean t = zhu.validPokerIsZhu(target);
    if (s && !t) {
      return source;
    } else if (!s && t) {
      return target;
    }
    //都不是明主
    if (source.value == 1) {
      return source;
    }
    if (target.value == 1) {
      return target;
    }
    return source.value >= target.value ? source : target;
  }

  public static boolean eq(Poker source, Poker target, Zhu zhu) {
    if (source.getValue() != target.getValue()) {
      return false;
    }
    if (source.getValue() == 2 || source.getValue() == 3) {
      if ((source.getPokerType() == zhu.getPokerType() && target.getPokerType() == zhu
          .getPokerType()) || (source.getPokerType() != zhu.getPokerType()
          && target.getPokerType() != zhu.getPokerType())) {
        return true;
      } else {
        return false;
      }
    }
    if (source.getPokerType() == target.getPokerType()) {
      return true;
    } else {
      return false;
    }
  }

  public List<Poker> getPokerList() {
    return pokerList;
  }

  public void setPokerList(List<Poker> pokerList) {
    this.pokerList = pokerList;
  }

  public static boolean validIsAllSamePokerTypes(Poker poker, List<Poker> pokerList, Zhu zhu) {
    boolean z = zhu.validPokerIsZhu(poker);
    boolean result = true;
    for (Poker p : pokerList) {
      if (z) {
        if (!zhu.validPokerIsZhu(p)) {
          return false;
        }
      } else {
        if (poker.getPokerType() != p.getPokerType()) {
          return false;
        }
      }
    }
    return true;
  }

  public static int haveSamePokerNumber(Poker poker, List<Poker> pokerList, Zhu zhu) {
    return filterSamePoker(poker, pokerList, zhu).size();
  }

  public static List<Poker> filterSamePoker(Poker poker, List<Poker> pokerList, Zhu zhu) {
    boolean z = zhu.validPokerIsZhu(poker);
    List<Poker> result = new ArrayList<>();
    for (Poker p : pokerList) {
      if (z) {
        if (zhu.validPokerIsZhu(p)) {
          result.add(p);
        }
      } else {
        if (poker.getPokerType() == p.getPokerType()) {
          result.add(p);
        }
      }
    }
    return result;
  }
}
