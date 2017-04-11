package liang.flow.core;

import liang.flow.config.ControllerObject;
import liang.flow.config.ControllerType;
import liang.flow.config.FlowConfig;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.RandomUtils;

import java.util.Map;

/**
 * Created by liangzhiyan on 2017/4/5.
 */
public abstract class AbstractController {

    public boolean control(String uri, String value, ControllerType controllerType) {
        ControllerObject controllerObject = FlowConfig.getControllerObject(controllerType, uri, value);
        if (controllerObject == null || !controllerObject.isOpen()) {
            return false;
        } else if (controllerObject.isForeverController()) {
//            setControllerBeginTime(controllerObject);
            return true;
        } else {
//            setControllerBeginTime(controllerObject);
//            if (controllerObject.getControllerBeginTime() + controllerObject.getControllerTime() >= System.currentTimeMillis()) {
//            } else {
//                return false;
//            }
            return controlRate(controllerObject);
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

    public void resetControllerBeginTime(ControllerObject controllerObject) {
        controllerObject.setControllerBeginTime(0);
    }
}