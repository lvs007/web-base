package com.liang.sangong.core;

import com.liang.sangong.bo.DataStatistics;
import com.liang.sangong.bo.PeopleInfo.PeopleType;
import com.liang.sangong.bo.UserResult;
import com.liang.sangong.bo.UserResult.ResultEnum;
import com.liang.sangong.service.UserService;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CounterManager {

  private static final Logger logger = LoggerFactory.getLogger(CounterManager.class);

  private final Map<PeopleType, AtomicLong> count = new ConcurrentHashMap<>();

  private final Map<Long, Map<String, DataStatistics>> dataStatisticsMap = new ConcurrentHashMap<>();

  @Autowired
  private UserService userService;

  private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1,
      r -> new Thread(r, "counter-thread"));

  public CounterManager() {
    count.put(PeopleType.TRX, new AtomicLong(0));
    count.put(PeopleType.JI_FEN, new AtomicLong(0));
    scheduledExecutorService.scheduleWithFixedDelay(() -> log(), 5, 5, TimeUnit.MINUTES);
  }

  public void incr(PeopleType type, long value) {
    if (type == null || value <= 0) {
      return;
    }
    count.get(type).addAndGet(value);
  }

  public void statistics(UserResult userResult) {
    Map<String, DataStatistics> dataStatistics = dataStatisticsMap.get(userResult.getUserId());
    String key = buildKey(userResult);
    DataStatistics data;
    if (dataStatistics == null) {
      dataStatistics = new ConcurrentHashMap<>();
      data = DataStatistics.build(userResult.getUserId(), userResult.getType());
    } else {
      data = dataStatistics.get(key);
      if (data == null) {
        data = DataStatistics.build(userResult.getUserId(), userResult.getType());
      }
    }
    if (userResult.getResult() == ResultEnum.fail.code) {
      data.setShuAmount(userResult.getCoin())
          .setShuCount(data.getShuCount() + 1);
    } else if (userResult.getResult() == ResultEnum.win.code) {
      data.setWinAmount(userResult.getCoin())
          .setWinCount(data.getWinCount() + 1);
    }
    dataStatistics.put(key, data);
    userService.insertOrUpdateDataStatistics(data);
    dataStatisticsMap.put(userResult.getUserId(), dataStatistics);
  }

  private String buildKey(UserResult userResult) {
    return userResult.getUserId() + "-" + userResult.getType();
  }

  @PreDestroy
  public void log() {
    logger.info("[CounterManager] from now trx rate is : {}, jifen rate is : {}",
        count.get(PeopleType.TRX).get(), count.get(PeopleType.JI_FEN).get());
  }
}
