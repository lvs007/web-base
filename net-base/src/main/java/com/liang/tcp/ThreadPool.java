package com.liang.tcp;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ThreadPool {

  private static final Logger logger = LoggerFactory.getLogger(ThreadPool.class);

  private ExecutorService sendMessageService = Executors.newFixedThreadPool(100,
      new ThreadFactoryBuilder().setNameFormat("Send-Msg-Pool-%d").build());

  private ScheduledExecutorService logService = Executors.newSingleThreadScheduledExecutor(
      new ThreadFactoryBuilder().setNameFormat("Log-Peer-Pool").build());

  public void executeMessage(Runnable runnable) {
    sendMessageService.execute(runnable);
  }

  public void executeLog(Runnable runnable) {
    logService.scheduleWithFixedDelay(runnable, 1, 1, TimeUnit.MINUTES);
  }

  @PostConstruct
  public void print() {
    ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) sendMessageService;
    Executors.newSingleThreadScheduledExecutor(
        new ThreadFactoryBuilder().setNameFormat("Print-Pool").build()).
        scheduleWithFixedDelay(() -> logger.info("{}", threadPoolExecutor.toString()), 110, 110,
            TimeUnit.SECONDS);
  }

  @PreDestroy
  public void destroy() {
    sendMessageService.shutdown();
    logService.shutdown();
  }
}
