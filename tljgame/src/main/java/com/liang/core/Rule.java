package com.liang.core;

import com.liang.bo.HumanPoker.OutPokerType;
import com.liang.bo.PeopleInfo;
import com.liang.bo.PokersBo;
import com.liang.bo.PokersBo.Poker;
import com.liang.bo.Table.Site;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class Rule {

  public PeopleInfo compare(Map<Site, PeopleInfo> playPeople, PeopleInfo currentFirstOutPeople) {
    PeopleInfo nowPeople = currentFirstOutPeople;
    Map<Site, PeopleInfo> copy = new HashMap<>(playPeople);
    Site nowSite = nowPeople.getSite();
    Site nextSite = nowSite;
    copy.remove(nowSite);
    while (copy.size() > 0) {
      nextSite = Site.nextSite(nextSite);
      PeopleInfo peopleInfo2 = copy.get(nextSite);
      copy.remove(nextSite);
      nowPeople = compareTwoReturnMax(nowPeople, peopleInfo2);
    }
    return nowPeople;
  }

  private PeopleInfo compareTwoReturnMax(PeopleInfo peopleInfo1, PeopleInfo peopleInfo2) {
    List<Poker> pokerList1 = new ArrayList<>(peopleInfo1.getHumanPoker().getCurrentOutPoker());
    List<Poker> pokerList2 = new ArrayList<>(peopleInfo2.getHumanPoker().getCurrentOutPoker());
    OutPokerType outPokerType = peopleInfo1.getHumanPoker().getOutPokerType();
    OutPokerType nowOutPokerType = peopleInfo2.getHumanPoker().getOutPokerType();
    if (outPokerType != nowOutPokerType) {
      if (outPokerType == OutPokerType.HH && peopleInfo1.getTable().getZhu()
          .validPokersIsZhu(pokerList2)) {
        return peopleInfo2;
      }
      return peopleInfo1;
    } else {
      if (peopleInfo1.getTable().getZhu().validPokersIsZhu(pokerList1) && !peopleInfo2
          .getTable().getZhu().validPokersIsZhu(pokerList2)) {
        return peopleInfo1;
      } else if (!peopleInfo1.getTable().getZhu().validPokersIsZhu(pokerList1) && peopleInfo2
          .getTable().getZhu().validPokersIsZhu(pokerList2)) {
        return peopleInfo2;
      }
      switch (outPokerType) {
        case ONE:
        case TWO:
        case THREE:
        case FOUR: {
          if (PokersBo.compareTwo(pokerList1.get(0), pokerList2.get(0),
              peopleInfo1.getTable().getZhu()) >= 0) {
            return peopleInfo1;
          } else {
            return peopleInfo2;
          }
        }
        case TLJ:
        case HH: {
          while (pokerList1.size() > 0) {
            Poker max1 = PokersBo.findMaxPoker(pokerList1, peopleInfo1.getTable().getZhu());
            Poker max2 = PokersBo.findMaxPoker(pokerList2, peopleInfo2.getTable().getZhu());
            int value = PokersBo.compareTwo(max1, max2, peopleInfo1.getTable().getZhu());
            if (value == 1) {
              return peopleInfo1;
            } else if (value == -1) {
              return peopleInfo2;
            } else {
              remove(pokerList1, max1);
              remove(pokerList2, max2);
            }
          }
          return peopleInfo1;
        }
        case UNKNOWN:
          return peopleInfo1;
        default:
          return peopleInfo1;
      }
    }
  }

  private void remove(List<Poker> pokerList, Poker poker) {
    for (Iterator<Poker> p = pokerList.iterator(); p.hasNext(); ) {
      if (p.next().getValue() == poker.getValue()) {
        p.remove();
      }
    }
  }

}
