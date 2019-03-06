package com.liang.spider.weibo;

import com.liang.spider.AbstractSpider;
import com.liang.spider.Crower;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

public class Weibo extends AbstractSpider implements Crower {

  @Override
  public void spider(String url) {
    WebDriver driver = new RemoteWebDriver(DesiredCapabilities.chrome());
    driver.get("http://www.google.com");
//    webDriver.get(url);
  }

  public static void main(String[] args) {
    String url = "http://reg.email.163.com/unireg/call.do?cmd=register.entrance&from=163mail_right";
    Weibo weibo = new Weibo();
    weibo.spider(url);
  }
}
