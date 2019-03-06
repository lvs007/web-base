/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.liang.dao.jdbc.annotation;

import java.lang.annotation.*;

/**
 * 如果不是采用默认的命名法，则可以用此注释来注解实体类的列
 *
 * @author
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
@Inherited
public @interface Column {

    String name();
}
