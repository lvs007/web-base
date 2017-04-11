package liang.mvc.filter;

import com.alibaba.fastjson.JSONObject;
import liang.common.http.api.ApiResponse;
import liang.common.http.api.BaseApi;
import liang.common.http.api.exception.ApiException;
import liang.common.http.api.exception.HttpException;
import liang.common.http.api.exception.InternalException;
import liang.common.util.PropertiesManager;
import liang.mvc.commons.SpringContextHolder;
import liang.mvc.constants.MvcConstants;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by liangzhiyan on 2017/3/20.
 */
public class LoginUtils {

    private static final Logger LOG = LoggerFactory.getLogger(LoginUtils.class);

    private static final PropertiesManager propertiesManager = SpringContextHolder.getBean("propertiesManager");

    private static final LoginHttp loginHttp = new LoginHttp();

    private static final ThreadLocal<UserInfo> threadLocal = new ThreadLocal<>();

    public static UserInfo getUser(String token) {
        try {
            UserInfo userInfo = threadLocal.get();
            if (userInfo == null) {
                userInfo = loginHttp.getUser(token);
                if (userInfo != null) {
                    threadLocal.set(userInfo);
                }
            }
            return userInfo;
        } catch (Exception e) {
            LOG.error("获取用户信息出错", e);
        }
        return null;
    }

    public static UserInfo getCurrentUser(HttpServletRequest request) {
        String token = request.getParameter(MvcConstants.TOKEN);
        if (StringUtils.isBlank(token)) {
            token = (String) request.getSession().getAttribute(MvcConstants.TOKEN);
            if (StringUtils.isBlank(token)) {
                return null;
            }
        }
        return getUser(token);
    }

    public static void setToken(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie(MvcConstants.TOKEN, token);
        response.addCookie(cookie);
        response.addHeader(MvcConstants.TOKEN, token);
    }

    private static class LoginHttp extends BaseApi {

        public UserInfo getUser(String token) throws InternalException, ApiException, HttpException {
            ApiResponse response = httpGet(propertiesManager.getString("account.server.get.user.url", "/v1/login/get-user-info") + "?token=" + token);
            JSONObject jsonObject = response.getJsonObject().getJSONObject("data");
            if (!jsonObject.getBooleanValue("success")) {
                return null;
            }
            return jsonObject.getObject("data", UserInfo.class);
        }

        @Override
        protected String getApiHost() {
            return propertiesManager.getString("account.server.host", "http://127.0.0.1:9091");
        }

        @Override
        protected String getSignKey() {
            return propertiesManager.getString("account.sign.key", "ddjdfge4");
        }
    }
}
