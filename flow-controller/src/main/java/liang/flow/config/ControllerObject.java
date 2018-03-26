package liang.flow.config;

import liang.common.exception.ParameterException;
import liang.flow.config.data.entity.Forbidden;
import liang.flow.constants.CommonConstants;
import org.apache.commons.lang3.StringUtils;

/**
 * flow数据的格式：uri|类型|流控对象（根据类型不同而不同，如：类型是url的是／xxx／action，类型是user的是liangzhiyan，类型是ip的是120.10.1.10等）|rate|isOpen
 * forbidden数据格式：uri|类型|流控对象（根据类型不同而不同，如：类型是url的是／xxx／action，类型是user的是liangzhiyan，类型是ip的是120.10.1.10等）|controllerTime|isOpen|isForeverController
 * <p>
 * 例子：
 * flow：／xxx／getList|ip|120.10.1.12|50|true
 * ／xxx／getList|url|／xxx／getList|50|true
 * forbidden：／xxx／getList|ip|120.10.1.12|360000|true|false
 * ／xxx／getList|url|／xxx／getList|360000|true|false
 * Created by liangzhiyan on 2017/4/4.
 */
public class ControllerObject {
    /**
     * 流控类型
     */
    private ControllerType controllerType;

    /**
     * 流控开始时间
     */
    private long controllerBeginTime;
    /**
     * 流控时间
     */
    private long controllerTime;
    /**
     * 需要流控的对象
     */
    private String value;

    /**
     * 需要控制的uri地址
     */
    private String uri;

    /**
     * 控制比率，0不控制，100控制全部，在0到100之间控制rate／100
     */
    private int rate;

    /**
     * 是否打开
     */
    private boolean isOpen;

    /**
     * 是否永久开启流控
     */
    private boolean isForeverController;

    public ControllerType getControllerType() {
        return controllerType;
    }

    public void setControllerType(ControllerType controllerType) {
        this.controllerType = controllerType;
    }

    public long getControllerTime() {
        return controllerTime;
    }

    public void setControllerTime(long controllerTime) {
        this.controllerTime = controllerTime;
    }

    public long getControllerBeginTime() {
        return controllerBeginTime;
    }

    public void setControllerBeginTime(long controllerBeginTime) {
        this.controllerBeginTime = controllerBeginTime;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public boolean isForeverController() {
        return isForeverController;
    }

    public void setForeverController(boolean foreverController) {
        isForeverController = foreverController;
    }

    public static ControllerObject buildFlow(String data) {
        String[] paramArray = StringUtils.split(data, CommonConstants.controllerObjectSplit);
        if (paramArray.length != 5) {
            throw new ParameterException("数据格式不对，data：" + data);
        }
        ControllerObject controllerObject = new ControllerObject();
        controllerObject.setUri(paramArray[0]);
        ControllerType controllerType = ControllerType.getControllerType(paramArray[1]);
        if (controllerType == null) {
            throw new ParameterException("不存在这种类型，type：" + paramArray[1]);
        }
        controllerObject.setControllerType(controllerType);
        controllerObject.setValue(paramArray[2]);
        controllerObject.setRate(Integer.parseInt(paramArray[3]));
        controllerObject.setOpen(Boolean.parseBoolean(paramArray[4]));
        return controllerObject;
    }

    public static ControllerObject buildForbidden(String data) {
        String[] paramArray = StringUtils.split(data, CommonConstants.controllerObjectSplit);
        if (paramArray.length != 6) {
            throw new ParameterException("数据格式不对，data：" + data);
        }
        ControllerObject controllerObject = new ControllerObject();
        controllerObject.setUri(paramArray[0]);
        ControllerType controllerType = ControllerType.getControllerType(paramArray[1]);
        if (controllerType == null) {
            throw new ParameterException("不存在这种类型，type：" + paramArray[1]);
        }
        controllerObject.setControllerType(controllerType);
        controllerObject.setValue(paramArray[2]);
        controllerObject.setControllerTime(Long.parseLong(paramArray[3]));
        controllerObject.setOpen(Boolean.parseBoolean(paramArray[4]));
        controllerObject.setForeverController(Boolean.parseBoolean(paramArray[5]));
        return controllerObject;
    }

    public static ControllerObject buildControllerObject(Forbidden forbidden) {
        ControllerObject controllerObject = new ControllerObject();
        controllerObject.setControllerType(ControllerType.getControllerType(forbidden.getControllerType()));
        controllerObject.setControllerBeginTime(forbidden.getControllerBeginTime());
        controllerObject.setControllerTime(forbidden.getControllerTime());
        controllerObject.setValue(forbidden.getValue());
        controllerObject.setUri(forbidden.getUri());
        controllerObject.setRate(forbidden.getRate());
        controllerObject.setOpen(forbidden.isOpen());
        controllerObject.setForeverController(forbidden.isForeverController());
        return controllerObject;
    }

    @Override
    public String toString() {
        return "ControllerObject{" +
                "controllerType=" + controllerType +
                ", controllerBeginTime=" + controllerBeginTime +
                ", controllerTime=" + controllerTime +
                ", value='" + value + '\'' +
                ", uri='" + uri + '\'' +
                ", rate=" + rate +
                ", isOpen=" + isOpen +
                ", isForeverController=" + isForeverController +
                '}';
    }
}
