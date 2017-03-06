/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package liang.dao.jdbc.annotation;

import java.lang.annotation.*;

/**
 * 注释实体类属于哪一张表
 *
 * @author
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface Table {

    String name();
}
