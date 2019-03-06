package com.liang.mvc.handler.resolver;

import org.springframework.util.StringValueResolver;

/**
 * 驼峰命名法转下划线命名法
 * <ul>
 * <li>
 * <li>当使用默认类映射规则时，可指定类和方法名是否转换为下划线命名方式，方法{@link #setUserUnderscore(boolean)}</li>
 * <li>当使用默认类映射规则时，可指定配置路径是否自动转为小写，方法{@link #setUseLowerCase(boolean)}</li>
 * </li>
 * </ul>
 *
 * @author skyfalling
 */
public class UnderscoreResolver implements StringValueResolver {

    /**
     * 是否启用小写模式
     */
    private boolean useLowerCase = true;
    /**
     * 是否启用下划线命名方式
     */
    private boolean userUnderscore = true;

    public boolean isUseLowerCase() {
        return useLowerCase;
    }

    /**
     * 设置是否启用小写模式
     *
     * @param useLowerCase
     */
    public void setUseLowerCase(boolean useLowerCase) {
        this.useLowerCase = useLowerCase;
    }

    public boolean isUserUnderscore() {
        return userUnderscore;
    }

    /**
     * 设置是否使用下划线命名法
     *
     * @param userUnderscore
     */
    public void setUserUnderscore(boolean userUnderscore) {
        this.userUnderscore = userUnderscore;
    }

    /**
     * 驼峰转下划线
     *
     * @param name
     * @return
     */
    private static String hump2Underscore(String name) {
        StringBuilder sb = new StringBuilder();
        int length = name.length();
        char[] chars = name.toCharArray();
        for (int i = 0; i < length; i++) {
            char ch = chars[i];
            if (Character.isUpperCase(ch)) {
                if (i > 0 && Character.isUpperCase(chars[i - 1]) && i < length - 1 && Character.isLowerCase(chars[i + 1])) {
                    sb.append("-");
                }
                sb.append(ch);
            } else {
                sb.append(ch);
                if (i < length - 1 && Character.isUpperCase(chars[i + 1])) {
                    sb.append("-");
                }
            }
        }
        return sb.toString();
    }

    @Override
    public String resolveStringValue(String strVal) {
        if (userUnderscore) {
            strVal = hump2Underscore(strVal);
        }
        return useLowerCase ? strVal.toLowerCase() : strVal;
    }
}
