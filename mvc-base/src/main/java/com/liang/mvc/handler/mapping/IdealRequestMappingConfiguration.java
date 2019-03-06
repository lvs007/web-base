package com.liang.mvc.handler.mapping;

import com.liang.mvc.handler.resolver.UnderscoreResolver;
import org.springframework.util.StringValueResolver;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.HashMap;
import java.util.Map;

/**
 * 对象{@link IdealRequestMappingHandlerMapping}的属性配置<br/>
 *
 * @author skyfalling
 * @see IdealRequestMappingHandlerMapping
 */
public class IdealRequestMappingConfiguration {
    /**
     * 匹配包名的正则表达式
     */
    private String packagePattern = ".*";
    /**
     * 替换匹配包名的表达式
     */
    private String packageReplacement = "";
    /**
     * 匹配类名的正则表达式
     */
    private String classPattern = "(.*)Controller";
    /**
     * 替换匹配类名的表达式
     */
    private String classReplacement = "$1";

    /**
     * 请求方法配置
     */
    private Map<String, RequestMethod> requestMethodPatterns = new HashMap<String, RequestMethod>();

    /**
     * 类名及方法名的处理
     */
    private StringValueResolver nameResolver = new UnderscoreResolver();

    public String getPackagePattern() {
        return packagePattern;
    }

    /**
     * 设置包名匹配模式,默认值".*"
     *
     * @param packagePattern
     */
    public void setPackagePattern(String packagePattern) {
        this.packagePattern = packagePattern;
    }

    public String getPackageReplacement() {
        return packageReplacement;
    }

    /**
     * 设置匹配包名的替换表达式,默认值""
     *
     * @param packageReplacement
     */
    public void setPackageReplacement(String packageReplacement) {
        this.packageReplacement = packageReplacement;
    }

    public String getClassPattern() {
        return classPattern;
    }

    /**
     * 设置匹配类名的正则表达式,默认值"(.*)Controller"
     *
     * @param classPattern
     */
    public void setClassPattern(String classPattern) {
        this.classPattern = classPattern;
    }

    public String getClassReplacement() {
        return classReplacement;
    }

    /**
     * 设置匹配类名的替换表达式,默认值"$1"
     *
     * @param classReplacement
     */
    public void setClassReplacement(String classReplacement) {
        this.classReplacement = classReplacement;
    }

    public Map<String, RequestMethod> getRequestMethodPatterns() {
        return requestMethodPatterns;
    }

    /**
     * 根据方法名正则表达式设置允许的RequestMethod
     *
     * @param requestMethodPatterns
     */
    public void setRequestMethodPatterns(Map<String, RequestMethod> requestMethodPatterns) {
        this.requestMethodPatterns = requestMethodPatterns;
    }

    public StringValueResolver getNameResolver() {
        return nameResolver;
    }

    /**
     * 设置匹配路径的处理类,这里用于对匹配路径的转换
     *
     * @param nameResolver
     */
    public void setNameResolver(StringValueResolver nameResolver) {
        this.nameResolver = nameResolver;
    }


}
