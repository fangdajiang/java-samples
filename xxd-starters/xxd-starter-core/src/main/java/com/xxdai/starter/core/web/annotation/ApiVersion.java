package com.xxdai.starter.core.web.annotation;

import org.springframework.web.bind.annotation.Mapping;

import java.lang.annotation.*;

/**
 * from: https://www.jianshu.com/p/5b820f393c62
 *
 * ClassName: ApiVersion<br/>
 * Function: url版本注解<br/>
 * Reason: value书写格式 n.n(小数点前最大到 9，小数点后仅支持 4 位，不支持 n.n.n.n 这种格式，
 * 由 ApiVersionCondition 的 compareTo 方法控制)，匹配规则为小于等于n.n最近的一个版本，示例如下：
 * 现有接口 v1.0,v1.1,v2.0,v2.3
 * 访问 v1.1/xxx   执行： v1.1
 * 访问 v1.2/xxx   执行： v1.1
 * 访问 v2.1/xxx   执行： v2.0
 * 访问 v4.0/xxx   执行： v2.3
 * <br/>
 *
 * @author maskwang520
 * @date 2017/12/2
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Mapping
public @interface ApiVersion {
    float value();
}
