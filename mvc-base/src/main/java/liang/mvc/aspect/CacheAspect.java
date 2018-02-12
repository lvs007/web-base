package liang.mvc.aspect;

import com.alibaba.fastjson.JSON;
import liang.cache.BaseCache;
import liang.cache.impl.DefaultCommonLocalCache;
import liang.cache.impl.RedisCache;
import liang.mvc.annotation.cache.LocalCacheAnnotation;
import liang.mvc.annotation.cache.RedisCacheAnnotation;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Created by liangzhiyan on 2018/2/7.
 */
@Aspect
public class CacheAspect {

    private static final Logger LOG = LoggerFactory.getLogger(CacheAspect.class);

    @Autowired
    private RedisCache redisCache;

    private static final Map<String, DefaultCommonLocalCache> localCacheMap = new HashMap<>();

    @Around("execution(* * (..)) && (@annotation(liang.mvc.annotation.cache.TairCacheAnnotation) " +
            "|| @annotation(liang.mvc.annotation.cache.LocalCacheAnnotation) " +
            "|| @annotation(liang.mvc.annotation.cache.RedisCacheAnnotation))")
    public Object doCache(ProceedingJoinPoint pjp) throws Throwable {
        Signature signature = pjp.getSignature();
        if (signature instanceof MethodSignature) {
            Method method = MethodSignature.class.cast(signature).getMethod();
            Annotation[] annotations = method.getAnnotations();
            Type type = method.getGenericReturnType();

            if (annotations != null) {
                for (Annotation annotation : annotations) {
                    CacheInfo cacheInfo = new CacheInfo();
                    BaseCache cache = selectCache(annotation, cacheInfo);
                    if (cache != null) {
                        String key = buildKey(pjp, cacheInfo.getPre());
                        Object cacheValue = cache.get(key);
                        //缓存中不存在这个key
                        if (cacheValue == null) {
                            continue;
                        }
                        String value = String.valueOf(cacheValue);
                        return renderReturnValue(type, value);
                    }
                }
                //缓存中不存在，执行方法，然后写入缓存中
                LOG.info("缓存中不存在，调用方法获取。请求的参数：{}", JSON.toJSONString(pjp.getArgs()));
                Object result = pjp.proceed();
                for (Annotation annotation : annotations) {
                    CacheInfo cacheInfo = new CacheInfo();
                    BaseCache cache = selectCache(annotation, cacheInfo);
                    if (cache == null) {
                        continue;
                    }
                    String key = buildKey(pjp, cacheInfo.getPre());
                    cache.set(key, JSON.toJSONString(result), cacheInfo.getExpireTime());
                }
                return result;
            }
        }
        return pjp.proceed();
    }

    private String buildKey(ProceedingJoinPoint pjp, String pre) {
        Object[] params = pjp.getArgs();
        if (params == null || params.length == 0) {
            return pre + pjp.getSignature().getName();
        }
        StringBuffer keyB = new StringBuffer();
        keyB.append(pre);
        for (Object param : params) {
            keyB.append("_");
            if (param instanceof String || param instanceof Byte || param instanceof Character
                    || param instanceof Short || param instanceof Integer || param instanceof Long
                    || param instanceof Float || param instanceof Double) {
                keyB.append(param.toString());
            } else if (param instanceof List) {
                List tmp = (List) param;
                Collections.sort(tmp);
                for (Object value : tmp) {
                    keyB.append(value);
                }
            } else {
                keyB.append(JSON.toJSONString(param));
            }
        }
        return keyB.toString();
    }

    private Object renderReturnValue(Type type, String value) {
        if (type instanceof Collection) {
            return JSON.parseArray(value, type.getClass());
        } else if (type.getClass().isArray()) {
            return JSON.parseArray(value, type.getClass()).toArray();
        } else {
            return JSON.parseObject(value, type);
        }
    }

    private BaseCache selectCache(Annotation annotation, CacheInfo cacheInfo) {
        if (annotation instanceof RedisCacheAnnotation) {
            cacheInfo.setExpireTime(((RedisCacheAnnotation) annotation).expireTime());
            cacheInfo.setPre(((RedisCacheAnnotation) annotation).pre());
            return redisCache;
        } else if (annotation instanceof LocalCacheAnnotation) {
            cacheInfo.setExpireTime(((LocalCacheAnnotation) annotation).expireTime());
            cacheInfo.setPre(((LocalCacheAnnotation) annotation).pre());
            return getDefaultCommonLocalCache(cacheInfo);
        }
        return null;
    }

    private DefaultCommonLocalCache getDefaultCommonLocalCache(CacheInfo cacheInfo) {
        String key = String.valueOf(cacheInfo.getExpireTime());
        DefaultCommonLocalCache defaultCommonLocalCache = localCacheMap.get(key);
        if (defaultCommonLocalCache == null) {
            synchronized (key) {
                defaultCommonLocalCache = localCacheMap.get(key);
                if (defaultCommonLocalCache == null) {
                    defaultCommonLocalCache = DefaultCommonLocalCache.getInstance(cacheInfo.getExpireTime());
                    localCacheMap.put(key, defaultCommonLocalCache);
                }
            }
        }
        return defaultCommonLocalCache;
    }

    private class CacheInfo {
        private String pre;
        private int expireTime;

        public String getPre() {
            return pre;
        }

        public void setPre(String pre) {
            this.pre = pre;
        }

        public int getExpireTime() {
            return expireTime;
        }

        public void setExpireTime(int expireTime) {
            this.expireTime = expireTime;
        }
    }
}
