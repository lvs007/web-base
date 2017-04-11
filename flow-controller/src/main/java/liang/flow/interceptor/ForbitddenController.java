package liang.flow.interceptor;

import liang.flow.config.ControlParameter;
import liang.flow.core.BaseFlowController;
import liang.flow.core.ControllerManager;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by liangzhiyan on 2017/4/11.
 */
public class ForbitddenController extends BaseController {

    public static boolean controlRequest(HttpServletRequest request) {
        String uri = request.getRequestURI();
        for (BaseFlowController forbitddenController : ControllerManager.controllerList) {
            ControlParameter controlParameter = setControlParameter(request);
            if (forbitddenController.forbitddenControl(controlParameter)) {
                return false;
            }
        }
        return true;
    }
}
