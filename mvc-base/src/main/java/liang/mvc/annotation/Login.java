/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package liang.mvc.annotation;

import java.lang.annotation.*;

/**
 * 客户端的登陆校验
 * 需要签名的action类上面的注释
 * 此注释可以放在类上面，也可以放在action方法上面
 *
 * @author
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Login {
}
