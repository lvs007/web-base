package com.liang.service;

import com.liang.common.util.PropertiesManager;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConfigService {

  @Autowired
  private PropertiesManager propertiesManager;

  public boolean startTcpService() {
    return StringUtils.equalsIgnoreCase("true",
        propertiesManager.getString("open.tcp.service", "true"));
  }

}
