package liang.flow.config.data.entity;

import liang.dao.jdbc.annotation.Table;

/**
 * forbidden数据格式：uri|类型|流控对象（根据类型不同而不同，如：类型是url的是／xxx／action，
 * 类型是user的是liangzhiyan，类型是ip的是120.10.1.10等）|controllerTime|isOpen|isForeverController
 * <p>
 * Created by liangzhiyan on 2018/3/21.
 */
@Table(name = "forbidden")
public class Forbidden {

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

    public Forbidden setId(long id) {
        this.id = id;
        return this;
    }

    public int getControllerType() {
        return controllerType;
    }

    public Forbidden setControllerType(int controllerType) {
        this.controllerType = controllerType;
        return this;
    }

    public long getControllerBeginTime() {
        return controllerBeginTime;
    }

    public Forbidden setControllerBeginTime(long controllerBeginTime) {
        this.controllerBeginTime = controllerBeginTime;
        return this;
    }

    public long getControllerTime() {
        return controllerTime;
    }

    public Forbidden setControllerTime(long controllerTime) {
        this.controllerTime = controllerTime;
        return this;
    }

    public String getValue() {
        return value;
    }

    public Forbidden setValue(String value) {
        this.value = value;
        return this;
    }

    public String getUri() {
        return uri;
    }

    public Forbidden setUri(String uri) {
        this.uri = uri;
        return this;
    }

    public int getRate() {
        return rate;
    }

    public Forbidden setRate(int rate) {
        this.rate = rate;
        return this;
    }

    public boolean isOpen() {
        return open;
    }

    public Forbidden setOpen(boolean open) {
        this.open = open;
        return this;
    }

    public boolean isForeverController() {
        return foreverController;
    }

    public Forbidden setForeverController(boolean foreverController) {
        this.foreverController = foreverController;
        return this;
    }

    public long getQps() {
        return qps;
    }

    public Forbidden setQps(long qps) {
        this.qps = qps;
        return this;
    }

    public int getSameTimeQ() {
        return sameTimeQ;
    }

    public Forbidden setSameTimeQ(int sameTimeQ) {
        this.sameTimeQ = sameTimeQ;
        return this;
    }

    @Override
    public String toString() {
        return "Forbidden{" +
                "id=" + id +
                ", controllerType=" + controllerType +
                ", controllerBeginTime=" + controllerBeginTime +
                ", controllerTime=" + controllerTime +
                ", value='" + value + '\'' +
                ", uri='" + uri + '\'' +
                ", rate=" + rate +
                ", open=" + open +
                ", foreverController=" + foreverController +
                ", qps=" + qps +
                ", sameTimeQ=" + sameTimeQ +
                '}';
    }
}
