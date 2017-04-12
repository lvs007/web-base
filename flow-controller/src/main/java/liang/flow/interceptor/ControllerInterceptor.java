package liang.flow.interceptor;

import liang.flow.config.ConfigService;
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
        boolean continues = true;
        if (!ConfigService.isOpenController()) {
            return continues;
        }
        if (ConfigService.isControlAllRequest()) {
            return false;
        }
        continues = ForbitddenController.controlRequest(request);
        if (!continues) {
            return continues;
        }
        return FlowController.controlRequest(request);
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        super.afterCompletion(request, response, handler, ex);
        FlowController.afterCompletion(request);
    }
}
