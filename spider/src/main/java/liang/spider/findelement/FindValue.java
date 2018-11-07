package liang.spider.findelement;

import liang.common.util.ThreadUtils;
import org.apache.commons.collections.CollectionUtils;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static liang.spider.findelement.FindElement.*;

/**
 * Created by liangzhiyan on 2018/3/20.
 */
public class FindValue {

    private WebElement webElement;

    private List<Node> nodeList = new ArrayList<>();

    public FindValue(WebElement webElement) {
        this.webElement = webElement;
    }

    public static FindValue getFindValue(WebElement webElement) {
        return new FindValue(webElement);
    }

    /**
     * 通过构造节点链，获取对应的值，这个最后需要调用build方法
     *
     * @param node
     * @return
     */
    public FindValue find(Node node) {
        nodeList.add(node);
        return this;
    }

    /**
     * 通过构造节点树来获取指定所有的变量数据
     *
     * @param node
     * @return 返回的map格式是顺序的，key对应第几个，value对应值
     */
    public Map<Integer, String> findMux(Node node) {
        Map<Integer, String> resultMap = new LinkedHashMap<>();
        search(node, webElement, resultMap, new AtomicInteger(1));
        return resultMap;
    }

    private void search(Node node, WebElement webElement, Map<Integer, String> resultMap, AtomicInteger count) {
        WebElement now = webElement;
        if (node != null && now != null) {
            if (CollectionUtils.isNotEmpty(node.getNextList())) {//说明当前节点不是获取值
                now = FindElement.findByNode(webElement, node);
                if (now != null) {
                    for (Node n : node.getNextList()) {
                        search(n, now, resultMap, count);
                    }
                } else {//todo,表示这个节点不存在对应的元素，这时候表明获取不到对应的值，直接设置为null
                    resultMap.put(count.getAndAdd(1), null);
                }
            } else {//表示当前节点是获取值
                String value = null;
                switch (node.getNodeType()) {
                    case Attribute:
                        value = webElement == null ? null : webElement.getAttribute(node.getValue());
                        break;
                    case CssValue:
                        value = webElement == null ? null : webElement.getCssValue(node.getValue());
                        break;
                    case TagName:
                        value = webElement == null ? null : webElement.getTagName();
                        break;
                    case Text:
                        value = webElement == null ? null : webElement.getText();
                        break;
                    default:
                        break;
                }
                resultMap.put(count.getAndAdd(1), value);
            }
        }
    }


    public String build() {
        for (Node node : nodeList) {
            switch (node.getNodeType()) {
                case Attribute:
                    return webElement == null ? null : webElement.getAttribute(node.getValue());
                case CssValue:
                    return webElement == null ? null : webElement.getCssValue(node.getValue());
                case TagName:
                    return webElement == null ? null : webElement.getTagName();
                case Text:
                    return webElement == null ? null : webElement.getText();
                case ByClassName:
                    if (webElement == null) {
                        return null;
                    }
                    webElement = findByClassName(webElement, node.getValue());
                    break;
                case ByCssSelector:
                    if (webElement == null) {
                        return null;
                    }
                    webElement = findByCssSelector(webElement, node.getValue());
                    break;
                case ById:
                    if (webElement == null) {
                        return null;
                    }
                    webElement = findById(webElement, node.getValue());
                    break;
                case ByLinkText:
                    if (webElement == null) {
                        return null;
                    }
                    webElement = findByLinkText(webElement, node.getValue());
                    break;
                case ByName:
                    if (webElement == null) {
                        return null;
                    }
                    webElement = findByName(webElement, node.getValue());
                    break;
                case ByPartialLinkText:
                    if (webElement == null) {
                        return null;
                    }
                    webElement = findByPartialLinkText(webElement, node.getValue());
                    break;
                case ByTagName:
                    if (webElement == null) {
                        return null;
                    }
                    webElement = findByTagName(webElement, node.getValue());
                    break;
                case ByXPath:
                    if (webElement == null) {
                        return null;
                    }
                    webElement = findByXpath(webElement, node.getValue());
                    break;
                default:
                    break;
            }
        }
        return null;
    }

    public static class Node {
        private NodeType nodeType;
        private String value;
        private int order;

        private List<Node> nextList;

        public Node(NodeType nodeType, String value) {
            this.nodeType = nodeType;
            this.value = value;
        }

        public Node(NodeType nodeType, String value, int order) {
            this.nodeType = nodeType;
            this.value = value;
            this.order = order;
        }

        public NodeType getNodeType() {
            return nodeType;
        }

        public Node setNodeType(NodeType nodeType) {
            this.nodeType = nodeType;
            return this;
        }

        public String getValue() {
            return value;
        }

        public Node setValue(String value) {
            this.value = value;
            return this;
        }

        public int getOrder() {
            return order;
        }

        public void setOrder(int order) {
            this.order = order;
        }

        public List<Node> getNextList() {
            return nextList;
        }

        public Node setNextList(List<Node> nextList) {
            this.nextList = nextList;
            return this;
        }

        public Node addNext(Node next) {
            if (CollectionUtils.isEmpty(nextList)) {
                nextList = new ArrayList<>();
            }
            nextList.add(next);
            return this;
        }
    }

    public static enum NodeType {
        ById, ByLinkText, ByPartialLinkText, ByName, ByTagName, ByXPath, ByClassName, ByCssSelector, Text, Attribute, CssValue, TagName;
    }
}
