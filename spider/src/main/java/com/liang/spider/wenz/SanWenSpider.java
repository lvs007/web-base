package com.liang.spider.wenz;

import com.liang.spider.AbstractSpider;
import com.liang.spider.Crower;
import com.liang.spider.findelement.FindValue;
import com.liang.spider.findelement.FindValue.Node;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

/**
 * Created by kiven on 2018/3/28.
 */
public class SanWenSpider extends AbstractSpider implements Crower {

    @Override
    public void spider(String url) {
        webDriver.get(url);
        WebElement webElement = webDriver.findElement(By.className("categorylist"));
        List<WebElement> webElementList = webElement.findElements(By.tagName("li"));
        for (WebElement we : webElementList) {
            FindValue findValue = FindValue.getFindValue(we);
            Map<Integer, String> map = findValue.findMux(new Node(FindValue.NodeType.ByTagName, "h3")
                    .addNext(new Node(FindValue.NodeType.ByTagName, "a", 1)
                            .addNext(new Node(FindValue.NodeType.Attribute, "href"))
                            .addNext(new Node(FindValue.NodeType.Text, null)))
                    .addNext(new Node(FindValue.NodeType.ByTagName, "a", 2)
                            .addNext(new Node(FindValue.NodeType.Attribute, "href"))
                            .addNext(new Node(FindValue.NodeType.Text, null)))
            );
            System.out.println(map);
            break;
        }

    }

    public static void main(String[] args) {
        String url = "https://www.sanwen.net/sanwen/?p=1";
        SanWenSpider sanWenSpider = new SanWenSpider();
        sanWenSpider.spider(url);
    }
}
