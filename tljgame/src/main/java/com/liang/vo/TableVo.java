package com.liang.vo;

import com.liang.bo.Table.Site;
import java.util.List;

public class TableVo {

  private String tableId;
  private List<PeopleVo> peopleVoList;
  private PeopleVo me;
  private PeopleVo site1;
  private PeopleVo site2;
  private PeopleVo site3;
  private boolean start;
  private boolean finish;

  public String getTableId() {
    return tableId;
  }

  public TableVo setTableId(String tableId) {
    this.tableId = tableId;
    return this;
  }

  public List<PeopleVo> getPeopleVoList() {
    return peopleVoList;
  }

  public TableVo setPeopleVoList(List<PeopleVo> peopleVoList) {
    this.peopleVoList = peopleVoList;
    return this;
  }

  public PeopleVo getMe() {
    return me;
  }

  public TableVo setMe(PeopleVo me) {
    this.me = me;
    return this;
  }

  public PeopleVo getSite1() {
    return site1;
  }

  public TableVo setSite1(PeopleVo site1) {
    this.site1 = site1;
    return this;
  }

  public PeopleVo getSite2() {
    return site2;
  }

  public TableVo setSite2(PeopleVo site2) {
    this.site2 = site2;
    return this;
  }

  public PeopleVo getSite3() {
    return site3;
  }

  public TableVo setSite3(PeopleVo site3) {
    this.site3 = site3;
    return this;
  }

  public boolean isStart() {
    return start;
  }

  public TableVo setStart(boolean start) {
    this.start = start;
    return this;
  }

  public boolean isFinish() {
    return finish;
  }

  public TableVo setFinish(boolean finish) {
    this.finish = finish;
    return this;
  }
}
