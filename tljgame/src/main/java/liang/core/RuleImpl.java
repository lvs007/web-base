package liang.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import liang.bo.HumanPoker.OutPokerType;
import liang.bo.PeopleInfo;
import liang.bo.PokersBo;
import liang.bo.PokersBo.Poker;
import liang.bo.Table;
import liang.bo.Table.Zhu;
import org.apache.commons.collections.CollectionUtils;

public class RuleImpl {

  private static final RuleImpl ruleImpl = new RuleImpl();

  public static RuleImpl getInstance() {
    return ruleImpl;
  }

  /**
   * 必须都是同一种牌型
   */
  public class PokerEntity {

    private boolean isZhu;

    private List<Poker> one = new ArrayList<>();//一张(key:花色，value:大小)
    private List<Poker> two = new ArrayList<>();//一对
    private List<Poker> three = new ArrayList<>();//三张
    private List<Poker> four = new ArrayList<>();//四张

    public PokerEntity add(int number, Poker poker) {
      switch (number) {
        case 1:
          add(one, poker);
          break;
        case 2:
          add(two, poker);
          break;
        case 3:
          add(three, poker);
          break;
        case 4:
          add(four, poker);
          break;
      }
      return this;
    }

    private void add(List<Poker> pokerList, Poker poker) {
      if (!contains(pokerList, poker)) {
        pokerList.add(poker);
      }
    }

    private boolean contains(List<Poker> pokerList, Poker poker) {
      for (Poker p : pokerList) {
        if (p.eq(poker)) {
          return true;
        }
      }
      return false;
    }

    public boolean isZhu() {
      return isZhu;
    }

    public PokerEntity setZhu(boolean zhu) {
      isZhu = zhu;
      return this;
    }
  }

  /**
   * 解析混合牌型
   */
  public PokerEntity parseOutPoker(List<Poker> pokerList, Zhu zhu) {
    Collections.sort(pokerList);
    Poker tmp = null;
    int count = 1;
    PokerEntity pokerEntity = new PokerEntity();
    for (Poker poker : pokerList) {
      if (tmp == null) {
        tmp = poker;
      } else if (tmp.getPokerType() == poker.getPokerType() && tmp.getValue() == poker.getValue()) {
        ++count;
      } else {
        pokerEntity.add(count, tmp);
        tmp = poker;
        count = 1;
      }
    }
    pokerEntity.setZhu(zhu.validPokerIsZhu(tmp));
    pokerEntity.add(count, tmp);
    return pokerEntity;
  }

  public boolean validHHOutPoker(Table table, List<Poker> pokerList, PeopleInfo peopleInfo) {
    PokerEntity pokerEntity = parseOutPoker(pokerList, table.getZhu());
    for (PeopleInfo people : table.getPlayPeople().values()) {
      if (people.getId() != peopleInfo.getId()) {
        if (!comparePoker(table, pokerEntity.one, people, 1)) {
          return false;
        }
        if (!comparePoker(table, pokerEntity.two, people, 2)) {
          return false;
        }
        if (!comparePoker(table, pokerEntity.three, people, 3)) {
          return false;
        }
        if (!comparePoker(table, pokerEntity.four, people, 4)) {
          return false;
        }
      }
    }
    return true;
  }


  private boolean comparePoker(Table table, List<Poker> pokerList, PeopleInfo people, int number) {
    List<Poker> have = people.getHumanPoker().getPokersByPokerType(pokerList.get(0), number);
    if (CollectionUtils.isNotEmpty(have)) {
      Poker oneValue = PokersBo.findMinPoker(pokerList, table.getZhu());
      Poker haveValue = PokersBo.findMaxPoker(have, table.getZhu());
      if (!oneValue.eq(haveValue) && !PokersBo.eq(PokersBo
          .compareTwoReturnMax(oneValue, haveValue, table.getZhu()), oneValue, table.getZhu())) {
        return false;
      }
    }
    return true;
  }

  public boolean compareHHTwoPeopleOutPoker(List<Poker> firstOutPoker, List<Poker> anotherOutPoker,
      PeopleInfo peopleInfo, Table table) {
    Poker first = firstOutPoker.get(0);
    Zhu zhu = table.getZhu();
    int anotherNumber = peopleInfo.getHumanPoker().getPokersByPokerType(first).size();
    if (anotherNumber >= firstOutPoker.size()) {
      return PokersBo.validIsAllSamePokerTypes(first, anotherOutPoker, zhu);
    } else {
      return PokersBo.haveSamePokerNumber(first, anotherOutPoker, zhu) == anotherNumber;
    }
  }

  public boolean validOutPokerTypeUnSame(OutPokerType outPokerType,
      List<Poker> firstPeoplePokers, List<Poker> otherPeoplePokers, PeopleInfo otherPeople) {
    Poker firstOutPoker = firstPeoplePokers.get(0);
    Zhu zhu = otherPeople.getTable().getZhu();
    if (!PokersBo.validIsAllSamePokerTypes(firstOutPoker, otherPeoplePokers, zhu)
        && PokersBo.haveSamePokerNumber(firstOutPoker, otherPeoplePokers, zhu) != otherPeople
        .getHumanPoker().getPokersByPokerType(firstOutPoker).size()) {//如果出的牌型不符合标准
      //要么出得牌都跟第一个出的一样，要么自己手上有的牌跟出的牌一样（数量一样）
      return false;
    }
    switch (outPokerType) {
      case ONE:
      case TWO:
      case THREE:
      case FOUR: {
        //todo,如果没有四张，得出三张，没有三张，得出两对
        return otherPeople.getHumanPoker().validOutPokerNumber(firstOutPoker,
            otherPeoplePokers, outPokerType.value);
      }
      case TLJ: {
        return otherPeople.getHumanPoker().validOutPokerTlj(firstPeoplePokers, otherPeoplePokers);
      }
      case HH: {//todo
        return false;
      }
      case UNKNOWN:
        return false;
      default:
        return false;
    }
  }

}
