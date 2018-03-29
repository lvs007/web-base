package liang.spider.findelement;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

/**
 * Created by liangzhiyan on 2018/3/20.
 */
public class FindElement {

    private static final Logger LOG = LoggerFactory.getLogger(FindElement.class);

    public static WebElement findByTagName(WebElement webElement, String tagName) {
        try {
            return webElement.findElement(By.tagName(tagName));
        } catch (Exception e) {
            LOG.error("", e);
        }
        return null;
    }

    public static WebElement findById(WebElement webElement, String id) {
        try {
            return webElement.findElement(By.id(id));
        } catch (Exception e) {
            LOG.error("", e);
        }
        return null;
    }

    public static WebElement findByLinkText(WebElement webElement, String linkText) {
        try {
            return webElement.findElement(By.linkText(linkText));
        } catch (Exception e) {
            LOG.error("", e);
        }
        return null;
    }

    public static WebElement findByPartialLinkText(WebElement webElement, String partialLinkText) {
        try {
            return webElement.findElement(By.partialLinkText(partialLinkText));
        } catch (Exception e) {
            LOG.error("", e);
        }
        return null;
    }

    public static WebElement findByName(WebElement webElement, String name) {
        try {
            return webElement.findElement(By.name(name));
        } catch (Exception e) {
            LOG.error("", e);
        }
        return null;
    }

    public static WebElement findByXpath(WebElement webElement, String xpath) {
        try {
            return webElement.findElement(By.xpath(xpath));
        } catch (Exception e) {
            LOG.error("", e);
        }
        return null;
    }

    public static WebElement findByClassName(WebElement webElement, String className) {
        try {
            return webElement.findElement(By.className(className));
        } catch (Exception e) {
            LOG.error("", e);
        }
        return null;
    }

    public static WebElement findByCssSelector(WebElement webElement, String cssSelector) {
        try {
            return webElement.findElement(By.cssSelector(cssSelector));
        } catch (Exception e) {
            LOG.error("", e);
        }
        return null;
    }

    public static WebElement findByNode(WebElement webElement, FindValue.Node node) {
        if (webElement == null) {
            return null;
        }
        switch (node.getNodeType()) {
            case ByClassName:
                return findByClassName(webElement, node.getValue());
            case ByCssSelector:
                return findByCssSelector(webElement, node.getValue());
            case ById:
                return findById(webElement, node.getValue());
            case ByLinkText:
                return findByLinkText(webElement, node.getValue());
            case ByName:
                return findByName(webElement, node.getValue());
            case ByPartialLinkText:
                return findByPartialLinkText(webElement, node.getValue());
            case ByTagName:
                return findByTagName(webElement, node.getValue());
            case ByXPath:
                return findByXpath(webElement, node.getValue());
            default:
                return null;
        }
    }

    public static List<WebElement> findByTagNames(WebElement webElement, String tagName) {
        try {
            return webElement.findElements(By.tagName(tagName));
        } catch (Exception e) {
            LOG.error("", e);
        }
        return Collections.emptyList();
    }

    public static List<WebElement> findByIds(WebElement webElement, String id) {
        try {
            return webElement.findElements(By.id(id));
        } catch (Exception e) {
            LOG.error("", e);
        }
        return Collections.emptyList();
    }

    public static List<WebElement> findByLinkTexts(WebElement webElement, String linkText) {
        try {
            return webElement.findElements(By.linkText(linkText));
        } catch (Exception e) {
            LOG.error("", e);
        }
        return Collections.emptyList();
    }

    public static List<WebElement> findByPartialLinkTexts(WebElement webElement, String partialLinkText) {
        try {
            return webElement.findElements(By.partialLinkText(partialLinkText));
        } catch (Exception e) {
            LOG.error("", e);
        }
        return Collections.emptyList();
    }

    public static List<WebElement> findByNames(WebElement webElement, String name) {
        try {
            return webElement.findElements(By.name(name));
        } catch (Exception e) {
            LOG.error("", e);
        }
        return Collections.emptyList();
    }

    public static List<WebElement> findByXpaths(WebElement webElement, String xpath) {
        try {
            return webElement.findElements(By.xpath(xpath));
        } catch (Exception e) {
            LOG.error("", e);
        }
        return Collections.emptyList();
    }

    public static List<WebElement> findByClassNames(WebElement webElement, String className) {
        try {
            return webElement.findElements(By.className(className));
        } catch (Exception e) {
            LOG.error("", e);
        }
        return Collections.emptyList();
    }

    public static List<WebElement> findByCssSelectors(WebElement webElement, String cssSelector) {
        try {
            return webElement.findElements(By.cssSelector(cssSelector));
        } catch (Exception e) {
            LOG.error("", e);
        }
        return Collections.emptyList();
    }

}
