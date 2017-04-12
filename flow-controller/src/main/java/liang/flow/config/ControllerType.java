package liang.flow.config;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by liangzhiyan on 2017/4/3.
 */
public enum ControllerType {
    ALL, COUNTRY, AREA, CITY, COUNTY, OS, APPTYPE, IP, DEVICE, USER, URL;

    public static ControllerType getControllerType(String value) {
        for (ControllerType controllerType : values()) {
            if (StringUtils.equalsIgnoreCase(controllerType.toString(), value)) {
                return controllerType;
            }
        }
        return null;
    }
}
