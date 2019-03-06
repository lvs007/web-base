package com.liang.sangong.common;

public class ThreadUtils {

  public static void sleep(long time) {
    try {
      Thread.sleep(time);
    } catch (InterruptedException e) {
    }
  }
}
