package com.xxdai.starter.sample.annotation;

import java.lang.annotation.*;

/**
 *
 * @author fangdajiang
 * @date 2018/6/21
 */
@Target(value = {ElementType.TYPE, ElementType.METHOD})
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface MyAnnotation {
    String test() default "default value";
    String value() default "";
    String name() default "devin"; //String
    int age() default 18; //int
    boolean isStudent() default true;
    String[] alias();
    enum Color {GREEN, BLUE, RED,}
    Color favoriteColor() default Color.GREEN;
}
