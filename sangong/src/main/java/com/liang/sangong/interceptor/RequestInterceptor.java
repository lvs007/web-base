package com.liang.sangong.interceptor;

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

  @Override
  public boolean preHandle(HttpServletRequest httpServletRequest,
      HttpServletResponse httpServletResponse, Object object) {
    boolean result = true;
    if (object instanceof HandlerMethod) {
      if (SystemState.maintain) {
        ResponseUtils.writeToResponse(httpServletResponse,
            ResponseUtils.ErrorResponse(50001, "系统维护中"));
        result = false;
      }
      String methodName = ((HandlerMethod) object).getMethod().getName();
      if (StringUtils.equals(methodName, "comeInRoom") || StringUtils.equals(methodName, "createRoom")) {
        if (roomPool.getUserNumber() >= SystemState.userLimit) {
          ResponseUtils.writeToResponse(httpServletResponse,
              ResponseUtils.ErrorResponse(50002, "当前玩家用户过多，请稍后重试"));
          result = false;
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
