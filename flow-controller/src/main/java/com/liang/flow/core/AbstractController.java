package com.liang.flow.core;

import com.liang.common.LogUtils;
import com.liang.flow.config.ControllerObject;
import com.liang.flow.config.ControllerType;
import com.liang.flow.config.FlowConfig;
import com.liang.flow.config.ForbiddenConfig;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;

/**
 * Created by liangzhiyan on 2017/4/5.
 */
public abstract class AbstractController implements BaseFlowController {

    private final Logger LOG = LogUtils.getInstance(getClass());

    public boolean flowControl(String uri, String value, ControllerType controllerType) {
        ControllerObject controllerObject = FlowConfig
            .getControllerObject(controllerType, uri, value);
        if (controllerObject == null || !controllerObject.isOpen()) {
            return false;
        } else if (controllerObject.isForeverController()) {
            LOG.info("flowControl开启永久性流控！uri:{},value:{},type:{}", uri, value, controllerType);
            return true;
        } else {
            LOG.info("flowControl开启部分流控！uri:{},value:{},type:{}", uri, value, controllerType);
            return controlRate(controllerObject);
        }
    }

    public boolean forbiddenControl(String uri, String value, ControllerType controllerType) {
        ControllerObject controllerObject = ForbiddenConfig
            .getControllerObject(controllerType, uri, value);
        if (controllerObject == null || !controllerObject.isOpen()) {
            return false;
        } else if (controllerObject.isForeverController()) {
            LOG.info("forbiddenControl开启永久性流控！uri:{},value:{},type:{}", uri, value, controllerType);
            setControllerBeginTime(controllerObject);
            return true;
        } else {
            setControllerBeginTime(controllerObject);
            if (controllerObject.getControllerBeginTime() < System.currentTimeMillis()
                    && controllerObject.getControllerBeginTime() + controllerObject.getControllerTime() >= System.currentTimeMillis()) {
                LOG.info("forbiddenControl开启流控！uri:{},value:{},type:{}", uri, value, controllerType);
                return true;
            } else {//解除forbitdden
//                resetController(controllerObject);
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
