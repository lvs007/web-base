package liang.flow.core;

import liang.flow.config.ControlParameter;
import liang.flow.config.ControllerType;

/**
 * Created by liangzhiyan on 2017/4/12.
 */
public class CountryFlowController extends AbstractController implements BaseFlowController {
    @Override
    public boolean flowControl(ControlParameter controlParameter) {
        return flowControl(controlParameter.getCurrentUri(), controlParameter.getCountry(), ControllerType.COUNTRY);
    }

    @Override
    public boolean forbitddenControl(ControlParameter controlParameter) {
        return forbitddenControl(controlParameter.getCurrentUri(), controlParameter.getCountry(), ControllerType.COUNTRY);
    }

    @Override
    public ControllerType getControllerType() {
        return ControllerType.COUNTRY;
    }

    @Override
    public void resetControllerBeginTime(ControlParameter controlParameter) {
        resetControllerBeginTime(controlParameter.getCurrentUri(), controlParameter.getCountry(), ControllerType.COUNTRY);
    }
}
