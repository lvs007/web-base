/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package liang.mvc.annotation;

import java.lang.annotation.*;

/**
 * 需要签名的action类上面的注释
 * 此注释可以放在类上面，也可以放在action方法上面
 *
 * @author
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Sign {

    /**
     * 直接写死的signKey
     *
     * @return
     */
    String signKey() default "";

    /**
     * 从配置文件读取取的signKey，这里存的是propertyName
     *
     * @return
     */
    String signKeyPropertyName() default "";

    /**
     * 跳过签名的值，慎用
     *
     * @return
     */
    String resignValue() default "";

    /**
     * 跳过签名的值在配置文件中的属性名
     *
     * @return
     */
    String resignValuePropertyName() default "";
}
