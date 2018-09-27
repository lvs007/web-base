package com.liang;

import com.liang.bo.PeopleInfo;
import com.liang.bo.PeopleInfo.PeopleStatus;
import com.liang.bo.Table;
import com.liang.bo.Table.Site;
import com.liang.core.TablePool;
import com.liang.vo.PeopleVo;
import com.liang.vo.TableVo;
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
    tableVo.setMe(buildPeopleVo(me, table));
    Site site = Site.nextSite(me.getSite());
    tableVo.setSite1(buildPeopleVo(table.getPlayPeople().get(site), table));
    site = Site.nextSite(site);
    tableVo.setSite2(buildPeopleVo(table.getPlayPeople().get(site), table));
    site = Site.nextSite(site);
    tableVo.setSite3(buildPeopleVo(table.getPlayPeople().get(site), table));
    return tableVo;
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
}
