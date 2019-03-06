package com.liang.flow.interceptor;

import com.liang.flow.config.ControlParameter;
import com.liang.flow.core.BaseFlowController;
import com.liang.flow.core.ControllerManager;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by liangzhiyan on 2017/4/11.
 */
public class ForbiddenController extends BaseController {

    public static boolean controlRequest(HttpServletRequest request) {
        String uri = request.getRequestURI();
        ControlParameter controlParameter = setControlParameter(request);
        System.out.println(controlParameter);
        for (BaseFlowController forbiddenController : ControllerManager.controllerList) {
            if (forbiddenController.forbiddenControl(controlParameter)) {
                return false;
            }
        }
        return true;
    }
}
