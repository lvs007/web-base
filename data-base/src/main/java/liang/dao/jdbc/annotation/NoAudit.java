/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package liang.dao.jdbc.annotation;

import java.lang.annotation.*;

/**
 * 此注解放在成员变量上，指示此成员变量在此实体类要实现审计的
 * 时候，此成员不参与审计
 *
 * @author
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
@Inherited
public @interface NoAudit {

}
