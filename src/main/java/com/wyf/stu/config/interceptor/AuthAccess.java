package com.wyf.stu.config.interceptor;

import java.lang.annotation.*;


/**
 * @description 自定义注解
 */

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AuthAccess {

}
