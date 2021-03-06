package com.liang.controller;

import com.liang.bo.PeopleInfo;
import com.liang.bo.Table;
import com.liang.common.TransferTo;
import com.liang.core.TablePool;
import com.liang.mvc.annotation.PcLogin;
import com.liang.mvc.commons.SpringContextHolder;
import com.liang.mvc.filter.LoginUtils;
import com.liang.mvc.filter.UserInfo;
import com.liang.service.TljService;
import com.liang.vo.TableVo;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;

@Controller
public class TljController {

  @Autowired
  private TablePool tablePool;

  @Autowired
  private TljService tljService;

  public String listTables(ModelMap modelMap) {
    Map<String, Table> tableMap = tablePool.getTableList();
    int i = 0;
    List<Map<String, Table>> listMap = new ArrayList<>();
    Map<String, Table> map = new HashMap<>();
    for (Entry<String, Table> entry : tableMap.entrySet()) {
      if (i % 5 == 0) {
        map = new HashMap<>();
        listMap.add(map);
      }
      map.put(entry.getKey(), entry.getValue());
      ++i;
    }
    modelMap.put("tableMap", tableMap);
    modelMap.put("listMap", listMap);
    return "list";
  }

  public String index() {
    return "index";
  }

  @PcLogin
  public String add(String tableId, int site) throws IOException {
    UserInfo userInfo = LoginUtils.getCurrentUser(SpringContextHolder.getRequest());
    PeopleInfo peopleInfo = TransferTo.transferTo(userInfo);
    Table result = tljService.add(tableId, site, peopleInfo);
    if (result != null) {
      return "redirect:/v1/tlj/table?tableId=" + result.getTableId();
    } else {
      return "redirect:/v1/tlj/list-tables";
    }
  }

  @PcLogin
  public String confirm(String tableId) {
    UserInfo userInfo = LoginUtils.getCurrentUser(SpringContextHolder.getRequest());
    tljService.confirm(tableId, userInfo);
    return "redirect:/v1/tlj/table?tableId=" + tableId;
  }

  @PcLogin
  public String unConfirm(String tableId) {
    UserInfo userInfo = LoginUtils.getCurrentUser(SpringContextHolder.getRequest());
    tljService.unConfirm(tableId, userInfo);
    return "redirect:/v1/tlj/table?tableId=" + tableId;
  }

  @PcLogin
  public String table(String tableId, ModelMap modelMap) {
    UserInfo userInfo = LoginUtils.getCurrentUser(SpringContextHolder.getRequest());
    TableVo tableVo = tljService.getTableVo(tableId, userInfo);
    modelMap.put("table", tableVo);
    return "table";
  }

}
