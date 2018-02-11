package liang.mvc.annotation.cache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by liangzhiyan on 2018/2/7.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisCacheAnnotation {
    /**
     * 缓存key的前缀
     *
     * @return
     */
    String pre() default "tair-";

    /**
     * 缓存的过期时间
     *
     * @return
     */
    int expireTime() default 60;
}
