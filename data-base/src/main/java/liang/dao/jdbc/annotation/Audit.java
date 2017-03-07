/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package liang.dao.jdbc.annotation;

import java.lang.annotation.*;

/**
 * 指示某个实体类是否需要被审计，如果需要被审计，则
 * 在增删改发生的时候，会自动记录到日志表中
 *
 * @author
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Inherited
public @interface Audit {

    boolean saveOldValue() default true;

    boolean saveNewValue() default true;
}
