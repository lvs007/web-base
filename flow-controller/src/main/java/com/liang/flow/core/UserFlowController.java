package com.liang.flow.core;

import com.liang.flow.config.ControlParameter;
import com.liang.flow.config.ControllerType;

/**
 * Created by liangzhiyan on 2017/4/12.
 */
public class UserFlowController extends AbstractController implements BaseFlowController {
    @Override
    public boolean flowControl(ControlParameter controlParameter) {
        return flowControl(controlParameter.getCurrentUri(), controlParameter.getUser(), ControllerType.USER);
    }

    @Override
    public boolean forbiddenControl(ControlParameter controlParameter) {
        return forbiddenControl(controlParameter.getCurrentUri(), controlParameter.getUser(), ControllerType.USER);
    }

    @Override
    public ControllerType getControllerType() {
        return ControllerType.USER;
    }

    @Override
    public void resetControllerBeginTime(ControlParameter controlParameter) {
        resetControllerBeginTime(controlParameter.getCurrentUri(), controlParameter.getUser(), ControllerType.USER);
    }
}
