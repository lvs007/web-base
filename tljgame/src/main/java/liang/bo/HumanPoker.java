package liang.bo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import liang.bo.PokersBo.Poker;
import liang.bo.PokersBo.PokerType;
import org.apache.commons.collections.CollectionUtils;

public class HumanPoker {

  private List<Poker> all = new ArrayList<>();
  private Map<PokerType, List<Poker>> allPokerMap = new HashMap<>();
  private List<List<Poker>> alreadyOutPokers = new ArrayList<>();

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

  public void clean() {
    all.clear();
    allPokerMap.clear();
    alreadyOutPokers.clear();
  }
}
