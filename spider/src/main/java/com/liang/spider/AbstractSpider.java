package com.liang.spider;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by kiven on 2018/4/20.
 */
public abstract class AbstractSpider implements Crower {

    protected final Logger LOG = LoggerFactory.getLogger(getClass());

    protected WebDriver webDriver;

    public AbstractSpider() {
//        System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver");
//        webDriver = new ChromeDriver();
    }
}
