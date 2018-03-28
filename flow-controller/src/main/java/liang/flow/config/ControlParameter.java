package liang.flow.config;

/**
 * Created by liangzhiyan on 2017/4/6.
 */
public class ControlParameter {

    private String currentUri;

    private String url;
    private String user;
    private String device;
    private String ip;
    private String appType;
    private String os;
    private String county;
    private String city;
    private String area;
    private String country;

    public String getCurrentUri() {
        return currentUri;
    }

    public void setCurrentUri(String currentUri) {
        this.currentUri = currentUri;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getAppType() {
        return appType;
    }

    public void setAppType(String appType) {
        this.appType = appType;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public String toString() {
        return "ControlParameter{" +
                "currentUri='" + currentUri + '\'' +
                ", url='" + url + '\'' +
                ", user='" + user + '\'' +
                ", device='" + device + '\'' +
                ", ip='" + ip + '\'' +
                ", appType='" + appType + '\'' +
                ", os='" + os + '\'' +
                ", county='" + county + '\'' +
                ", city='" + city + '\'' +
                ", area='" + area + '\'' +
                ", country='" + country + '\'' +
                '}';
    }
}
