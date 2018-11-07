package liang.cache.impl;

import liang.cache.AbstractLocalLoadingCache;
import liang.cache.BaseService;
import liang.common.exception.NotSupportException;

/**
 * Created by liangzhiyan on 2017/5/30.
 */
public class DefaultCommonLocalLoadingCache extends AbstractLocalLoadingCache<String, Object> {

    private static final String PRE = "loading-";
    private BaseService baseService;

    public DefaultCommonLocalLoadingCache(BaseService baseService) {
        this.baseService = baseService;
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
        return PRE + key;
    }

    @Override
    protected Object loadData(String key) {
        return baseService.findByKey(key);
    }
}
