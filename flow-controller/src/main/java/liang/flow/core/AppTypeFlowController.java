package liang.flow.core;

import liang.flow.config.ControlParameter;
import liang.flow.config.ControllerType;

/**
 * Created by liangzhiyan on 2017/4/12.
 */
public class AppTypeFlowController extends AbstractController implements BaseFlowController {
    @Override
    public boolean flowControl(ControlParameter controlParameter) {
        return flowControl(controlParameter.getCurrentUri(), controlParameter.getAppType(), ControllerType.APPTYPE);
    }

    @Override
    public boolean forbiddenControl(ControlParameter controlParameter) {
        return forbiddenControl(controlParameter.getCurrentUri(), controlParameter.getAppType(), ControllerType.APPTYPE);
    }

    @Override
    public ControllerType getControllerType() {
        return ControllerType.APPTYPE;
    }

    @Override
    public void resetControllerBeginTime(ControlParameter controlParameter) {
        resetControllerBeginTime(controlParameter.getCurrentUri(), controlParameter.getAppType(), ControllerType.APPTYPE);
    }
}
