package liang.bo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import liang.bo.PokersBo.Poker;
import liang.bo.PokersBo.PokerType;
import liang.bo.Table.Zhu;
import org.apache.commons.collections.CollectionUtils;

public class HumanPoker {

  private List<Poker> all = new ArrayList<>();
  private Map<PokerType, List<Poker>> allPokerMap = new HashMap<>();
  private List<List<Poker>> alreadyOutPokers = new ArrayList<>();

  public enum OutPokerType {
    ONE,//一张
    TWO,//一对
    THREE,//三张
    FOUR,//四张
    TLJ,//拖拉机
    HH,//混合牌型
    UNKNOWN//不符合的牌型
  }

  /**
   * 发牌
   */
  public void add(Poker poker) {
    if (poker == null) {
      return;
    }
    all.add(poker);
    if (allPokerMap.containsKey(poker.getPokerType())) {
      allPokerMap.get(poker.getPokerType()).add(poker);
    } else {
      List<Poker> pokerList = new ArrayList<>();
      pokerList.add(poker);
      allPokerMap.put(poker.getPokerType(), pokerList);
    }
    Collections.sort(allPokerMap.get(poker.getPokerType()));
  }

  /**
   * 出牌
   */
  public void outPokers(List<Poker> pokerList) {
    alreadyOutPokers.add(pokerList);
    all.removeAll(pokerList);
    for (Poker poker : pokerList) {
      allPokerMap.get(poker.getPokerType()).remove(poker);
    }
  }

  /**
   * 牌是否打完
   */
  public boolean isFinish() {
    return all.isEmpty();
  }

  /**
   * 获取当前出的牌
   */
  public List<Poker> getCurrentOutPoker() {
    if (CollectionUtils.isNotEmpty(alreadyOutPokers)) {
      return alreadyOutPokers.get(alreadyOutPokers.size() - 1);
    }
    return null;
  }

  public OutPokerType parseOutPoker(List<Poker> pokerList, Zhu zhu) {
    if (pokerList.size() == 1) {
      return OutPokerType.ONE;
    }
    if (pokerList.size() == 2 && validSamePoker(pokerList)) {
      return OutPokerType.TWO;
    }
    if (pokerList.size() == 3 && validSamePoker(pokerList)) {
      return OutPokerType.THREE;
    }
    if (pokerList.size() == 4 && validSamePoker(pokerList)) {
      return OutPokerType.FOUR;
    }
  }

  /**
   * 验证是否是同色
   */
  private boolean validSameColor(List<Poker> pokerList) {
    Poker tmp = null;
    for (Poker poker : pokerList) {
      if (tmp == null) {
        tmp = poker;
      } else if (tmp.getPokerType() == poker.getPokerType()) {
        continue;
      } else {
        return false;
      }
    }
    return true;
  }

  /**
   * 验证是否是同色同大小
   */
  private boolean validSamePoker(List<Poker> pokerList) {
    Poker tmp = null;
    for (Poker poker : pokerList) {
      if (tmp == null) {
        tmp = poker;
      } else if (tmp.getPokerType() == poker.getPokerType() && tmp.getValue() == poker.getValue()) {
        continue;
      } else {
        return false;
      }
    }
    return true;
  }

  public void clean() {
    all.clear();
    allPokerMap.clear();
    alreadyOutPokers.clear();
  }
}
