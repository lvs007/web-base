package liang.cache.impl;

import liang.cache.AbstractLocalCache;

/**
 * Created by liangzhiyan on 2017/5/16.
 */
public class DefaultCommonLocalCache extends AbstractLocalCache<String, Object> {

    private static DefaultCommonLocalCache defaultCommonLocalCache;
    private static long expireTime = 24 * 60 * 60;

    private DefaultCommonLocalCache() {
    }

    public static DefaultCommonLocalCache getInstance() {
        return getInstance(expireTime);
    }

    public static DefaultCommonLocalCache getInstance(long expire) {
        expireTime = expire;
        if (defaultCommonLocalCache == null) {
            synchronized (DefaultCommonLocalCache.class) {
                if (defaultCommonLocalCache == null) {
                    defaultCommonLocalCache = new DefaultCommonLocalCache();
                }
            }
        }
        return defaultCommonLocalCache;
    }

    @Override
    public long getExpireTime() {
        return expireTime;
    }
}
