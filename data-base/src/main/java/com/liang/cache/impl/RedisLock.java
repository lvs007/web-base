package com.liang.cache.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.UUID;

/**
 * Created by liangzhiyan on 2017/6/2.
 */
@Service
public class RedisLock {

    private static final long EXPIRE = 5000;
    private ThreadLocal<String> uuidThreadLocal = new ThreadLocal<>();

    @Resource(name = "redisCache")
    private RedisCache redisCache;

    public boolean lock(String key) {
        return lock(key, EXPIRE);
    }

    public boolean lock(String key, long expire) {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        uuidThreadLocal.set(uuid);
        boolean result = redisCache.setNx(key, uuid, expire);
        if (!result) {
            uuidThreadLocal.remove();
        }
        return result;
    }

    public void unLock(String key) {
        String uuid = uuidThreadLocal.get();
        if (StringUtils.isNotBlank(uuid) && uuid.equals(redisCache.get(key))) {
            redisCache.remove(key);
            uuidThreadLocal.remove();
        }
    }
}
