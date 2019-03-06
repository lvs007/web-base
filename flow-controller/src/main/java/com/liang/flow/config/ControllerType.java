package com.liang.flow.config;

import com.liang.common.exception.ParameterException;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by liangzhiyan on 2017/4/3.
 */
public enum ControllerType {
    ALL(0, "全部"), COUNTRY(1, "国家"), AREA(2, "区域"), CITY(3, "城市"), COUNTY(4, "区"), OS(5, "操作系统"), APPTYPE(6, "业务类型"), IP(7, "ip"), DEVICE(8, "设备"), USER(9, "用户"), URL(10, "url");

    ControllerType(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static ControllerType getControllerType(String value) {
        for (ControllerType controllerType : values()) {
            if (StringUtils.equalsIgnoreCase(controllerType.toString(), value)) {
                return controllerType;
            }
        }
        return null;
    }

    public static ControllerType getControllerType(int value) {
        for (ControllerType controllerType : values()) {
            if (controllerType.getValue() == value) {
                return controllerType;
            }
        }
        throw ParameterException.throwException("没有对应的流控类型");
    }

    private int value;
    private String desc;

    public int getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }
}
