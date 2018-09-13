package com.liang.controller;

import com.liang.bo.Table;
import com.liang.common.TransferTo;
import com.liang.core.TablePool;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import liang.mvc.annotation.PcLogin;
import liang.mvc.commons.SpringContextHolder;
import liang.mvc.filter.LoginUtils;
import liang.mvc.filter.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;

@Controller
public class TljController {

  @Autowired
  private TablePool tablePool;

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

  @PcLogin
  public String add(String tableId, int site, ModelMap modelMap) throws IOException {
    UserInfo userInfo = LoginUtils.getCurrentUser(SpringContextHolder.getRequest());
    boolean result = tablePool.add(tableId, site, TransferTo.transferTo(userInfo));
    modelMap.put("success",result);
    if (result) {
      return "table";
    } else {
      SpringContextHolder.getResponse().sendRedirect("/v1/tlj/add");
      return "";
    }

  }

}
