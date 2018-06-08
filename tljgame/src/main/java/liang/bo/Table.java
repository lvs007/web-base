package liang.bo;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import liang.bo.PeopleInfo.PeopleStatus;
import liang.bo.PokersBo.PokerType;

public class Table {

  private static final int peopleNumber = 4;

  private String tableId;

  private volatile TableStatus status;

  private Map<Site, PeopleInfo> playPeople;

  private Zhu zhu;

  private PeopleInfo currentOutPikerPeople;//一轮的首个当前出牌人

  private Object object = new Object();
  private Object startLock = new Object();

  private enum TableStatus {
    INIT,//初始化
    JOIN_FINISH,//人数已满
    ALL_CONFIRM,//所有人都已经准备
    DO_ING,//进行中
//    GAME_OVER//牌局结束
  }

  /**
   * 座位，东南西北
   */
  public enum Site {
    D(1), N(2), X(3), B(4);

    Site(int value) {
      this.value = value;
    }

    public static Site getSite(int value) {
      for (Site site : values()) {
        if (site.value == value) {
          return site;
        }
      }
      return null;
    }


    public int value;
  }

  public class Zhu {

    private PokerType pokerType;
    private int number;
    private int playNumber;//打几

    public Zhu(PokerType pokerType, int number) {
      this.pokerType = pokerType;
      this.number = number;
    }

    public PokerType getPokerType() {
      return pokerType;
    }

    public Zhu setPokerType(PokerType pokerType) {
      this.pokerType = pokerType;
      return this;
    }

    public int getNumber() {
      return number;
    }

    public Zhu setNumber(int number) {
      this.number = number;
      return this;
    }

    public int getPlayNumber() {
      return playNumber;
    }

    public Zhu setPlayNumber(int playNumber) {
      this.playNumber = playNumber;
      return this;
    }
  }

  public Table() {
    playPeople = new HashMap<>(peopleNumber);
    status = TableStatus.INIT;
    tableId = UUID.randomUUID().toString();
  }

  public void join(PeopleInfo peopleInfo, int site) {
    synchronized (object) {
      if (isCanJoin(peopleInfo,site)) {
        playPeople.put(Site.getSite(site), peopleInfo);
        peopleInfo.setTable(this);
        if (playPeople.size() >= peopleNumber) {
          status = TableStatus.JOIN_FINISH;
        }
      }
    }
  }

  public boolean leave(PeopleInfo peopleInfo) {
    synchronized (object) {
      if (isCanLeave(peopleInfo)) {
        playPeople.remove(peopleInfo.getSite());
        status = TableStatus.INIT;
        return true;
      }
    }
    return false;
  }

  public boolean start() {
    synchronized (startLock) {
      if (isCanStart()) {
        status = TableStatus.DO_ING;
        for (PeopleInfo people : playPeople.values()) {
          people.setStatus(PeopleStatus.DO_ING);
        }
        return true;
      }
      return false;
    }
  }

  public boolean gameOver() {
    for (PeopleInfo peopleInfo : playPeople.values()) {
      if (!peopleInfo.getHumanPoker().isFinish()) {
        return false;
      }
    }
    clean();
    return true;
  }

  public void peopleUnConfirm(PeopleInfo peopleInfo) {
    synchronized (startLock) {
      if (status == TableStatus.ALL_CONFIRM) {
        status = TableStatus.JOIN_FINISH;
      }
    }
  }

  private void clean() {
    status = TableStatus.INIT;
    zhu = null;
  }

  private boolean isCanStart() {
    for (PeopleInfo people : playPeople.values()) {
      if (people.getStatus() != PeopleStatus.CONFIRM) {
        return false;
      }
    }
    status = TableStatus.ALL_CONFIRM;
    return true;
  }

  private boolean isCanJoin(PeopleInfo peopleInfo, int size) {
    return status == TableStatus.INIT && peopleInfo.getStatus() == PeopleStatus.INIT
        && playPeople.get(Site.getSite(size)) == null;
  }

  private boolean isCanLeave(PeopleInfo peopleInfo) {
    return (status == TableStatus.INIT || status == TableStatus.JOIN_FINISH)
        && peopleInfo.getStatus() == PeopleStatus.INIT;
  }

  public Zhu getZhu() {
    return zhu;
  }

  public void setZhu(Zhu zhu) {
    this.zhu = zhu;
  }

  public PeopleInfo getCurrentOutPikerPeople() {
    return currentOutPikerPeople;
  }

  public void setCurrentOutPikerPeople(PeopleInfo currentOutPikerPeople) {
    this.currentOutPikerPeople = currentOutPikerPeople;
  }
}
