package liang.spider;

import liang.common.http.HttpClient;
import liang.spider.findelement.FindValue;
import liang.spider.findelement.FindValue.Node;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static liang.spider.findelement.FindValue.NodeType.*;

/**
 * Created by liangzhiyan on 2018/3/7.
 */
public class Spider {

    private static final Logger LOG = LoggerFactory.getLogger(Spider.class);

    private HttpClient httpClient = HttpClient.getDefault();

    private WebDriver webDriver;

    private void spider(String url) {
        try {
//            System.setProperty("webdriver.chrome.driver", "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome");
            System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver");
//            String response = httpClient.httpGet(url);
//            System.out.println(response);
            webDriver = new ChromeDriver();
            webDriver.get(url);
            setCache(webDriver);
            webDriver.get(url);
//            webDriver.findElement(By.id("u1")).findElement(By.className("lb")).click();
            System.out.println(webDriver.getPageSource());
//            webDriver.findElement(By.id("")).sendKeys();
            WebElement webElement = webDriver.findElement(By.className("s_tab"));
            List<WebElement> webElementList = webElement.findElements(By.tagName("a"));
            List<String> urlList = new ArrayList<>();
            for (WebElement we : webElementList) {
                urlList.add(we.getAttribute("href"));
            }
            webElement = webDriver.findElement(By.className("s-news-list-wrapper"));
            webElementList = webElement.findElements(By.tagName("div"));
            List<Article> articleList = new ArrayList<>();
            for (WebElement we : webElementList) {
                Node node = new Node(ByTagName, "h2")
                        .addNext(new Node(ByClassName, "s-title-yahei")
                                .addNext(new Node(Text, null))
                                .addNext(new Node(Attribute, "href")));
                Map<Integer,String> valueMap = FindValue.getFindValue(we).findMux(node);
                String title = FindValue.getFindValue(we).find(new Node(ByTagName, "h2")).find(new Node(ByClassName, "s-title-yahei")).find(new Node(Text, null)).build();
                String aurl = FindValue.getFindValue(we).find(new Node(ByTagName, "h2")).find(new Node(ByClassName, "s-title-yahei")).find(new Node(Attribute, "href")).build();
                String time = FindValue.getFindValue(we).find(new Node(ByClassName, "from")).find(new Node(ByClassName, "src-time")).find(new Node(Text, null)).build();
                String tag = FindValue.getFindValue(we).find(new Node(ByClassName, "from")).find(new Node(ByClassName, "hot-point")).find(new Node(Text, null)).build();
                String source = FindValue.getFindValue(we).find(new Node(ByClassName, "from")).find(new Node(ByClassName, "src-net")).find(new Node(ByTagName, "a")).find(new Node(Text, null)).build();
                String sourceUrl = FindValue.getFindValue(we).find(new Node(ByClassName, "from")).find(new Node(ByClassName, "src-net")).find(new Node(ByTagName, "a")).find(new Node(Attribute, "href")).build();
                Article article = Article.builder().setTitle(title).setUrl(aurl).setTime(time)
                        .setTag(tag).setSource(source).setSourceUrl(sourceUrl);
                articleList.add(article);
            }
            System.out.println(urlList);
            System.out.println(articleList);
//            LOG.info(webDriver.getPageSource());
            webDriver.close();
        } catch (Exception e) {
            LOG.error("", e);
        }
    }

    private static class Article {
        private String title;
        private String content;
        private String url;
        private String time;
        private String tag;
        private String source;
        private String sourceUrl;

        public String getTitle() {
            return title;
        }

        public Article setTitle(String title) {
            this.title = title;
            return this;
        }

        public String getContent() {
            return content;
        }

        public Article setContent(String content) {
            this.content = content;
            return this;
        }

        public String getUrl() {
            return url;
        }

        public Article setUrl(String url) {
            this.url = url;
            return this;
        }

        public String getTime() {
            return time;
        }

        public Article setTime(String time) {
            this.time = time;
            return this;
        }

        public String getTag() {
            return tag;
        }

        public Article setTag(String tag) {
            this.tag = tag;
            return this;
        }

        public String getSource() {
            return source;
        }

        public Article setSource(String source) {
            this.source = source;
            return this;
        }

        public String getSourceUrl() {
            return sourceUrl;
        }

        public Article setSourceUrl(String sourceUrl) {
            this.sourceUrl = sourceUrl;
            return this;
        }

        public static Article builder() {
            return new Article();
        }

        @Override
        public String toString() {
            return "Article{" +
                    "title='" + title + '\'' +
                    ", content='" + content + '\'' +
                    ", url='" + url + '\'' +
                    ", time='" + time + '\'' +
                    ", tag='" + tag + '\'' +
                    ", source='" + source + '\'' +
                    ", sourceUrl='" + sourceUrl + '\'' +
                    '}';
        }
    }

    private static void setCache(WebDriver webDriver) {
        WebDriver.Options options = webDriver.manage();
        options.addCookie(new Cookie("BAEID", "A84CD1546D54FE71019982E98959ADE0", "sp0.baidu.com", "/", null));
        options.addCookie(new Cookie("BAIDUCUID", "++"));
        options.addCookie(new Cookie("BAIDUID", "47D736B2AD54823E2CEF102473071981:FG=1"));
        options.addCookie(new Cookie("BDRCVFR[feWj1Vr5u3D]", "I67x6TjHwwYf0"));
        options.addCookie(new Cookie("BDSFRCVID", "TX_sJeC62GNurJRABcp7MhgRFmWQoXvTH6aoK4ZkY9WEpYsMlPmPEG0Pqx8g0Ku-awc4ogKK0mOTHUcP"));
        options.addCookie(new Cookie("BDUSS", "p6MXE3NmwzWjhLMmVVdUJVQmR0VzBGfnJmaXZPbkxSbnl0ODk5WVhXMFo5ZFphQVFBQUFBJCQAAAAAAAAAAAEAAAB-N1APenlsdnMwMDcAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABlor1oZaK9aam"));
        options.addCookie(new Cookie("BD_CK_SAM", "1"));
        options.addCookie(new Cookie("BD_HOME", "1"));
        options.addCookie(new Cookie("BD_UPN", "123253"));
        options.addCookie(new Cookie("BIDUPSID", "6FBECF6B39CFD91F9364D99EC2CAD6DE"));
        options.addCookie(new Cookie("FP_UID", "3fae4c9978a02a75a7b2ed52a401e1be"));
        options.addCookie(new Cookie("H_BDCLCKID_SF", "JJutoC82JCvMqRjnMPoKq4u_KxrXb-uXKKOLVhIXbPOkeq8CDRoqyJFghR6PK5cMBCbM_nn_LxAhSUj2y5jHyJJDMb6Raxjk36vlBJFXW-5psIJM5-DWbT8U5ecrQf48aKviaKJEBMb1SJOMe6thj6jLDHtsaPRJ5CtX0RROaJ0_Hn7zenuWMntpbt-qJj3fyInz_fcG5-058JnTj-7q-4us5M6nBT5KW6PfKp5s2f76Ep6GDjoqBP0kQN3T-pLO5bRiL66SLt8aDn3oyT3VXp0n3fbTqjFqfnFD_IvDanTJj--kKJo_-JjH-UnLqhb3W57Z0l8KtfcYMx5sjp6lbnk3Xf7MtPv-amjxBnrmWIQHDnQSQUTi5J-90Mc056cOJtj4KKJx-4PWeIJo5DFWWq-ghUJiB5OLBan7-ROxfDI-bKLle5Rshn08h2TD-46K-to2WbCQXhbMqpcNLTDK0xQ3jM6ZXqQMaGnuBn022n5IfIjYDpO1j4_eQlrvXx3LQec-LKTPKKJ1sh5jDh3a3jksD-Rte6vkQ2Qy0hvctn6cShna-p00ejbbeH0tt6n2b6b03RA8--5_jJrTq4bohjP9QeceBtQmJJrm-t-KBhcAqhc9yUcsbpD3bf54-U-tQg-q3R7ctJ5njn3Dyt5NMfu-5R500x-jLT6OVn0MWhDVeh64yPnJyUnQhtnnBpQ2LnLtoKIbfI0MbP5kMtn_qttjMfbWetTbHD7yWCv2WqT5OR5Jj6KbK-kqjGozbUcPL2j30Mo22q3B8DDG3MA--fF7QM5M-jQ-KJcg_to52M-Wsq0x0-jle-bQyp_LXnOx-DOMahkMal7xOM5n05C-jjJ-eaDfqbbfb-_DoIthtTrjDnCr3h7rKUI8LNDH0M6t-eQN5loJ-I3Yjb3-Q4cR0t4R5RO7ttoyWbnzBMIXQh52otOEhh3V0ML1Db3DhTvMtg3tsRj5KKboepvoD-cc3MkA-n0E5bj2qRFeVILK3H"));
        options.addCookie(new Cookie("H_PS_PSSID", "1425_21111_17001_20930"));
        options.addCookie(new Cookie("MCITY", "-131%3A"));
        options.addCookie(new Cookie("PSINO", "2"));
        options.addCookie(new Cookie("__cfduid", "da8bf3db3eb848484b4186da048345bb51501579687"));
        options.addCookie(new Cookie("cflag", "15%3A3"));
    }

    public static void main(String[] args) {
        Spider spider = new Spider();
        spider.spider("http://www.baidu.com");
    }
}
