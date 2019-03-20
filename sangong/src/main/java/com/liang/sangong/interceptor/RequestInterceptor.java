package com.liang.sangong.interceptor;

import com.liang.common.util.Encodes;
import com.liang.common.util.PropertiesManager;
import com.liang.mvc.commons.ResponseUtils;
import com.liang.sangong.common.SystemState;
import com.liang.sangong.core.RoomPool;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * Created by liangzhiyan on 2017/3/7.
 */
public class RequestInterceptor extends HandlerInterceptorAdapter {

  @Autowired
  private PropertiesManager propertiesManager;

  @Autowired
  private RoomPool roomPool;

  private static final String ERROR_URL = "/v1/door/error?message=";

  @Override
  public boolean preHandle(HttpServletRequest httpServletRequest,
      HttpServletResponse httpServletResponse, Object object) throws Exception {
    boolean result = true;
    if (object instanceof HandlerMethod) {
      if (SystemState.maintain) {
//        httpServletRequest.setAttribute("message", "系统维护中");
//        httpServletRequest.getRequestDispatcher("error")
//            .forward(httpServletRequest, httpServletResponse);
        httpServletResponse.sendRedirect(ERROR_URL + Encodes.urlEncode("系统维护中"));
        return false;
      }
      String methodName = ((HandlerMethod) object).getMethod().getName();
      if (StringUtils.equals(methodName, "comeInRoom") || StringUtils
          .equals(methodName, "createRoom")) {
        if (roomPool.getUserNumber() >= SystemState.userLimit) {
          httpServletResponse.sendRedirect(ERROR_URL + Encodes.urlEncode("当前玩家用户过多，请稍后重试"));
          return false;
        }
      }
    }
    return result;
  }

  @Override
  public void afterCompletion(HttpServletRequest httpServletRequest,
      HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
    super.afterCompletion(httpServletRequest, httpServletResponse, o, e);
  }

}
