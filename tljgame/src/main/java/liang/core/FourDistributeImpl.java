package liang.core;

import java.util.Map;
import liang.bo.FourPokers;
import liang.bo.PeopleInfo;
import liang.bo.PokersBo.Poker;

/**
 * 发牌
 */
public class FourDistributeImpl implements Distribute {

  private static final int peopleNumber = 4;
  private Map<Long, PeopleInfo> peopleInfoMap;

  public FourDistributeImpl(Map<Long, PeopleInfo> peopleInfoMap) {
    this.peopleInfoMap = peopleInfoMap;
  }

  @Override
  public void distribute() {
    FourPokers fourPokers = new FourPokers();
    for (PeopleInfo peopleInfo : peopleInfoMap.values()) {
      peopleInfo.getHumanPoker().add(fourPokers.pop());
    }
  }
}
