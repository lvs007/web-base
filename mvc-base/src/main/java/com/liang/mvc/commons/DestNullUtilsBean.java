package com.liang.mvc.commons;

/**
 * Created by mc-050 on 2017/1/17 17:00.
 * KIVEN will tell you life,send email to xxx@163.com
 */

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.PropertyUtils;

import java.lang.reflect.InvocationTargetException;

public class DestNullUtilsBean
        extends BeanUtilsBean {
    public void copyProperty(Object dest, String name, Object value)
            throws IllegalAccessException, InvocationTargetException {
        try {
            if ((value != null) && (PropertyUtils.getProperty(dest, name) == null)) {
                super.copyProperty(dest, name, value);
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
}

