package com.liang.bo;

import com.liang.bo.PokersBo.Poker;
import com.liang.bo.Table.Site;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatisticsData {

  private static final int addBase = 80;
  private static final int step1 = 160;
  private static final int step2 = 240;

  private int score;

  private Map<Site, Integer> scoreMap = new HashMap<>();

  public StatisticsData() {
    init();
  }

  private void init() {
    scoreMap.put(Site.D, 0);
    scoreMap.put(Site.N, 0);
    scoreMap.put(Site.X, 0);
    scoreMap.put(Site.B, 0);
  }

  public void statisticsScore(PeopleInfo peopleInfo, Map<Site, PeopleInfo> playPeople) {
    int score = 0;
    for (PeopleInfo p : playPeople.values()) {
      score += compute(p.getHumanPoker().getCurrentOutPoker());
      p.resetCycle();
    }
  }

  private int compute(List<Poker> pokerList) {
    int score = 0;
    for (Poker poker : pokerList) {
      if (5 == poker.getValue()) {
        score += 5;
      } else if (10 == poker.getValue() || 13 == poker.getValue()) {
        score += 10;
      }
    }
    return score;
  }

  public int getScore() {
    return score;
  }

  public StatisticsData setScore(int score) {
    this.score = score;
    return this;
  }

  public void clean() {

  }
}
