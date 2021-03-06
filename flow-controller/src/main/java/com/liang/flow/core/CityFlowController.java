package com.liang.flow.core;

import com.liang.flow.config.ControlParameter;
import com.liang.flow.config.ControllerType;

/**
 * Created by liangzhiyan on 2017/4/12.
 */
public class CityFlowController extends AbstractController implements BaseFlowController {
    @Override
    public boolean flowControl(ControlParameter controlParameter) {
        return flowControl(controlParameter.getCurrentUri(), controlParameter.getCity(), ControllerType.CITY);
    }

    @Override
    public boolean forbiddenControl(ControlParameter controlParameter) {
        return forbiddenControl(controlParameter.getCurrentUri(), controlParameter.getCity(), ControllerType.CITY);
    }

    @Override
    public ControllerType getControllerType() {
        return ControllerType.CITY;
    }

    @Override
    public void resetControllerBeginTime(ControlParameter controlParameter) {
        resetControllerBeginTime(controlParameter.getCurrentUri(), controlParameter.getCity(), ControllerType.CITY);
    }
}
