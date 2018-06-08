package liang.bo;

import java.util.List;
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

  public enum PeopleStatus {
    INIT,//初始化
    CONFIRM,//准备
    DO_ING,//进行中
    LEAVE//掉线
  }

  public PeopleInfo() {
    status = PeopleStatus.INIT;
    humanPoker = new HumanPoker();
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
}
