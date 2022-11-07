package com.xxdai.starter.core.web.checker;

import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by yq on 2017/3/15.
 */
public interface CommonChecker {
    /**
     * 对请求数据进行身份检查，未通过的信息存放到request的exception字段
     * @param request
     */
    boolean preCheck(HttpServletRequest request, HandlerMethod handlerMethod);
    boolean postCheck(HttpServletRequest request, HandlerMethod handlerMethod);
}
