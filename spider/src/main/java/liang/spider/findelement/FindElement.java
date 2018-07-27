package liang.spider.findelement;

import liang.common.util.ThreadUtils;
import org.apache.commons.collections4.CollectionUtils;
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
            LOG.error("tagName:{}", tagName, e);
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
        int tryTimes = 0;
        while (!webElement.isEnabled() && tryTimes < 10) {
            ThreadUtils.sleep(100);
            ++tryTimes;
        }
        switch (node.getNodeType()) {
            case ByClassName:
                if (node.getOrder() > 0) {
                    return selectOne(findByClassNames(webElement, node.getValue()), node);
                }
                return findByClassName(webElement, node.getValue());
            case ByCssSelector:
                if (node.getOrder() > 0) {
                    return selectOne(findByCssSelectors(webElement, node.getValue()), node);
                }
                return findByCssSelector(webElement, node.getValue());
            case ById:
                if (node.getOrder() > 0) {
                    return selectOne(findByIds(webElement, node.getValue()), node);
                }
                return findById(webElement, node.getValue());
            case ByLinkText:
                if (node.getOrder() > 0) {
                    return selectOne(findByLinkTexts(webElement, node.getValue()), node);
                }
                return findByLinkText(webElement, node.getValue());
            case ByName:
                if (node.getOrder() > 0) {
                    return selectOne(findByNames(webElement, node.getValue()), node);
                }
                return findByName(webElement, node.getValue());
            case ByPartialLinkText:
                if (node.getOrder() > 0) {
                    return selectOne(findByPartialLinkTexts(webElement, node.getValue()), node);
                }
                return findByPartialLinkText(webElement, node.getValue());
            case ByTagName:
                if (node.getOrder() > 0) {
                    return selectOne(findByTagNames(webElement, node.getValue()), node);
                }
                return findByTagName(webElement, node.getValue());
            case ByXPath:
                if (node.getOrder() > 0) {
                    return selectOne(findByXpaths(webElement, node.getValue()), node);
                }
                return findByXpath(webElement, node.getValue());
            default:
                return null;
        }
    }

    private static WebElement selectOne(List<WebElement> webElementList, FindValue.Node node) {
        if (CollectionUtils.isEmpty(webElementList) || webElementList.size() < node.getOrder()) {
            return null;
        } else {
            return webElementList.get(node.getOrder() - 1);
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
