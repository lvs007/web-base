package liang.flow.core;

import liang.flow.config.ControlParameter;
import liang.flow.config.ControllerType;

/**
 * Created by liangzhiyan on 2017/4/12.
 */
public class CountyFlowController extends AbstractController implements BaseFlowController {
    @Override
    public boolean flowControl(ControlParameter controlParameter) {
        return flowControl(controlParameter.getCurrentUri(), controlParameter.getCounty(), ControllerType.COUNTY);
    }

    @Override
    public boolean forbitddenControl(ControlParameter controlParameter) {
        return forbitddenControl(controlParameter.getCurrentUri(), controlParameter.getCounty(), ControllerType.COUNTY);
    }

    @Override
    public ControllerType getControllerType() {
        return ControllerType.COUNTY;
    }

    @Override
    public void resetControllerBeginTime(ControlParameter controlParameter) {
        resetControllerBeginTime(controlParameter.getCurrentUri(), controlParameter.getCounty(), ControllerType.COUNTY);
    }
}
