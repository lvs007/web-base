package com.liang.mvc.filter;

import com.alibaba.fastjson.JSONObject;
import com.liang.cache.impl.DefaultCommonLocalCache;
import com.liang.common.http.api.ApiResponse;
import com.liang.common.http.api.BaseApi;
import com.liang.common.http.api.exception.ApiException;
import com.liang.common.http.api.exception.HttpException;
import com.liang.common.http.api.exception.InternalException;
import com.liang.common.util.PropertiesManager;
import com.liang.mvc.commons.SpringContextHolder;
import com.liang.mvc.constants.MvcConstants;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by liangzhiyan on 2017/3/20.
 */
public class LoginUtils {

  private static final Logger LOG = LoggerFactory.getLogger(LoginUtils.class);

  private static final PropertiesManager propertiesManager = SpringContextHolder
      .getBean("propertiesManager");

  private static final LoginHttp loginHttp = new LoginHttp();

  private static final DefaultCommonLocalCache userInfoCache = new DefaultCommonLocalCache(
      30 * 60 * 1000);

  public static String getToken(HttpServletRequest request) {
    HttpSession session = request.getSession(false);
    String token = session == null ? null : (String) session.getAttribute(MvcConstants.TOKEN);
    if (StringUtils.isBlank(token)) {
      token = request.getParameter(MvcConstants.TOKEN);
    }
    if (StringUtils.isBlank(token)) {
      token = request.getHeader(MvcConstants.TOKEN);
    }
    if (StringUtils.isBlank(token)) {
      Cookie[] cookies = request.getCookies();
      if (cookies != null) {
        for (Cookie cookie : cookies) {
          if (StringUtils.equals(cookie.getName(), MvcConstants.TOKEN)) {
            token = cookie.getValue();
            break;
          }
        }
      }
    }
    if (StringUtils.isBlank(token)) {
      String cookie = request.getHeader(MvcConstants.COOKIE);
      String[] cookies = StringUtils.split(cookie, MvcConstants.SEMICOLON);
      if (cookies != null) {
        for (String str : cookies) {
          str = StringUtils.trim(str);
          if (StringUtils.startsWith(str, MvcConstants.TOKEN)) {
            token = str.substring(str.indexOf(MvcConstants.EQUAL) + 1);
            break;
          }
        }
      }
    }
    return token;
  }

  public static UserInfo getUser(String token) {
    if (StringUtils.isBlank(token) || "null".equalsIgnoreCase(token)) {
      return null;
    }
    try {
      UserInfo userInfo = (UserInfo) userInfoCache.get(token);
      if (userInfo == null) {
        userInfo = loginHttp.getUser(token);
        if (userInfo != null) {
          userInfoCache.set(token, userInfo);
        }
      }
      return userInfo;
    } catch (Exception e) {
      LOG.error("获取用户信息出错,token:{}", token, e);
    }
    return null;
  }

  public static UserInfo getCurrentUser(HttpServletRequest request) {
    String token = getToken(request);
    return getUser(token);
  }

  public static void setToken(HttpServletResponse response, String token) {
    Cookie cookie = new Cookie(MvcConstants.TOKEN, token);
    cookie.setPath("/");
//        cookie.setDomain("");
    response.addCookie(cookie);
    response.addHeader(MvcConstants.TOKEN, token);
  }

  private static void setCookie(HttpServletRequest request, String name, String value) {
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if (StringUtils.equals(cookie.getName(), name)) {
          cookie.setValue(value);
        }
      }
    }
  }

  public static boolean logOut(String token) {
    try {
      Cookie cookie = new Cookie("nb_token", "");
      cookie.setPath("/");
      SpringContextHolder.getResponse().addCookie(cookie);
      setCookie(SpringContextHolder.getRequest(), MvcConstants.TOKEN, "");
      return loginHttp.logOut(token) ? userInfoCache.remove(token) : false;
    } catch (Exception e) {
      LOG.error("登出出错", e);
    }
    return false;
  }

  private static class LoginHttp extends BaseApi {

    public UserInfo getUser(String token) throws InternalException, ApiException, HttpException {
      LOG.info("请求登陆系统通过token获取用户信息，token：{}", token);
      ApiResponse response = httpGet(
          propertiesManager.getString("account.server.get.user.url", "/v1/login/get-user-info")
              + "?token=" + token);
      JSONObject jsonObject = response.getJsonObject().getJSONObject("data");
      if (!jsonObject.getBooleanValue("success")) {
        return null;
      }
      return jsonObject.getObject("data", UserInfo.class);
    }

    public boolean logOut(String token) throws InternalException, ApiException, HttpException {
      ApiResponse response = httpGet(
          propertiesManager.getString("account.server.logout.url", "/v1/login/log-out")
              + "?nb_token=" + token);
      JSONObject jsonObject = response.getJsonObject().getJSONObject("data");
      return jsonObject.getBooleanValue("success");
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
