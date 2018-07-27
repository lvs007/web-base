package liang.bo;

import java.util.List;
import liang.bo.HumanPoker.OutPokerType;
import liang.bo.PokersBo.Poker;
import liang.bo.PokersBo.PokerType;
import liang.bo.Table.Site;
import liang.core.Action;
import liang.core.PlayPoker;

public class PeopleInfo implements Action {

  private long id;
  private String name;
  private volatile PeopleStatus status;
  private HumanPoker humanPoker;
  private boolean jiao;
  private Site site;
  private boolean isOutPoker;//是否已经出牌
  private Table table;
  private PlayPoker playPoker;
  private boolean isFirstOneOutPoker;//是否当前出牌者
  private boolean big;//当前出牌大小,默认是小的
  private int playNumber = 3;//当前自己打几,默认都是从3开始

  public enum PeopleStatus {
    INIT,//初始化
    CONFIRM,//准备
    DO_ING,//进行中
    LEAVE//掉线
  }

  public PeopleInfo() {
    status = PeopleStatus.INIT;
    humanPoker = new HumanPoker(this);
    playPoker = new PlayPoker(this, table);
  }

  @Override
  public void leaveTable() {
    if (table != null && table.leave(this)) {
      table = null;
      status = PeopleStatus.INIT;
      reset();
    }
  }

  /**
   * 清除当前的牌
   */
  public void reset() {
    humanPoker.clean();
  }

  /**
   * 准备
   */
  @Override
  public boolean confirm() {
    if (status == PeopleStatus.INIT) {
      status = PeopleStatus.CONFIRM;
    }
    table.start();
    return true;
  }

  /**
   * 撤销准备
   */
  @Override
  public boolean unConfirm() {
    if (status == PeopleStatus.CONFIRM) {
      status = PeopleStatus.INIT;
      table.peopleUnConfirm(this);
    }
    return true;
  }

  @Override
  public boolean gameOver() {
    if (getHumanPoker().isFinish()) {
      clean();
      return table.gameOver();
    }
    return false;
  }

  @Override
  public boolean call(PokerType pokerType, int number) {
    return playPoker.call(pokerType, number);
  }

  @Override
  public boolean play(List<Poker> pokerList) {
    return playPoker.play(pokerList);
  }

  public boolean validOutPoker(List<Poker> pokerList) {
    //解析出牌类型
    humanPoker.parseOutPoker(pokerList);
    if (isFirstOneOutPoker) {//是否是第一个出牌者
      if (humanPoker.getOutPokerType() == OutPokerType.HH && table
          .validFirstOutPokerHH(pokerList, this)) {//混合牌型（摔牌），必须验证最小的是否是四个里面最大的
        return true;
      }
      return humanPoker.validFirstOneOutPoker();
    } else {//不是第一个出牌，比较是否符合第一个出牌的牌型
      return table.validOutPoker(pokerList, this);
    }
  }

  private void clean() {
    status = PeopleStatus.INIT;
    jiao = false;
    reset();
  }

  public HumanPoker getHumanPoker() {
    return humanPoker;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public PeopleStatus getStatus() {
    return status;
  }

  public void setStatus(PeopleStatus status) {
    this.status = status;
  }

  public Table getTable() {
    return table;
  }

  public void setTable(Table table) {
    this.table = table;
  }

  public boolean isJiao() {
    return jiao;
  }

  public Site getSite() {
    return site;
  }

  public void setSite(Site site) {
    this.site = site;
  }

  public boolean isOutPoker() {
    return isOutPoker;
  }

  public void setOutPoker(boolean outPoker) {
    isOutPoker = outPoker;
  }

  public void setJiao(boolean jiao) {
    this.jiao = jiao;
  }

  public boolean isFirstOneOutPoker() {
    return isFirstOneOutPoker;
  }

  public void setFirstOneOutPoker(boolean firstOneOutPoker) {
    isFirstOneOutPoker = firstOneOutPoker;
  }

  public boolean isBig() {
    return big;
  }

  public void setBig(boolean big) {
    this.big = big;
  }

  public int getPlayNumber() {
    return playNumber;
  }

  public PeopleInfo setPlayNumber(int playNumber) {
    this.playNumber = playNumber;
    table.setPlayNumber(playNumber);
    return this;
  }
}
