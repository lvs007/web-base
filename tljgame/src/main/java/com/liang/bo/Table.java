package com.liang.bo;

import com.liang.bo.HumanPoker.OutPokerType;
import com.liang.bo.PeopleInfo.PeopleStatus;
import com.liang.bo.PokersBo.Poker;
import com.liang.bo.PokersBo.PokerType;
import com.liang.core.FourDistributeImpl;
import com.liang.core.RuleImpl;
import com.liang.core.TablePool;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Table {

  private static final int peopleNumber = 4;

  private String tableId;

  private volatile TableStatus status;

  private Map<Site, PeopleInfo> playPeople;

  private Zhu zhu;

  private PeopleInfo currentFirstOneOutPikerPeople;//一轮的首个当前出牌人
  private PeopleInfo oneOutPikerPeople;//一局的首个当前出牌人

  private int playNumber = 3;//默认打3

  private Object object = new Object();
  private Object startLock = new Object();

  private StatisticsData statisticsData;
  private FourDistributeImpl fourDistribute;

  private List<Poker> sendDiPai = new ArrayList<>();
  private List<Poker> peopleDiPai = new ArrayList<>();
  private boolean sendPokerFinish;
  private TablePool tablePool;

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

    public static Site nextSite(Site currentSite) {
      if (currentSite == D) {
        return N;
      }
      if (currentSite == N) {
        return X;
      }
      if (currentSite == X) {
        return B;
      }
      if (currentSite == B) {
        return D;
      }
      throw new RuntimeException("没有这样的Site：" + currentSite);
    }

    public int value;
  }

  public class Zhu {

    private PokerType pokerType;
    private int number;//一共几张2
    private int playNumber = 3;//打几

    public Zhu(PokerType pokerType, int number) {
      this.pokerType = pokerType;
      this.number = number;
    }

    /**
     * 验证牌是否是主,主包括大小王，2，打几，和叫的花色
     */
    public boolean validPokerIsZhu(Poker poker) {
      if (poker.getPokerType() == pokerType || poker.getValue() == playNumber
          || poker.getValue() == 2 || poker.getPokerType() == PokerType.GUI) {
        return true;
      }
      return false;
    }

    public boolean validPokersIsZhu(List<Poker> pokerList) {
      for (Poker poker : pokerList) {
        if (!validPokerIsZhu(poker)) {
          return false;
        }
      }
      return true;
    }

    public int haveZhuNumber(List<Poker> pokerList) {
      int count = 0;
      for (Poker poker : pokerList) {
        if (validPokerIsZhu(poker)) {
          ++count;
        }
      }
      return count;
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

    public void clean() {
      pokerType = null;
      number = 0;
    }
  }

  public Table(String tableId, TablePool tablePool) {
    playPeople = new HashMap<>(peopleNumber);
    status = TableStatus.INIT;
    this.tableId = tableId;
    this.tablePool = tablePool;
  }

  public boolean join(PeopleInfo peopleInfo, int site) {
    synchronized (object) {
      if (isCanJoin(peopleInfo, site)) {
        Site s = Site.getSite(site);
        playPeople.put(s, peopleInfo);
        peopleInfo.setTable(this);
        peopleInfo.setSite(s);
        if (playPeople.size() >= peopleNumber) {
          status = TableStatus.JOIN_FINISH;
        }
        return true;
      } else {
        return false;
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
        statisticsData = new StatisticsData();
        fourDistribute = new FourDistributeImpl(playPeople, sendDiPai);
        fourDistribute.distribute();
        sendPokerFinish = true;
        return true;
      }
      return false;
    }
  }

  public boolean gameOver() {
    if (!isFinish()) {
      return false;
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

  /**
   * 摔牌，必须大于所有人的牌，或者这个牌所有人都没有了
   */
  public boolean validFirstOutPokerHH(List<Poker> pokerList, PeopleInfo peopleInfo) {
    return RuleImpl.getInstance().validHHOutPoker(this, pokerList, peopleInfo);
  }

  public boolean validOutPoker(List<Poker> pokerList, PeopleInfo peopleInfo) {
    List<Poker> firstPeopleoutPokerList = currentFirstOneOutPikerPeople.getHumanPoker()
        .getCurrentOutPoker();
    if (firstPeopleoutPokerList.size() != playPeople
        .size()) {
      return false;
    }
    OutPokerType outPokerType = currentFirstOneOutPikerPeople.getHumanPoker().getOutPokerType();
    OutPokerType nowOutPokerType = peopleInfo.getHumanPoker().getOutPokerType();
    if (outPokerType == nowOutPokerType) {//出牌的类型一样，还得判断颜色，todo
      switch (outPokerType) {
        case ONE:
        case TWO:
        case THREE:
        case FOUR:
        case TLJ: {
          Poker firstPoker = firstPeopleoutPokerList.get(0);
          Poker nowPoker = pokerList.get(0);
          if (firstPoker.getPokerType() == nowPoker.getPokerType()) {//颜色相同
            return true;
          } else if (zhu.validPokerIsZhu(firstPoker) && zhu.validPokerIsZhu(nowPoker)) {//都是主
            return true;
          } else {//颜色不同，并且不都是主
            if (peopleInfo.getHumanPoker().getExitNumber(firstPoker) > 0) {
              return false;
            }
            return true;
          }
        }
        case HH: {
          return RuleImpl.getInstance().compareHHTwoPeopleOutPoker(firstPeopleoutPokerList,
              pokerList, peopleInfo, this);
        }
        case UNKNOWN:
          return false;
        default:
          return false;
      }
    }
    //出牌的类型不一样,说明他没有这个类型的牌，所以得判断他是否有这个类型的牌，如果没有在看出的是否是同一花色的，如果不是看他手上有没有
    return RuleImpl.getInstance()
        .validOutPokerTypeUnSame(outPokerType, firstPeopleoutPokerList, pokerList, peopleInfo);
  }

  public boolean statisticsData() {
    if (currentOutPokerIsFinish()) {
      PeopleInfo maxPeopleInfo = RuleImpl.getInstance()
          .compare(playPeople, currentFirstOneOutPikerPeople);
      //统计分数，重置出牌
      statisticsData.statisticsScore(currentFirstOneOutPikerPeople, playPeople);
      //谁的牌大，设置他为当前出牌人,
      setCurrentFirstOneOutPikerPeople(maxPeopleInfo);
      return true;
    }
    return false;
  }

  /**
   * 当前一圈出牌是否结束
   */
  private boolean currentOutPokerIsFinish() {
    for (PeopleInfo peopleInfo : playPeople.values()) {
      if (!peopleInfo.isOutPoker()) {
        return false;
      }
    }
    return true;
  }

  private void clean() {
    status = TableStatus.INIT;
    zhu.clean();
    statisticsData.clean();
    currentFirstOneOutPikerPeople = null;
    sendPokerFinish = false;
    sendDiPai.clear();
    peopleDiPai.clear();
  }

  private boolean isCanStart() {
    if (playPeople.size() != 4){
      return false;
    }
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

  public Map<Site, PeopleInfo> getPlayPeople() {
    return playPeople;
  }

  public PeopleInfo getCurrentFirstOneOutPikerPeople() {
    return currentFirstOneOutPikerPeople;
  }

  public void setCurrentFirstOneOutPikerPeople(PeopleInfo currentFirstOneOutPikerPeople) {
    this.currentFirstOneOutPikerPeople = currentFirstOneOutPikerPeople;
    currentFirstOneOutPikerPeople.setFirstOneOutPoker(true);
  }

  public int getPlayNumber() {
    return playNumber;
  }

  public Table setPlayNumber(int playNumber) {
    this.playNumber = playNumber;
    zhu.setPlayNumber(playNumber);
    return this;
  }

  public boolean isStart() {
    return status == TableStatus.DO_ING;
  }

  public boolean isFinish() {
    for (PeopleInfo peopleInfo : playPeople.values()) {
      if (!peopleInfo.getHumanPoker().isFinish()) {
        return false;
      }
    }
    return true;
  }

  public PeopleInfo getPeopleInfo(long userId) {
    for (Entry<Site, PeopleInfo> entry : playPeople.entrySet()) {
      if (entry.getValue().getId() == userId) {
        return entry.getValue();
      }
    }
    return null;
  }

  public String getTableId() {
    return tableId;
  }
}
