package org.easytech.blogs.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限检查注解
 * 用于Controller方法级别的权限控制
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresPermission {
    
    /**
     * 需要的权限
     */
    String[] value() default {};
    
    /**
     * 权限检查逻辑：AND 或 OR
     * true: 需要所有权限（AND）
     * false: 需要任一权限（OR）
     */
    boolean requireAll() default true;
    
    /**
     * 权限描述
     */
    String description() default "";
}