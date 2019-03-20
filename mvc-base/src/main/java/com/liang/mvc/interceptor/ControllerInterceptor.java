package com.liang.mvc.interceptor;

import com.alibaba.fastjson.JSON;
import com.liang.common.exception.SignException;
import com.liang.common.http.SignUtils;
import com.liang.common.util.Encodes;
import com.liang.common.util.PropertiesManager;
import com.liang.mvc.annotation.Login;
import com.liang.mvc.annotation.PcLogin;
import com.liang.mvc.annotation.Sign;
import com.liang.mvc.commons.ResponseUtils;
import com.liang.mvc.filter.LoginUtils;
import com.liang.mvc.filter.UserInfo;
import com.liang.mvc.monitor.ControllerMonitor;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by liangzhiyan on 2017/3/7.
 */
public class ControllerInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private PropertiesManager propertiesManager;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object object) throws Exception {
        boolean result = true;
        if (object instanceof HandlerMethod) {
            ControllerMonitor.get().setClassAndMethod((HandlerMethod) object)
                    .setBeginTime(System.currentTimeMillis()).setInParam(httpServletRequest);
            result = validSign(httpServletRequest, httpServletResponse, (HandlerMethod) object)
                    && validLogin(httpServletRequest, httpServletResponse, (HandlerMethod) object);
        }
        return result;
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        super.afterCompletion(httpServletRequest, httpServletResponse, o, e);
    }

    private boolean validSign(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, HandlerMethod handlerMethod) {
        Sign sign = handlerMethod.getMethodAnnotation(Sign.class);
        if (sign != null) {
            String signKey = null;
            if (StringUtils.isNotBlank(sign.signKey())) {
                signKey = sign.signKey();
            } else {
                signKey = propertiesManager.getString(sign.signKeyPropertyName());
            }
            if (!SignUtils.isValidSign(httpServletRequest, httpServletResponse, signKey, null)) {
                throw SignException.throwException("签名错误");
            }
        }
        return true;
    }

    private boolean validLogin(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) throws IOException {
        Login login = handlerMethod.getMethodAnnotation(Login.class);
        PcLogin pcLogin = handlerMethod.getMethodAnnotation(PcLogin.class);
        if (pcLogin != null || login != null) {
            //验证token
            String token = LoginUtils.getToken(request);
            if (StringUtils.isBlank(token) || LoginUtils.getUser(token) == null) {//重定向到登陆页
                if (pcLogin != null) {
                    String loginUrl = propertiesManager.getString("account.login.url", "http://127.0.0.1:9091/v1/pc-login/login-page");
                    String requestUri = request.getRequestURL().toString();
                    String queryString = getQueryString(request);
                    if (StringUtils.isNotBlank(queryString)) {
                        requestUri = requestUri + "?" + queryString;
                    }
                    loginUrl += "?callBackUrl=" + Encodes.urlEncode(requestUri);
                    response.sendRedirect(loginUrl);
                    return false;
                } else {
                    ResponseUtils.writeToResponse(response, ResponseUtils.NotLoginResponse());
                    return false;
                }

            }
            UserInfo userInfo = LoginUtils.getUser(token);
            ControllerMonitor.get().setUser(userInfo);
            LoginUtils.setToken(response, token);
        }
        return true;
    }

    private String getQueryString(HttpServletRequest request) {
        Map<String, String[]> params = request.getParameterMap();
        String queryString = "";
        for (String key : params.keySet()) {
            String[] values = params.get(key);
            for (int i = 0; i < values.length; i++) {
                String value = values[i];
                queryString += key + "=" + value + "&";
            }
        }
        // 去掉最后一个空格
        if (StringUtils.length(queryString) >= 1) {
            queryString = queryString.substring(0, queryString.length() - 1);
        }
        System.out.println("queryString: "+queryString);
        return queryString;
    }
}
