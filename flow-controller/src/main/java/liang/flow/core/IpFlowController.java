package liang.flow.core;

import liang.flow.config.ControlParameter;
import liang.flow.config.ControllerType;

/**
 * Created by liangzhiyan on 2017/4/12.
 */
public class IpFlowController extends AbstractController implements BaseFlowController {
    @Override
    public boolean flowControl(ControlParameter controlParameter) {
        return flowControl(controlParameter.getCurrentUri(), controlParameter.getIp(), ControllerType.IP);
    }

    @Override
    public boolean forbiddenControl(ControlParameter controlParameter) {
        return forbiddenControl(controlParameter.getCurrentUri(), controlParameter.getIp(), ControllerType.IP);
    }

    @Override
    public ControllerType getControllerType() {
        return ControllerType.IP;
    }

    @Override
    public void resetControllerBeginTime(ControlParameter controlParameter) {
        resetControllerBeginTime(controlParameter.getCurrentUri(), controlParameter.getIp(), ControllerType.IP);
    }
}
