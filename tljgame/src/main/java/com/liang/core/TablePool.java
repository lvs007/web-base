package com.liang.core;

import com.liang.bo.PeopleInfo;
import com.liang.bo.Table;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class TablePool {

  private static final int SIZE = 100;
  private volatile int count;
  private Map<String, Table> tableList = new ConcurrentHashMap<>(SIZE);

  public TablePool() {
    init();
  }

  private void init() {
    for (int i = 0; i < 100; i++) {
      if (count < SIZE) {
        String tableId = UUID.randomUUID().toString().replaceAll("-", "");
        Table table = new Table(tableId, this);
        tableList.put(tableId, table);
        ++count;
      } else {
        return;
      }
    }
  }

  public boolean add(String tableId, int site, PeopleInfo peopleInfo) {
    if (tableList.containsKey(tableId)) {
      return tableList.get(tableId).join(peopleInfo, site);
    }
    return false;
  }

  public boolean add(PeopleInfo peopleInfo) {
    for (Table table : tableList.values()) {
      for (int site = 1; site <= 4; site++) {
        if (table.join(peopleInfo, site)) {
          return true;
        }
      }
    }
    return false;
  }

  public Map<String, Table> getTableList() {
    return tableList;
  }
}
