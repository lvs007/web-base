package liang.mvc.interceptor;

import liang.common.exception.SignException;
import liang.common.http.SignUtils;
import liang.common.util.PropertiesManager;
import liang.mvc.annotation.Login;
import liang.mvc.annotation.Sign;
import liang.mvc.commons.SpringContextHolder;
import liang.mvc.constants.MvcConstants;
import liang.mvc.filter.LoginUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created by liangzhiyan on 2017/3/7.
 */
public class ControllerInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private PropertiesManager propertiesManager;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object object) throws Exception {
        boolean result = validSign(httpServletRequest, httpServletResponse, (HandlerMethod) object)
                && validLogin(httpServletRequest, httpServletResponse, (HandlerMethod) object);
        return result;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

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
            System.out.println("[ControllerInterceptor.validSign] = " + signKey);
            if (!SignUtils.isValidSign(httpServletRequest, httpServletResponse, signKey, null)) {
                throw SignException.throwException("签名错误");
            }
        }
        return true;
    }

    private boolean validLogin(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) throws IOException {
        Login login = handlerMethod.getMethodAnnotation(Login.class);
        if (login != null) {
            String token = request.getParameter(MvcConstants.TOKEN);
            if (StringUtils.isBlank(token)) {
                HttpSession session = request.getSession(false);
                token = session == null ? null : (String) session.getAttribute(MvcConstants.TOKEN);
            }
            if (StringUtils.isBlank(token)) {
                token = request.getHeader(MvcConstants.TOKEN);
            }
            //验证token
            if (StringUtils.isBlank(token) || LoginUtils.getUser(token) == null) {//重定向到登陆页
                response.sendRedirect(propertiesManager.getString("account.login.url", "http://localhost:9091/v1/login/login"));
                return false;
            }
            LoginUtils.setToken(response, token);
        }
        return true;
    }
}
