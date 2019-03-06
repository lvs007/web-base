package com.liang.spider.proxypool.htmlparse;

import static java.lang.System.out;

import java.util.List;

/**
 * Created by hg_yi on 17-8-8.
 */
public class IPThread extends Thread {

  private List<String> urls;
  private IPPool ipPool;

  public IPThread(List<String> urls, IPPool ipPool) {
    this.urls = urls;
    this.ipPool = ipPool;
  }

  @Override
  public void run() {
    //进行ip的抓取
    for (String url : urls) {
      out.println(Thread.currentThread().getName() + "爬取的地址为：" + url);
    }
    ipPool.getIP(urls);
  }
}
