package com.liang.flow.core;

import org.apache.commons.collections4.MapUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by liangzhiyan on 2017/4/5.
 */
public class ControllerManager {

    public static final List<BaseFlowController> controllerList = new ArrayList<>();

    static {
        controllerList.add(new CountryFlowController());
        controllerList.add(new AreaFlowController());
        controllerList.add(new CityFlowController());
        controllerList.add(new CountyFlowController());
        controllerList.add(new OsFlowController());
        controllerList.add(new AppTypeFlowController());
        controllerList.add(new IpFlowController());
        controllerList.add(new DeviceFlowController());
        controllerList.add(new UserFlowController());
        controllerList.add(new UrlFlowController());
    }
}
