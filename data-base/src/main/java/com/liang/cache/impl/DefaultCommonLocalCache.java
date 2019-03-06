package com.liang.cache.impl;

import com.liang.cache.AbstractLocalCache;
import com.liang.common.exception.NotSupportException;

/**
 * Created by liangzhiyan on 2017/5/16.
 */
public class DefaultCommonLocalCache extends AbstractLocalCache<String, Object> {

    public DefaultCommonLocalCache() {
    }

    public DefaultCommonLocalCache(long expireTime) {
        super(expireTime);
    }

    public DefaultCommonLocalCache(long expireTime, int initialCapacity, long maximumSize) {
        super(initialCapacity, maximumSize, expireTime);
    }

    @Override
    public boolean set(String key, Object value, long expire) {
        return set(key, value);
    }

    @Override
    public boolean setNx(String key, Object value, long expire) {
        throw NotSupportException.throwException("不支持的操作");
    }

    @Override
    public String buildKey(String key) {
        return key;
    }
}
