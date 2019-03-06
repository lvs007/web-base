/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.liang.dao.jdbc.annotation;

import java.lang.annotation.*;

/**
 * 如果某个表可以审计的话，那么此列注释在审计的列上面，构成标题
 *
 * @author
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
@Inherited
public @interface Title {

    /**
     * 优先级，数量越小排在越前面
     *
     * @return
     */
    int priority() default 0;
}
