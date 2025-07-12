package org.easytech.blogs.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 公开访问注解
 * 标记无需认证即可访问的接口
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PublicAccess {
    
    /**
     * 描述信息
     */
    String value() default "公开访问";
}