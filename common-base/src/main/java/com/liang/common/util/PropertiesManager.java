/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.liang.common.util;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.util.*;

public class PropertiesManager extends PropertyPlaceholderConfigurer implements EnvironmentAware {

    private Properties properties;
    private boolean productionMode = true;

    public Map<String, String> getPropertiesByKeyPrefix(String prefix) {
        Map<String, String> map = new HashMap<>();
        for (String key : properties.stringPropertyNames()) {
            if (key.startsWith(prefix)) {
                map.put(key, properties.getProperty(key));
            }
        }
        return map;
    }

    public String getString(String key) {
        return properties.getProperty(key);
    }

    public String getString(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

//    @Override
//    protected String resolvePlaceholder(String placeholder, Properties props) {
//        String value = super.resolvePlaceholder(placeholder, props);
//        try {
//            value = CryptoUtils.decryptValue(value);
//        } catch (IOException e) {
//            e.printStackTrace();
//            throw new IllegalStateException(e);
//        }
//        return value;
//    }

    public int getInt(String key) {
        return NumberUtils.toInt(getString(key), 0);
    }

    public int getInt(String key, int defaultValue) {
        return NumberUtils.toInt(getString(key), defaultValue);
    }

    public float getFloat(String key) {
        return NumberUtils.toFloat(getString(key), 0f);
    }

    public float getFloat(String key, float defaultValue) {
        return NumberUtils.toFloat(getString(key), defaultValue);
    }

    @Override
    protected Properties mergeProperties() throws IOException {
        properties = super.mergeProperties();
        return properties;
    }

    public boolean isProductionMode() {
        return productionMode;
    }

    public boolean isNotProductionMode() {
        return !productionMode;
    }

    @Override
    public void setEnvironment(Environment e) {
        Set<String> activeProfiles = new HashSet<>();
        String[] ap = e.getActiveProfiles();
        if (ap.length > 0) {
            for (String pro : ap) {
                activeProfiles.add(pro);
            }
        } else {
            String[] defaultProfiles = e.getDefaultProfiles();
            if (defaultProfiles.length > 0) {
                for (String pro : defaultProfiles) {
                    activeProfiles.add(pro);
                }
            }
        }
        if (activeProfiles.size() > 0 && !activeProfiles.contains("production")) {
            productionMode = false;
        }
    }
}

