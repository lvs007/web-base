package com.liang.mvc.commons;

/**
 * Created by mc-050 on 2017/1/17 16:59.
 * KIVEN will tell you life,send email to xxx@163.com
 */

import org.apache.commons.beanutils.BeanUtilsBean;

import java.lang.reflect.InvocationTargetException;

public class OrigNotNullUtilsBean
        extends BeanUtilsBean {
    public void copyProperty(Object dest, String name, Object value)
            throws IllegalAccessException, InvocationTargetException {
        if (value == null) {
            return;
        }
        super.copyProperty(dest, name, value);
    }
}
