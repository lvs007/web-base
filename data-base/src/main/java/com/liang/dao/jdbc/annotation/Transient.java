/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.liang.dao.jdbc.annotation;

import java.lang.annotation.*;

/**
 * 提示某列是不需要持久化的
 * @author
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
@Inherited
public @interface Transient {
    
}
