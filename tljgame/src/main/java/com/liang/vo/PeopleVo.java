package com.liang.vo;

import com.liang.bo.PokersBo.Poker;
import com.liang.bo.Table.Site;
import java.util.List;

public class PeopleVo {

  private long peopleId;
  private Site site;
  private String headImgSrc;
  private boolean ready;
  private boolean isNowPlay;//
  private List<PokerVo> pokerList;

  public long getPeopleId() {
    return peopleId;
  }

  public PeopleVo setPeopleId(long peopleId) {
    this.peopleId = peopleId;
    return this;
  }

  public Site getSite() {
    return site;
  }

  public PeopleVo setSite(Site site) {
    this.site = site;
    return this;
  }

  public String getHeadImgSrc() {
    return headImgSrc;
  }

  public PeopleVo setHeadImgSrc(String headImgSrc) {
    this.headImgSrc = headImgSrc;
    return this;
  }

  public boolean isReady() {
    return ready;
  }

  public PeopleVo setReady(boolean ready) {
    this.ready = ready;
    return this;
  }

  public boolean isNowPlay() {
    return isNowPlay;
  }

  public PeopleVo setNowPlay(boolean nowPlay) {
    isNowPlay = nowPlay;
    return this;
  }

  public List<PokerVo> getPokerList() {
    return pokerList;
  }

  public PeopleVo setPokerList(List<PokerVo> pokerList) {
    this.pokerList = pokerList;
    return this;
  }
}
