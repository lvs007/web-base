package com.liang.flow.core;

import com.liang.flow.config.ControlParameter;
import com.liang.flow.config.ControllerType;

/**
 * Created by liangzhiyan on 2017/4/12.
 */
public class AreaFlowController extends AbstractController implements BaseFlowController {
    @Override
    public boolean flowControl(ControlParameter controlParameter) {
        return flowControl(controlParameter.getCurrentUri(), controlParameter.getArea(), ControllerType.AREA);
    }

    @Override
    public boolean forbiddenControl(ControlParameter controlParameter) {
        return forbiddenControl(controlParameter.getCurrentUri(), controlParameter.getArea(), ControllerType.AREA);
    }

    @Override
    public ControllerType getControllerType() {
        return ControllerType.AREA;
    }

    @Override
    public void resetControllerBeginTime(ControlParameter controlParameter) {
        resetControllerBeginTime(controlParameter.getCurrentUri(), controlParameter.getArea(), ControllerType.AREA);
    }
}
