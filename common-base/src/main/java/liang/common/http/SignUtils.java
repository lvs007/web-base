package liang.common.http;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 签名的工具类
 *
 * @author
 */
public class SignUtils {

    /**
     * 签名一个请求的签名是否合法，根据如下两个步骤
     * 1，如果sign有值，并且合法
     * 2，如果sign无值，并且resign的值合法
     */
    public static boolean isValidSign(HttpServletRequest request, HttpServletResponse response,
                                      String[] signKey, String resignValue) {
        return isValidSign(request, response, signKey, resignValue, null);
    }

    public static boolean isValidSign(HttpServletRequest request, HttpServletResponse response, String signKey,
            String resignValue) {
        return isValidSign(request, response, new String[]{signKey}, resignValue, null);
    }

    public static boolean isValidSign(HttpServletRequest request, HttpServletResponse response,
                                      String[] signKey, String resignValue, String originUrlHeaderKey) {
        return RiddleWrapper.INSTANCE.isValidSign(request, response, signKey, resignValue, originUrlHeaderKey);

    }
    /**
     * 检查签名是否合法
     */
    public static boolean isValidSign(String url, String... key) {
        return RiddleWrapper.INSTANCE.isValidSign(url, key);
    }

    public static String signUrl(String url, String key) {
        return RiddleWrapper.INSTANCE.signUrl(url, key);
    }
}
