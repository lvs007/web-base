package com.liang.flow.dto;

/**
 * Created by liangzhiyan on 2018/3/21.
 */
public class ForbiddenVo {

    private long id;
    /**
     * 流控类型
     */
    private int controllerType;

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
    private boolean open;

    /**
     * 是否永久开启流控
     */
    private boolean foreverController;

    private long qps;

    /**
     * 并发访问量
     */
    private int sameTimeQ;

    public long getId() {
        return id;
    }

    public ForbiddenVo setId(long id) {
        this.id = id;
        return this;
    }

    public int getControllerType() {
        return controllerType;
    }

    public ForbiddenVo setControllerType(int controllerType) {
        this.controllerType = controllerType;
        return this;
    }

    public long getControllerBeginTime() {
        return controllerBeginTime;
    }

    public ForbiddenVo setControllerBeginTime(long controllerBeginTime) {
        this.controllerBeginTime = controllerBeginTime;
        return this;
    }

    public long getControllerTime() {
        return controllerTime;
    }

    public ForbiddenVo setControllerTime(long controllerTime) {
        this.controllerTime = controllerTime;
        return this;
    }

    public String getValue() {
        return value;
    }

    public ForbiddenVo setValue(String value) {
        this.value = value;
        return this;
    }

    public String getUri() {
        return uri;
    }

    public ForbiddenVo setUri(String uri) {
        this.uri = uri;
        return this;
    }

    public int getRate() {
        return rate;
    }

    public ForbiddenVo setRate(int rate) {
        this.rate = rate;
        return this;
    }

    public boolean isOpen() {
        return open;
    }

    public ForbiddenVo setOpen(boolean open) {
        this.open = open;
        return this;
    }

    public boolean isForeverController() {
        return foreverController;
    }

    public ForbiddenVo setForeverController(boolean foreverController) {
        this.foreverController = foreverController;
        return this;
    }

    public long getQps() {
        return qps;
    }

    public ForbiddenVo setQps(long qps) {
        this.qps = qps;
        return this;
    }

    public int getSameTimeQ() {
        return sameTimeQ;
    }

    public ForbiddenVo setSameTimeQ(int sameTimeQ) {
        this.sameTimeQ = sameTimeQ;
        return this;
    }
}
