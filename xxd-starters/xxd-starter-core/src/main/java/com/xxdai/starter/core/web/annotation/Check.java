package com.xxdai.starter.core.web.annotation;

import com.xxdai.starter.core.web.checker.CommonChecker;

import java.lang.annotation.*;

/**
 * 用在用@RestController声明的Spring Bean的方法或者类上，用以使用指定的检查器对请求参数进行执行规则的检查
 * Created by yq on 2017/3/15.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE,ElementType.METHOD})
public @interface Check {
    Class<? extends CommonChecker>[] checkerClass();
}
