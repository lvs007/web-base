package liang.flow.core;

import liang.flow.config.ControlParameter;
import liang.flow.config.ControllerObject;
import liang.flow.config.ControllerType;

/**
 * Created by liangzhiyan on 2017/3/31.
 */
public interface BaseFlowController {

    boolean flowControl(ControlParameter controlParameter);

    boolean forbiddenControl(ControlParameter controlParameter);

    ControllerType getControllerType();

    void resetControllerBeginTime(ControlParameter controlParameter);
}
