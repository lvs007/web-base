package liang.core;

import java.util.List;
import liang.bo.PeopleInfo;
import liang.bo.PokersBo.Poker;
import liang.bo.PokersBo.PokerType;
import liang.bo.Table;
import org.apache.commons.collections.CollectionUtils;

public class PlayPoker {

  private Table table;
  private PeopleInfo peopleInfo;

  public PlayPoker(PeopleInfo peopleInfo, Table table) {
    this.peopleInfo = peopleInfo;
    this.table = table;
  }

  /**
   * 叫主
   */
  public boolean call(PokerType pokerType, int number) {
    if (table == null || pokerType == null || number <= 0) {
      return false;
    }
    if (table.getZhu() == null) {
      table.setZhu(table.new Zhu(pokerType, number));
      peopleInfo.setJiao(true);
      table.setCurrentOutPikerPeople(peopleInfo);
      return true;
    }
    if (table.getZhu().getNumber() < number) {
      table.getZhu().setPokerType(pokerType).setNumber(number);
      peopleInfo.setJiao(true);
      table.setCurrentOutPikerPeople(peopleInfo);
      return true;
    }
    return false;
  }

  public boolean play(List<Poker> pokerList) {
    //校验出牌的牌型是否对
    peopleInfo.getHumanPoker().outPokers(pokerList);
    peopleInfo.setOutPoker(true);

    peopleInfo.gameOver();
    return true;
  }

  private boolean validOutPoker(List<Poker> pokerList, PeopleInfo peopleInfo, Table table) {
    if (CollectionUtils.isEmpty(pokerList)) {
      return false;
    }
    if (peopleInfo.getId() == table.getCurrentOutPikerPeople().getId()) {//第一个出牌的人
      return true;
    }
    if (pokerList.size() != table.getCurrentOutPikerPeople().getHumanPoker()
        .getCurrentOutPoker().size()) {
      return false;
    }

    return true;
  }

}
