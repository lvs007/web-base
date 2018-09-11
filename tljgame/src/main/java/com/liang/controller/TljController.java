package com.liang.controller;

import com.liang.bo.Table;
import com.liang.core.TablePool;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;

@Controller
public class TljController {

  @Autowired
  private TablePool tablePool;

  public String listTables(ModelMap modelMap) {
    Map<String, Table> tableMap = tablePool.getTableList();
    modelMap.put("tableMap", tableMap);
    return "list";
  }

  public String add(String tableId, int site) {
//    tablePool.add(tableId, site, );
    return "";
  }

}
