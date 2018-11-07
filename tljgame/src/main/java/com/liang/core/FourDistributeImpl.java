package com.liang.core;

import com.liang.bo.FourPokers;
import com.liang.bo.PeopleInfo;
import com.liang.bo.PokersBo.Poker;
import com.liang.bo.Table.Site;
import java.util.List;
import java.util.Map;

/**
 * 发牌
 */
public class FourDistributeImpl implements Distribute {

  public static final int dipaiNumber = 8;
  private static final int peopleNumber = 4;
  private Map<Site, PeopleInfo> peopleInfoMap;
  private List<Poker> dipai;

  public FourDistributeImpl(Map<Site, PeopleInfo> peopleInfoMap, List<Poker> dipai) {
    this.peopleInfoMap = peopleInfoMap;
    this.dipai = dipai;
  }

  @Override
  public void distribute() {
    FourPokers fourPokers = new FourPokers();
    while (true) {
      if (fourPokers.getAllPoker().size() <= 8) {
        dipai.addAll(fourPokers.getAllPoker());
        break;
      }
      for (PeopleInfo peopleInfo : peopleInfoMap.values()) {
        peopleInfo.getHumanPoker().add(fourPokers.pop());
      }
    }
  }
}
