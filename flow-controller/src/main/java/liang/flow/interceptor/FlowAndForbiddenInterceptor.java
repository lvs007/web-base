package liang.flow.interceptor;

import liang.dao.jdbc.split.service.CreateTable;
import liang.mvc.commons.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by liangzhiyan on 2017/3/31.
 */
public class FlowAndForbiddenInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private CreateTable createTable;

    @PostConstruct
    private void init() {
        createTable.createForbiddenTable();
//        FlowController.init();
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        boolean continues = true;
//        if (!ConfigService.isOpenController()) {
//            return continues;
//        }
//        if (ConfigService.isControlAllRequest()) {
//            return false;
//        }
        continues = ForbiddenController.controlRequest(request);
        if (!continues) {
            ResponseUtils.writeToResponse(response, ResponseUtils.FlowControlErrorResponse());
            return continues;
        }
        continues = FlowController.controlRequest(request);
        if (!continues) {
            ResponseUtils.writeToResponse(response, ResponseUtils.FlowControlErrorResponse());
        }
        return continues;
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        super.afterCompletion(request, response, handler, ex);
        FlowController.afterCompletion(request);
    }
}
