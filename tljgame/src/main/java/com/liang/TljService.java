package com.liang;

import com.liang.bo.HumanPoker.PokerClassify;
import com.liang.bo.PeopleInfo;
import com.liang.bo.PeopleInfo.PeopleStatus;
import com.liang.bo.PokersBo;
import com.liang.bo.PokersBo.Poker;
import com.liang.bo.PokersBo.PokerType;
import com.liang.bo.Table;
import com.liang.bo.Table.Site;
import com.liang.bo.Table.Zhu;
import com.liang.core.TablePool;
import com.liang.vo.PeopleVo;
import com.liang.vo.PokerVo;
import com.liang.vo.TableVo;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import liang.mvc.filter.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TljService {

  @Autowired
  private TablePool tablePool;

  public TableVo getTableVo(String tableId, UserInfo userInfo) {
    TableVo tableVo = new TableVo();
    Table table = tablePool.getTable(tableId);
    if (table == null) {
      return tableVo;
    }
    tableVo.setTableId(tableId);
    tableVo.setStart(table.isStart());
    tableVo.setFinish(table.isFinish());

    PeopleInfo me = null;
    for (Entry<Site, PeopleInfo> entry : table.getPlayPeople().entrySet()) {
      if (entry.getValue().getId() == userInfo.getId()) {
        me = entry.getValue();
        break;
      }
    }
    if (me == null) {
      return tableVo;
    }
    PeopleVo meVo = buildPeopleVo(me, table);
    setValue(meVo, me);
    tableVo.setMe(meVo);
    Site site = Site.nextSite(me.getSite());
    tableVo.setSite1(buildPeopleVo(table.getPlayPeople().get(site), table));
    site = Site.nextSite(site);
    tableVo.setSite2(buildPeopleVo(table.getPlayPeople().get(site), table));
    site = Site.nextSite(site);
    tableVo.setSite3(buildPeopleVo(table.getPlayPeople().get(site), table));
    return tableVo;
  }

  private void setValue(PeopleVo meVo, PeopleInfo me) {
    List<PokerVo> pokerVoList = new ArrayList<>();
    List<Poker> pokerAllList = new ArrayList<>();
    PokerClassify pokerClassify = me.getHumanPoker().getPokerClassify();
    List<Poker> pokerList = pokerClassify.getZhu();
    Zhu zhu = me.getTable().getZhu();
    if (zhu == null) {
      zhu = new Zhu(PokerType.HEIT, me.getTable().getPlayNumber());
    }
    PokersBo.sortFormMaxToMin(pokerList, zhu);
    pokerAllList.addAll(pokerList);
    pokerList = pokerClassify.getHeiTao();
    PokersBo.sortFormMaxToMin(pokerList, zhu);
    pokerAllList.addAll(pokerList);
    pokerList = pokerClassify.getHongTao();
    PokersBo.sortFormMaxToMin(pokerList, zhu);
    pokerAllList.addAll(pokerList);
    pokerList = pokerClassify.getMeiHuan();
    PokersBo.sortFormMaxToMin(pokerList, zhu);
    pokerAllList.addAll(pokerList);
    pokerList = pokerClassify.getFangPian();
    PokersBo.sortFormMaxToMin(pokerList, zhu);
    pokerAllList.addAll(pokerList);
    for (Poker poker : pokerAllList) {
      pokerVoList.add(PokerVo.build(poker));
    }
    meVo.setPokerList(pokerVoList);
  }

  private PeopleVo buildPeopleVo(PeopleInfo me, Table table) {
    PeopleVo peopleVo = new PeopleVo();
    if (me == null) {
      return peopleVo;
    }
    peopleVo.setHeadImgSrc(null);
    peopleVo.setSite(me.getSite());
    peopleVo.setReady(me.getStatus() == PeopleStatus.CONFIRM);
    peopleVo.setPeopleId(me.getId());
    if (table.getCurrentFirstOneOutPikerPeople() != null) {
      peopleVo.setNowPlay(table.getCurrentFirstOneOutPikerPeople().getId() == me.getId());
    }
    return peopleVo;
  }

  public boolean confirm(String tableId, UserInfo userInfo) {
    PeopleInfo peopleInfo = getPeopleInfo(tableId, userInfo);
    if (peopleInfo == null) {
      return false;
    }
    return peopleInfo.confirm();
  }

  public boolean unConfirm(String tableId, UserInfo userInfo) {
    PeopleInfo peopleInfo = getPeopleInfo(tableId, userInfo);
    if (peopleInfo == null) {
      return false;
    }
    return peopleInfo.unConfirm();
  }

  public PeopleInfo getPeopleInfo(String tableId, UserInfo userInfo) {
    Table table = tablePool.getTable(tableId);
    if (table == null) {
      return null;
    }
    return table.getPeopleInfo(userInfo.getId());
  }

  public Table add(String tableId, int site, PeopleInfo peopleInfo) {
    for (Table table : tablePool.getTableList().values()) {
      for (PeopleInfo people : table.getPlayPeople().values()) {
        if (people.getId() == peopleInfo.getId()) {
          return table;
        }
      }
    }
    Table table = tablePool.getTable(tableId);
    if (table.join(peopleInfo, site)) {
      return table;
    }
    return null;
  }
}
