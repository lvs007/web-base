package com.liang.cache.impl;

import com.liang.cache.BaseCache;
import com.liang.cache.redis.RedisConstants;
import com.liang.common.exception.NotSupportException;
import com.liang.common.util.ReflectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisClusterConnection;
import org.springframework.data.redis.connection.jedis.JedisConnection;
import org.springframework.data.redis.core.RedisConnectionUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisCommands;

import java.util.concurrent.TimeUnit;

/**
 * NX|XX, NX -- Only set the key if it does not already exist. XX -- Only set the key
 * if it already exist.
 * EX|PX, expire time units: EX = seconds; PX = milliseconds
 * Created by liangzhiyan on 2017/5/31.
 */
@Service(value = "redisCache")
public class RedisCache implements BaseCache<String, String>, InitializingBean {

    private static final Logger LOG = LoggerFactory.getLogger(RedisCache.class);

    @Autowired
    RedisTemplate<String, String> template;

    Jedis jedis;

    JedisCluster cluster;

    JedisCommands jedisCommands;

    private void init() throws NoSuchFieldException, IllegalAccessException {
        RedisConnectionFactory factory = template.getConnectionFactory();
        RedisConnection conn = RedisConnectionUtils.getConnection(factory);
        if (conn instanceof JedisConnection) {
            jedisCommands = jedis = (Jedis) ReflectUtils.getValue(conn, "jedis");
        } else if (conn instanceof JedisClusterConnection) {
            jedisCommands = cluster = (JedisCluster) ReflectUtils.getValue(conn, "cluster");
        } else {
            throw NotSupportException.throwException("不支持的redis connection，" + conn);
        }
    }

    @Override
    public String get(String key) {
        return template.opsForValue().get(buildKey(key));
    }

    @Override
    public boolean set(String key, String value) {
        template.opsForValue().set(buildKey(key), value);
        return true;
    }

    @Override
    public boolean set(String key, String value, long expire) {
        template.opsForValue().set(buildKey(key), value, expire, TimeUnit.MILLISECONDS);
        return true;
    }

    @Override
    public boolean setNx(String key, String value, long expire) {
        String result = jedisCommands.set(key, value, RedisConstants.Commands.NX.name(), RedisConstants.TimeUnit.PX.name(), expire);
        return StringUtils.equalsIgnoreCase(RedisConstants.OK, result);
    }

    private void clean() {
        jedis = null;
        cluster = null;
        jedisCommands = null;
    }

    @Override
    public boolean remove(String key) {
        template.delete(buildKey(key));
        return true;
    }

    @Override
    public String buildKey(String key) {
        return key;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        init();
    }
}
