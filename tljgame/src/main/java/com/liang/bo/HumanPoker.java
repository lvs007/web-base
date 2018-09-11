package com.liang.bo;

import com.liang.bo.PokersBo.Poker;
import com.liang.bo.PokersBo.PokerType;
import com.liang.bo.Table.Zhu;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.collections.CollectionUtils;

public class HumanPoker {

  private List<Poker> all = new ArrayList<>();
  private Map<PokerType, List<Poker>> allPokerMap = new HashMap<>();
  private List<List<Poker>> alreadyOutPokers = new ArrayList<>();
  private OutPokerType outPokerType;//出牌的类型
  private PokerClassify pokerClassify = new PokerClassify();//牌型的分类，分为主，四个花色排序
  private List<Poker> dipai = new ArrayList<>();

  private PeopleInfo peopleInfo;

  public HumanPoker(PeopleInfo peopleInfo) {
    this.peopleInfo = peopleInfo;
  }

  public OutPokerType getOutPokerType() {
    return outPokerType;
  }

  public List<Poker> getPokersByPokerType(Poker p, int number) {
    List<Poker> pokerList = getPokersByPokerType(p);
    return getPokers(pokerList, number);
  }

  /**
   * 找出相同牌数量是number的所有牌,已经排序过
   */
  public List<Poker> getPokers(List<Poker> pokerList, int number) {
    switch (number) {
      case 1:
        return pokerList;
      case 2:
      case 3:
      case 4: {
        List<Poker> pokers = new ArrayList<>();
        if (pokerList.size() < number) {
          return pokers;
        }
        Collections.sort(pokerList);
        Poker tmp = null;
        int count = 1;
        for (Poker poker : pokerList) {
          if (tmp == null) {
            tmp = poker;
          } else if (tmp.getPokerType() == poker.getPokerType()
              && tmp.getValue() == poker.getValue()) {
            ++count;
          } else {
            if (count == number) {
              pokers.add(tmp);
            }
            tmp = poker;
            count = 1;
          }
        }
        if (count == number) {
          pokers.add(tmp);
        }
        return pokers;
      }
    }
    return pokerList;
  }

  public List<Poker> getPokersByPokerType(Poker poker) {
    Zhu zhu = peopleInfo.getTable().getZhu();
    PokerType pokerType = zhu.validPokerIsZhu(poker) ? zhu.getPokerType() : poker.getPokerType();
    List<Poker> pokerList = new ArrayList<>();
    switch (pokerType) {
      case FANGP:
        pokerList.addAll(pokerClassify.getFangPian());
        break;
      case MEIH:
        pokerList.addAll(pokerClassify.getMeiHuan());
        break;
      case HONGT:
        pokerList.addAll(pokerClassify.getHongTao());
        break;
      case HEIT:
        pokerList.addAll(pokerClassify.getHeiTao());
        break;
    }
    if (pokerType == zhu.getPokerType()) {
      pokerList.addAll(pokerClassify.getZhu());
    }
    return pokerList;
  }

  public class PokerClassify {

    private List<Poker> zhu = new ArrayList<>();
    private List<Poker> fangPian = new ArrayList<>();
    private List<Poker> meiHuan = new ArrayList<>();
    private List<Poker> hongTao = new ArrayList<>();
    private List<Poker> heiTao = new ArrayList<>();

    public List<Poker> getZhu() {
      return zhu;
    }

    public PokerClassify setZhu(List<Poker> zhu) {
      this.zhu = zhu;
      return this;
    }

    public List<Poker> getFangPian() {
      return fangPian;
    }

    public PokerClassify setFangPian(List<Poker> fangPian) {
      this.fangPian = fangPian;
      return this;
    }

    public List<Poker> getMeiHuan() {
      return meiHuan;
    }

    public PokerClassify setMeiHuan(List<Poker> meiHuan) {
      this.meiHuan = meiHuan;
      return this;
    }

    public List<Poker> getHongTao() {
      return hongTao;
    }

    public PokerClassify setHongTao(List<Poker> hongTao) {
      this.hongTao = hongTao;
      return this;
    }

    public List<Poker> getHeiTao() {
      return heiTao;
    }

    public PokerClassify setHeiTao(List<Poker> heiTao) {
      this.heiTao = heiTao;
      return this;
    }

    public PokerClassify add(Poker poker) {
      if (poker.getPokerType() == PokerType.GUI || poker.getValue() == 2
          || poker.getValue() == peopleInfo.getTable().getPlayNumber()) {
        zhu.add(poker);
        Collections.sort(zhu);
      } else {
        switch (poker.getPokerType()) {
          case FANGP:
            fangPian.add(poker);
            PokersBo.sortClassify(fangPian);
            break;
          case MEIH:
            meiHuan.add(poker);
            PokersBo.sortClassify(meiHuan);
            break;
          case HONGT:
            hongTao.add(poker);
            PokersBo.sortClassify(hongTao);
            break;
          case HEIT:
            heiTao.add(poker);
            PokersBo.sortClassify(heiTao);
            break;
        }
      }
      return this;
    }

    public void remove(List<Poker> pokerList) {
      for (Poker poker : pokerList) {
        remove(poker);
      }
    }

    public void remove(Poker poker) {
      if (poker.getPokerType() == PokerType.GUI || poker.getValue() == 2
          || poker.getValue() == peopleInfo.getTable().getPlayNumber()) {
        zhu.remove(poker);
      } else {
        switch (poker.getPokerType()) {
          case FANGP:
            fangPian.remove(poker);
          case MEIH:
            meiHuan.remove(poker);
          case HONGT:
            hongTao.remove(poker);
          case HEIT:
            heiTao.remove(poker);
        }
      }
    }

  }

  public enum OutPokerType {
    ONE(1),//一张
    TWO(2),//一对
    THREE(3),//三张
    FOUR(4),//四张
    TLJ(5),//拖拉机
    HH(6),//混合牌型
    UNKNOWN(100);//不符合的牌型

    OutPokerType(int value) {
      this.value = value;
    }

    public int value;
  }

  /**
   * 设置底牌
   */
  public void setDipai(List<Poker> dipai) {
    this.dipai.addAll(dipai);
    for (Poker poker : dipai) {
      add(poker);
    }
  }

  public List<Poker> getDipai() {
    return dipai;
  }

  /**
   * 发牌
   */
  public void add(Poker poker) {
    if (poker == null) {
      return;
    }
    all.add(poker);
    pokerClassify.add(poker);
    if (allPokerMap.containsKey(poker.getPokerType())) {
      allPokerMap.get(poker.getPokerType()).add(poker);
    } else {
      List<Poker> pokerList = new ArrayList<>();
      pokerList.add(poker);
      allPokerMap.put(poker.getPokerType(), pokerList);
    }

    //todo
    Collections.sort(allPokerMap.get(poker.getPokerType()));
  }

  /**
   * 出牌
   */
  public void outPokers(List<Poker> pokerList) {
    alreadyOutPokers.add(pokerList);
    all.removeAll(pokerList);
    pokerClassify.remove(pokerList);
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

  /**
   * 解析出牌的类型（ ONE,//一张 TWO,//一对 THREE,//三张 FOUR,//四张 TLJ,//拖拉机 HH,//混合牌型 UNKNOWN//不符合的牌型 ）
   */
  public OutPokerType parseOutPoker(List<Poker> pokerList) {
    Zhu zhu = peopleInfo.getTable().getZhu();
    if (pokerList.size() == 1) {
      outPokerType = OutPokerType.ONE;
    }
    if (validSamePoker(pokerList)) {
      if (pokerList.size() == 2) {
        outPokerType = OutPokerType.TWO;
      }
      if (pokerList.size() == 3) {
        outPokerType = OutPokerType.THREE;
      }
      if (pokerList.size() == 4) {
        outPokerType = OutPokerType.FOUR;
      }
      outPokerType = OutPokerType.UNKNOWN;
    }
    if (validTLJ(pokerList, zhu)) {
      outPokerType = OutPokerType.TLJ;
    }
    if (validSameColor(pokerList) || validAllZhu(pokerList, zhu)) {
      outPokerType = OutPokerType.HH;
    }
    outPokerType = OutPokerType.UNKNOWN;
    return outPokerType;
  }

  public boolean validFirstOneOutPoker() {
    return outPokerType != OutPokerType.UNKNOWN;
  }

  public boolean validOutPokerInHis(List<Poker> pokerList) {
    boolean find = true;
    for (Poker poker : pokerList) {
      for (Poker has : all) {
        if (poker.getPokerType() == has.getPokerType() && poker.getValue() == has.getValue()) {
          break;
        } else {
          find = false;
        }
      }
    }
    return find;
  }

  public int getExitNumber(Poker poker) {
    Zhu zhu = peopleInfo.getTable().getZhu();
    boolean isZhu = zhu.validPokerIsZhu(poker);
    int count = 0;
    for (Poker poker1 : all) {
      if (isZhu) {
        if (zhu.validPokerIsZhu(poker1)) {
          ++count;
        }
      } else if (poker.getPokerType() == poker1.getPokerType()) {
        ++count;
      }
    }
    return count;
  }

  /**
   * 验证是否是拖拉机
   */
  private boolean validTLJ(List<Poker> pokerList, Zhu zhu) {
    if (CollectionUtils.isEmpty(pokerList) || pokerList.size() < 4 || pokerList.size() % 2 != 0) {
      return false;
    }
    PokersBo.sortTlj(pokerList);
    if (validSameColor(pokerList)) {
      int count = 0;
      int before = count;
      Poker tmp = null;
      int number = 0;
      for (Poker poker : pokerList) {
        if (zhu.validPokerIsZhu(poker)) {
          ++number;
        }
        if (tmp == null) {
          tmp = poker;
          ++count;
        } else if (tmp.getValue() == poker.getValue()) {
          ++count;
        } else {
          if ((tmp.getValue() + 2 == poker.getValue() && tmp.getPokerType() != zhu.getPokerType()
              && tmp.getValue() + 1 == zhu.getPlayNumber())
              || (tmp.getValue() + 1 == poker.getValue())
              || (tmp.getValue() == 13 && poker.getValue() == 1)) {//非主拖拉机可以隔开打几的那张牌，主的拖拉机不可以隔开
            tmp = poker;
            if (before == 0) {
              before = count;
            } else if (before != count) {
              return false;
            }
            count = 0;
          } else {
            return false;
          }
        }
      }
      if (number != 0 && number != pokerList.size()) {//要么都是主，要么都是副牌
        return false;
      }
    }
    return true;
  }

  /**
   * 验证是否是同色
   */
  private boolean validSameColor(List<Poker> pokerList) {
    Poker tmp = null;
    for (Poker poker : pokerList) {
      if (tmp == null) {
        tmp = poker;
      } else if (tmp.getPokerType() == poker.getPokerType()) {
        continue;
      } else {
        return false;
      }
    }
    return true;
  }

  /**
   * 验证是否是同色同大小
   */
  private boolean validSamePoker(List<Poker> pokerList) {
    Poker tmp = null;
    for (Poker poker : pokerList) {
      if (tmp == null) {
        tmp = poker;
      } else if (tmp.getPokerType() == poker.getPokerType() && tmp.getValue() == poker.getValue()) {
        continue;
      } else {
        return false;
      }
    }
    return true;
  }

  /**
   * 验证都是主吗
   */
  private boolean validAllZhu(List<Poker> pokerList, Zhu zhu) {
    for (Poker poker : pokerList) {
      if (!zhu.validPokerIsZhu(poker)) {
        return false;
      }
    }
    return true;
  }

  /**
   * 是否有多张一样的
   *
   * @param number 数量:1到4
   */
  public boolean haveMore(Poker poker, int number) {
    List<Poker> pokerList = getPokersByPokerType(poker, number);
    if (CollectionUtils.isEmpty(pokerList)) {
      return false;
    }
    return true;
  }

  public boolean validOutPokerTlj(List<Poker> firstOutPokerList, List<Poker> meOutPokerList) {
    List<Poker> haveList = getPokersByPokerType(firstOutPokerList.get(0));
    TljType tljType = TljType.parse(firstOutPokerList);
    PokersBo.sortTlj(haveList);
    if (isHaveTlj(haveList, tljType)) {
      return false;
    }
    return validTljOutPoker(haveList, meOutPokerList, tljType);
  }

  private boolean validTljOutPoker(List<Poker> meHavePokerListP, List<Poker> meOutPokerListP,
      TljType firstTljType) {
    boolean result = false;
    List<Poker> meHavePokerList = new ArrayList<>(meHavePokerListP);
    List<Poker> meOutPokerList = new ArrayList<>(meOutPokerListP);
    switch (firstTljType.getCount()) {
      case 2:
        result = tlj2(meHavePokerList, meOutPokerList, firstTljType.step);
        break;
      case 3:
        result = tlj3(meHavePokerList, meOutPokerList, firstTljType.step);
        break;
      case 4:
        result = tlj4(meHavePokerList, meOutPokerList, firstTljType.step);
        break;
      default:
        break;
    }
    return result;
  }

  private boolean tlj2(List<Poker> meHavePokerList, List<Poker> meOutPokerList, int length) {
    int me_pokerList_2 = getPokers(meHavePokerList, 2).size();
    int out_pokerList_2 = getPokers(meOutPokerList, 2).size();
    List<Integer> me_tlj_2 = allTlj(meHavePokerList, 2);
    List<Integer> out_tlj_2 = allTlj(meOutPokerList, 2);
    Collections.sort(me_tlj_2, Collections.reverseOrder());
    Collections.sort(out_tlj_2, Collections.reverseOrder());
    int tmp = length;
    List<Integer> resultTljLengths = new ArrayList<>();
    for (int i = 0; i < me_tlj_2.size(); i++) {
      if (length - me_tlj_2.get(i) <= 0) {
        return false;
      } else {
        if (tmp - me_tlj_2.get(i) > 0) {
          resultTljLengths.add(me_tlj_2.get(i));
        } else {
          resultTljLengths.add(tmp);
        }
        tmp = tmp - me_tlj_2.get(i);
      }
    }
    if (out_tlj_2.size() != resultTljLengths.size()) {
      return false;
    }
    for (int i = 0; i < resultTljLengths.size(); i++) {
      if (resultTljLengths.get(i) != out_tlj_2.get(i)) {
        return false;
      }
    }
    if (me_pokerList_2 > length) {
      if (out_pokerList_2 != length) {
        return false;
      }
    } else if (me_pokerList_2 != out_pokerList_2) {
      return false;
    }
    return true;
  }

  private boolean tlj3(List<Poker> meHavePokerList, List<Poker> meOutPokerList, int length) {
    List<Poker> me_pokerList_3 = getPokers(meHavePokerList, 3);
    List<Poker> out_pokerList_3 = getPokers(meOutPokerList, 3);
    if (me_pokerList_3.size() >= length) {
      if (length != out_pokerList_3.size()) {
        return false;
      }
    } else {
      if (me_pokerList_3.size() != out_pokerList_3.size()) {
        return false;
      }
      remove(me_pokerList_3, meHavePokerList);
      remove(out_pokerList_3, meOutPokerList);
      return tlj2(meHavePokerList, meOutPokerList, length - out_pokerList_3.size());
    }
    return true;
  }

  private void remove(List<Poker> me_pokerList_3, List<Poker> meHavePokerList) {
    for (Poker poker : me_pokerList_3) {
      for (Iterator<Poker> p = meHavePokerList.iterator(); p.hasNext(); ) {
        if (p.next().getValue() == poker.getValue()) {
          p.remove();
        }
      }
    }
  }

  private boolean tlj4(List<Poker> meHavePokerList, List<Poker> meOutPokerList, int length) {
    List<Poker> me_pokerList_4 = getPokers(meHavePokerList, 4);
    List<Poker> out_pokerList_4 = getPokers(meOutPokerList, 4);
    if (me_pokerList_4.size() >= length) {
      if (length != out_pokerList_4.size()) {
        return false;
      }
    } else {
      if (me_pokerList_4.size() != out_pokerList_4.size()) {
        return false;
      }
      remove(me_pokerList_4, meHavePokerList);
      remove(out_pokerList_4, meOutPokerList);
      return tlj3(meHavePokerList, meOutPokerList, length - out_pokerList_4.size());
    }
    return true;
  }

  private List<TljType> parseAllTlj(List<Poker> pokerList) {
    int count = 1;
    int step = 1;
    Poker tmp = pokerList.get(0);
    Map<Integer, Integer> numberMap = new LinkedHashMap<>();
    for (int i = 1; i < pokerList.size(); i++) {
      if (tmp.eq(pokerList.get(i))) {
        ++count;
      } else {
        numberMap.put(tmp.getValue(), count);
        tmp = pokerList.get(i);
        count = 1;
      }
    }
    if (count > 1) {
      numberMap.put(tmp.getValue(), count);
    }
    Entry<Integer, Integer> tmpEntry = null;
    boolean find = false;
    List<TljType> result = new ArrayList<>();
    for (Entry<Integer, Integer> entry : numberMap.entrySet()) {
      if (tmpEntry == null) {
        tmpEntry = entry;
        step = 1;
        count = tmpEntry.getValue();
      } else if (tmpEntry.getValue() == count && entry.getValue() == count && (
          tmpEntry.getKey() + 1 == entry.getKey() || tmpEntry.getKey() == 13)) {
        ++step;
        find = true;
      } else {
        if (find) {
          result.add(new TljType(step, count));
        }
        find = false;
        tmpEntry = entry;
        step = 1;
        count = tmpEntry.getValue();
      }
    }
    if (find) {
      result.add(new TljType(step, count));
    }
    return result;
  }

  private boolean isHaveTlj(List<Poker> pokerList, TljType tljType) {
    int count = 1;
    int step = 1;
    Poker tmp = pokerList.get(0);
    Map<Integer, Integer> numberMap = new LinkedHashMap<>();
    for (int i = 1; i < pokerList.size(); i++) {
      if (tmp.eq(pokerList.get(i))) {
        ++count;
      } else {
        numberMap.put(tmp.getValue(), count);
        tmp = pokerList.get(i);
        count = 1;
      }
    }
    if (count > 1) {
      numberMap.put(tmp.getValue(), count);
    }
    Entry<Integer, Integer> tmpEntry = null;
    for (Entry<Integer, Integer> entry : numberMap.entrySet()) {
      if (tmpEntry == null) {
        tmpEntry = entry;
        step = 1;
      } else if (tmpEntry.getValue() >= tljType.getCount()
          && entry.getValue() >= tljType.getCount() && (tmpEntry.getKey() + 1 == entry.getKey()
          || tmpEntry.getKey() == 13)) {
        ++step;
      } else {
        tmpEntry = entry;
        step = 1;
      }
      if (step == tljType.getStep()) {
        return true;
      }
    }
    return false;
  }

  private List<Integer> allTlj(List<Poker> pokerList, int counts) {
    int count = 1;
    int step = 1;
    Poker tmp = pokerList.get(0);
    Map<Integer, Integer> numberMap = new LinkedHashMap<>();
    for (int i = 1; i < pokerList.size(); i++) {
      if (tmp.eq(pokerList.get(i))) {
        ++count;
      } else {
        numberMap.put(tmp.getValue(), count);
        tmp = pokerList.get(i);
        count = 1;
      }
    }
    if (count > 1) {
      numberMap.put(tmp.getValue(), count);
    }
    Entry<Integer, Integer> tmpEntry = null;
    List<Integer> resultList = new ArrayList<>();
    for (Entry<Integer, Integer> entry : numberMap.entrySet()) {
      if (tmpEntry == null) {
        tmpEntry = entry;
        step = 1;
      } else if (tmpEntry.getValue() >= counts
          && entry.getValue() >= counts && (tmpEntry.getKey() + 1 == entry.getKey()
          || tmpEntry.getKey() == 13)) {
        ++step;
      } else {
        if (step > 1) {
          resultList.add(step);
        }
        tmpEntry = entry;
        step = 1;
      }
    }
    if (step > 1) {
      resultList.add(step);
    }
    return resultList;
  }

  public static class TljType implements Comparable<TljType> {

    private int step;//步长（一共几拖）
    private int count;//对，三条，四条

    public TljType(int step, int count) {
      this.step = step;
      this.count = count;
    }

    public int getStep() {
      return step;
    }

    public TljType setStep(int step) {
      this.step = step;
      return this;
    }

    public int getCount() {
      return count;
    }

    public TljType setCount(int count) {
      this.count = count;
      return this;
    }

    /**
     * 调用这个方法，必须是已经确认为拖拉机
     */
    public static TljType parse(List<Poker> pokerList) {
      int count = 1;
      Collections.sort(pokerList);
      Poker tmp = null;
      for (Poker poker : pokerList) {
        if (tmp == null) {
          tmp = poker;
        } else if (tmp.getValue() == poker.getValue()) {
          ++count;
        } else {
          break;
        }
      }
      int step = pokerList.size() / count;
      return new TljType(step, count);
    }

    @Override
    public int compareTo(TljType o) {
      if (count > o.getCount()) {
        return 1;
      } else if (count < o.getCount()) {
        return -1;
      }
      return 0;
    }
  }


  public boolean validOutPokerNumber(Poker first, List<Poker> outPokerList, int number) {
    if (haveMore(first, number)) {
      return false;
    }
    Zhu zhu = peopleInfo.getTable().getZhu();
    List<Poker> outPokerSameFirst = PokersBo.filterSamePoker(first, outPokerList, zhu);
    if (number == 4 && haveMore(first, number - 1)) {//如果没有四张，得出三张，没有三张，得出两对
      if (CollectionUtils.isNotEmpty(getPokers(outPokerSameFirst, number - 1))) {
        return true;
      } else {
        return false;
      }
    }
    if (number == 4 && getPokersByPokerType(first, number - 2).size() != getPokers(
        outPokerSameFirst, number - 2).size()) {
      return false;
    }
    if (number == 3 && haveMore(first, number - 1)) {
      if (CollectionUtils.isNotEmpty(getPokers(outPokerSameFirst, number - 1))) {
        return true;
      } else {
        return false;
      }
    }
    return true;
  }

  public void clean() {
    all.clear();
    allPokerMap.clear();
    alreadyOutPokers.clear();
  }
}
