package com.liang.mvc.handler.mapping;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.StringValueResolver;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.condition.*;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 可配置的请求映射处理类，满足以下功能：<br/>
 * <ul>
 * <li>
 * 支持{@link org.springframework.web.bind.annotation.RequestMapping}注解和类自动映射，RequestMapping配置优先级高于类映射；<br/>
 * 当未使用{@link org.springframework.web.bind.annotation.RequestMapping}注解或者未指定映射路径时，则采用类映射
 * <li>采用正则表达式配置类映射规则，见方法{@link #setClassPattern(String)}和{@link #setClassReplacement(String)}</li>
 * <li>采用正则表达式配置包路径映射，见方法{@link #setPackagePattern(String)}和{@link #setPackageReplacement(String)}</li>
 * </li>
 * </ul>
 * <pre>
 *      <code>设存在类com.abc.controller.xyz.XxxController，在未配置{@link org.springframework.web.bind.annotation.RequestMapping}注解的情况下，则默认取Xxx作为映射路径
 *      此时如果配置packagePattern="^com.abc.controller"，packageReplacement="/api"，则映射路径为/api/xyz/Xxx<br/>
 *      </code>
 *  </pre>
 *
 * @author skyfalling
 */
public class IdealRequestMappingHandlerMapping extends RequestMappingHandlerMapping {

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

    private String classPatternAction = "(.*)Action";
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
    private StringValueResolver nameResolver = new StringValueResolver() {
        @Override
        public String resolveStringValue(String strVal) {
            return strVal;
        }
    };

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

    /**
     * Uses method and type-level @{@link org.springframework.web.bind.annotation.RequestMapping} annotations to create
     * the RequestMappingInfo.
     *
     * @return the created RequestMappingInfo, or {@code null} if the method
     * does not have a {@code @RequestMapping} annotation.
     * @see #getCustomMethodCondition(java.lang.reflect.Method)
     * @see #getCustomTypeCondition(Class)
     */
    @Override
    protected RequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {
        if (AnnotationUtils.findAnnotation(handlerType, NotMapping.class) != null || AnnotationUtils.findAnnotation(method, NotMapping.class) != null)
            return null;
        return createRequestMappingInfo(createRequestMappingConfig(handlerType)).combine(createRequestMappingInfo(createRequestMappingConfig(method)));
    }


    /**
     * 获取方法上的RequestMapping配置
     *
     * @param method
     * @return
     */
    protected RequestMappingConfiguration createRequestMappingConfig(Method method) {
        RequestMappingConfiguration config = new RequestMappingConfiguration();
        RequestMapping annotation = AnnotationUtils.findAnnotation(method, RequestMapping.class);
        String methodName = method.getName();
        config.value(annotation != null && annotation.value().length != 0 ?
                annotation.value() :
                new String[]{nameResolver.resolveStringValue(methodName)});

        List<RequestMethod> requestMethods = new ArrayList<RequestMethod>();
        for (String key : requestMethodPatterns.keySet()) {
            if (methodName.matches(key)) {
                requestMethods.add(requestMethodPatterns.get(key));
            }
        }
        config.method(requestMethods.toArray(new RequestMethod[0]));
        config.requestCondition(getCustomMethodCondition(method));
        return config;
    }

    /**
     * 获取类上的RequestMapping配置
     *
     * @param handlerType
     * @return
     */
    protected RequestMappingConfiguration createRequestMappingConfig(Class handlerType) {
        RequestMappingConfiguration config = new RequestMappingConfiguration();
        RequestMapping annotation = AnnotationUtils.findAnnotation(handlerType, RequestMapping.class);
        Package aPackage = handlerType.getPackage();
        String packageName = aPackage != null ? aPackage.getName() : "";
        String baseName = packageName.replaceAll(packagePattern.replaceAll("\\.+$", ""), packageReplacement);
        String[] values = annotation != null && annotation.value().length != 0 ? annotation.value() :
                new String[]{nameResolver.resolveStringValue(handlerType.getSimpleName()
                        .replaceAll(classPattern, classReplacement).replaceAll(classPatternAction, classReplacement))};
        int i = 0;
        if (annotation == null) {
            for (String value : values) {
                if (StringUtils.contains(baseName, "action")
                        || StringUtils.contains(baseName, "actions")
                        || StringUtils.contains(baseName, "controller")
                        || StringUtils.contains(baseName, "controllers")) {
                    values[i++] = (packageReplacement + "/" + value).replaceAll("/+", "/");
                } else {
                    values[i++] = (baseName + "/" + value).replaceAll("/+", "/");
                }
            }
        }
        config.value(values);
        config.requestCondition(getCustomTypeCondition(handlerType));
        return config;
    }

    /**
     * 根据RequestMapping配置创建RequestMappingInfo对象
     *
     * @param config
     * @return
     */
    protected RequestMappingInfo createRequestMappingInfo(RequestMappingConfiguration config) {
        String[] patterns = resolveEmbeddedValuesInPatterns(config.value());
        return new RequestMappingInfo(
                new PatternsRequestCondition(patterns, getUrlPathHelper(), getPathMatcher(),
                        this.useSuffixPatternMatch(), this.useTrailingSlashMatch(), this.getFileExtensions()),
                new RequestMethodsRequestCondition(config.method()),
                new ParamsRequestCondition(config.params()),
                new HeadersRequestCondition(config.headers()),
                new ConsumesRequestCondition(config.consumes(), config.headers()),
                new ProducesRequestCondition(config.produces(), config.headers(), getContentNegotiationManager()),
                config.requestCondition());
    }


}
