package liang.common.http;

import liang.common.util.MiscUtils;
import liang.common.util.UrlBuilder;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 签名，加密和解密。
 */
public class RiddleWrapper {

    public static final RiddleWrapper INSTANCE = new RiddleWrapper();

    private RiddleWrapper() {
    }

    /**
     * 签名一个请求的签名是否合法，根据如下两个步骤
     * 1，如果sign有值，并且合法
     * 2，如果sign无值，并且resign的值合法
     */
    public boolean isValidSign(HttpServletRequest request, HttpServletResponse response,
                               String[] signKey, String resignValue, String originUrlHeaderKey) {
        String sign = request.getParameter("sign");
        String resign = request.getParameter("resign");
        String fullUrl = StringUtils.isBlank(originUrlHeaderKey) ? null : request.getHeader(originUrlHeaderKey);
        if (StringUtils.isBlank(fullUrl)) {
            fullUrl = MiscUtils.getFullUrl(request);
        }

        if (StringUtils.isNotBlank(sign) && isValidSign(fullUrl, signKey)) {
            return true;
        }

        if (StringUtils.isBlank(sign) && StringUtils.isNotBlank(resign) && StringUtils.equals(resign, resignValue)) {
            return true;
        }

        return false;
    }

    /**
     * 检查签名是否合法
     */
    public boolean isValidSign(String url, String... keys) {
        UrlBuilder ub = new UrlBuilder(url);
        String sign1 = ub.getParam("sign");
        Riddle riddle = Riddle.ONE;
        String key = null;
        if (StringUtils.length(sign1) == 34) {
            String mod = StringUtils.substring(sign1, 32, 34);// 取后两位计算签名用的key
            sign1 = StringUtils.substring(sign1, 0, 32);// 前32位才是真正的签名结果
            int index = Integer.parseInt(mod, 16) % 19;
            if (index < keys.length) {
                key = keys[index];
            } else {
                throw new IllegalArgumentException("签名key错误");
            }

        } else {
            if (keys.length > 0) {
                key = keys[0];
            } else {
                throw new IllegalArgumentException("未找到签名key");
            }
        }
        String sign2 = riddle.sign(ub.getSignPart(), key);
        return riddle.signEquals(sign1, sign2);
    }

    String signUrl(String url, String key) {
        if (url == null || key == null) {
            return null;
        }
        UrlBuilder ub = new UrlBuilder(url);
        return signUrl(ub, key, Riddle.from(ub.getParam("sign")));
    }

    String signUrl(UrlBuilder ub, String key, Riddle riddle) {
        if (ub == null || key == null) {
            return null;
        }
        String sign = riddle.sign(ub.getSignPart(), key);
        ub.removeParam("sign");
        ub.setQuery("sign", sign);
        return ub.getUriWithRawQuery();
    }
}
