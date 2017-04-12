package liang.flow.core;

import liang.flow.config.ControllerObject;
import liang.flow.config.ControllerType;
import liang.flow.config.FlowConfig;
import liang.flow.config.ForbiddenConfig;
import org.apache.commons.lang3.RandomUtils;

/**
 * Created by liangzhiyan on 2017/4/5.
 */
public abstract class AbstractController implements BaseFlowController {

    public boolean flowControl(String uri, String value, ControllerType controllerType) {
        ControllerObject controllerObject = FlowConfig.getControllerObject(controllerType, uri, value);
        if (controllerObject == null || !controllerObject.isOpen()) {
            return false;
        } else if (controllerObject.isForeverController()) {
            return true;
        } else {
            return controlRate(controllerObject);
        }
    }

    public boolean forbiddenControl(String uri, String value, ControllerType controllerType) {
        ControllerObject controllerObject = ForbiddenConfig.getControllerObject(controllerType, uri, value);
        if (controllerObject == null || !controllerObject.isOpen()) {
            return false;
        } else if (controllerObject.isForeverController()) {
            setControllerBeginTime(controllerObject);
            return true;
        } else {
            setControllerBeginTime(controllerObject);
            if (controllerObject.getControllerBeginTime() + controllerObject.getControllerTime() >= System.currentTimeMillis()) {
                return true;
            } else {//解除forbitdden
                resetController(controllerObject);
                return false;
            }
        }
    }

    protected void setControllerBeginTime(ControllerObject controllerObject) {
        if (controllerObject.getControllerBeginTime() <= 0) {
            controllerObject.setControllerBeginTime(System.currentTimeMillis());
        }
    }

    protected boolean controlRate(ControllerObject controllerObject) {
        return RandomUtils.nextInt(0, 100) <= controllerObject.getRate();
    }

    public void resetController(ControllerObject controllerObject) {
        if (controllerObject != null) {
            controllerObject.setOpen(false);
            controllerObject.setControllerBeginTime(0);
        }
    }

    public void resetControllerBeginTime(String uri, String value, ControllerType controllerType) {
        ControllerObject controllerObject = ForbiddenConfig.getControllerObject(controllerType, uri, value);
        if (controllerObject != null) {
            controllerObject.setControllerBeginTime(0);
        }
    }
}
