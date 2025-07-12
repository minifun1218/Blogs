package org.easytech.blogs.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 角色检查注解
 * 用于Controller方法级别的角色控制
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresRole {
    
    /**
     * 需要的角色
     */
    String[] value() default {};
    
    /**
     * 角色检查逻辑：AND 或 OR
     * true: 需要所有角色（AND）
     * false: 需要任一角色（OR）
     */
    boolean requireAll() default false;
    
    /**
     * 角色描述
     */
    String description() default "";
}