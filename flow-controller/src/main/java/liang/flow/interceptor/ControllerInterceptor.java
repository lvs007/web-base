package liang.flow.interceptor;

import liang.flow.core.ConfigService;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by liangzhiyan on 2017/3/31.
 */
public class ControllerInterceptor extends HandlerInterceptorAdapter {


    @PostConstruct
    private void init() {
        FlowController.init();
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!ConfigService.isOpenController()) {
            return true;
        }
        if (ConfigService.isControlAllRequest()) {
            return false;
        }
        return FlowController.controlRequest(request);
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        super.afterCompletion(request, response, handler, ex);
        FlowController.afterCompletion(request);
    }
}
