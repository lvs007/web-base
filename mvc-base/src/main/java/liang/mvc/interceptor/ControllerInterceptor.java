package liang.mvc.interceptor;

import liang.common.exception.SignException;
import liang.common.http.SignUtils;
import liang.common.util.PropertiesManager;
import liang.mvc.annotation.Sign;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by liangzhiyan on 2017/3/7.
 */
public class ControllerInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private PropertiesManager propertiesManager;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        return validSign(httpServletRequest, httpServletResponse, (HandlerMethod) o);
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
            System.out.println("[ControllerInterceptor.validSign] = "+signKey);
            if (!SignUtils.isValidSign(httpServletRequest, httpServletResponse, signKey, null)) {
                throw SignException.throwException("签名错误");
            }
        }
        return true;
    }
}
