package liang.spider.findelement;

import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

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

    public FindValue find(Node node) {
        nodeList.add(node);
        return this;
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

        public Node(NodeType nodeType, String value) {
            this.nodeType = nodeType;
            this.value = value;
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
    }

    public static enum NodeType {
        ById, ByLinkText, ByPartialLinkText, ByName, ByTagName, ByXPath, ByClassName, ByCssSelector, Text, Attribute, CssValue, TagName;
    }
}
