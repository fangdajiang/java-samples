package com.xxdai.starter.core.web.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.xxdai.pub.common.model.BaseRequest;
import com.xxdai.pub.common.model.BaseRequestData;
import com.xxdai.pub.common.model.BaseResponse;
import com.xxdai.pub.common.util.RequestUtil;
import com.xxdai.pub.constant.Global;
import com.xxdai.pub.constant.ResultCode;
import com.xxdai.starter.core.util.comm.HttpServletResponseUtil;
import com.xxdai.starter.core.web.annotation.Check;
import com.xxdai.starter.core.web.checker.CommonChecker;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * PreHandle 验证 header 里的参数，有误时存储 exception 到 request 中；
 * 假如有 checkers，进行检查；记录 MDC
 *
 * @author fangdajiang
 */
@Slf4j
public class CommonInterceptor extends HandlerInterceptorAdapter {
    private static final String REQUEST_RECOGNITION = "REQ_RECOGNITION";

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler, ModelAndView modelAndView)
            throws Exception {
        log.debug("ready to exec CommonInterceptor postHandle in xxd-starter-core.jar");
        if (!(handler instanceof HandlerMethod)) {
            log.debug("handler:{} is not a HandlerMethod, ignore to process postHandle()", handler);
        } else {
            doPostCheckers(request, (HandlerMethod) handler);
        }
    }

    /**
     * 逐一检查所有实现了 CommonChecker 的 xxxChecker 里的 postCheck 方法
     *
     */
    private void doPostCheckers(HttpServletRequest request, HandlerMethod handlerMethod)
            throws NoSuchMethodException, IllegalAccessException,
            InstantiationException, InvocationTargetException {
        List<Class<? extends CommonChecker>> checkerClassList = collectCheckerClass(handlerMethod);
        for (Class<? extends CommonChecker> clazz : checkerClassList) {
            //逐个检查
            clazz.getMethod("postCheck", HttpServletRequest.class, HandlerMethod.class)
                    .invoke(clazz.newInstance(), request, handlerMethod);
        }
    }

    /**
     * 获取 header 中的参数并存储在 request 的 baseRequest 中。参数有误时存储 exception 到 request 中。
     * 静态资源返回 true
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            log.debug("handler:{} is not a HandlerMethod, ignore to process preHandle()", handler);
            return true;
        }
        boolean returnValue = false;
        log.debug("ready to exec CommonInterceptor preHandle in xxd-starter-core.jar");

        //提取出过滤器生成的 BaseRequest 对象，补充类、方法进去
        BaseRequest baseRequestFromFilter = (BaseRequest) request.getAttribute(Global.BASE_REQUEST_ATTR_KEY);
        log.debug("baseRequestFromFilter:{}", baseRequestFromFilter);
        BaseRequest baseRequestFilled = fillBaseRequest(baseRequestFromFilter, (HandlerMethod) handler);
        log.debug("baseRequestFilled:{}", baseRequestFilled);
        //提取出过滤器生成的 BaseResponse 对象
        BaseResponse baseResponseFromFilter = (BaseResponse) request.getAttribute(Global.BASE_RESPONSE_ATTR_KEY);
        log.debug("baseResponseFromFilter:{}", baseResponseFromFilter);
        if (!baseResponseFromFilter.isSuccess()) {
            HttpServletResponseUtil.writeResponseWhenError(baseRequestFilled, baseResponseFromFilter, response);
        } else {
            try {
                //检查器处理
                boolean result = doPreCheckers(baseResponseFromFilter, request, (HandlerMethod) handler);
                if (!result) {
                    log.info("doPreCheckers failed, handler:{}, baseResponse:{}", handler, baseResponseFromFilter);
                    HttpServletResponseUtil.writeResponseWhenError(baseRequestFilled, baseResponseFromFilter, response);
                } else {
                    //根据访问的方法及其参数类型在 request 中生成 requestData
                    createRequestData(baseRequestFilled, request);

                    //区分用户 session
                    MDC.put(REQUEST_RECOGNITION, baseRequestFilled.getBaseRequestHeader().getClientId()
                            + "_" + RequestUtil.getRealIpAddress(request)
                            + "_" + baseRequestFilled.getBaseRequestHeader().getClientTime());
                    log.trace("preHandle executed");
                    returnValue = true;
                }
            } catch (Exception e) {
                String errMsg = e.getMessage()
                        + ", Exception occured when treat checkers or body, baseRequestFilled:"
                        + baseRequestFilled + ", baseResponseFromFilter:"
                        + baseResponseFromFilter;
                log.error(errMsg, e);
                baseResponseFromFilter.setResponseCodeMessage(ResultCode.ResponseCode.UNKNOWN_EXCEPTION)
                        .concatInfo(errMsg).setExceptionMessage(e);
                HttpServletResponseUtil.writeResponseWhenError(baseRequestFilled,
                        baseResponseFromFilter, response);
            }
        }
        return returnValue;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) throws Exception {
        MDC.remove(REQUEST_RECOGNITION);
    }

    private List<Class<? extends CommonChecker>> collectCheckerClass(HandlerMethod handlerMethod) {
        List<Class<? extends CommonChecker>> checkerClassList = new ArrayList<>();
        //读取配置在Class上的CheckerClass
        Check checkAnnotation = handlerMethod.getBeanType().getAnnotation(Check.class);
        if (checkAnnotation != null) {
            checkerClassList.addAll(Arrays.asList(checkAnnotation.checkerClass()));
        }
        //读取配置在Method上的CheckerClass
        checkAnnotation = handlerMethod.getMethod().getAnnotation(Check.class);
        if (checkAnnotation != null) {
            for (Class<? extends CommonChecker> clazz : checkAnnotation.checkerClass()) {
                if (!checkerClassList.contains(clazz)) {
                    checkerClassList.add(clazz);
                }
            }
        }
        return checkerClassList;
    }

    /**
     * 检查请求的方法是否配置了检查器，如有，依次执行它们
     * 逻辑修正（未检验）：把 baseResponse.isSuccess() 从 for 中挪到外面作初始判断
     */
    private boolean doPreCheckers(BaseResponse baseResponse, HttpServletRequest request,
                                  HandlerMethod handlerMethod) throws NoSuchMethodException,
            IllegalAccessException, InstantiationException, InvocationTargetException {
        if (!baseResponse.isSuccess()) {
            return false;
        } else {
            List<Class<? extends CommonChecker>> checkerClassList = collectCheckerClass(handlerMethod);
            for (Class<? extends CommonChecker> clazz : checkerClassList) {
                //逐个检查
                clazz.getMethod("preCheck", HttpServletRequest.class,
                        HandlerMethod.class).invoke(clazz.newInstance(),
                        request, handlerMethod);
            }
            return true;
        }
    }

    /**
     * body 中有数据的话，检查 request 所访问的 uri 对应的方法中的所有参数，假如一个参数继承自 BaseRequestData（只能有一个），
     * 则将 body 中的 json 数据解析成该参数的类型并存储到 request 中的 requestData 中
     */
    private void createRequestData(BaseRequest baseRequest, HttpServletRequest request) {
        if (StringUtils.isNotBlank(baseRequest.getBodyString())) {
            Class<?>[] parameterTypes = baseRequest.getRequestMethod().getParameterTypes();
            for (Class<?> clazz : parameterTypes) {
                log.debug("clazz:{}", clazz);
                if (null != clazz.getInterfaces() && clazz.getInterfaces().length > 0
                        && clazz.getInterfaces()[0].equals(BaseRequestData.class)) {
                    log.debug("is sub instance of BaseRequestData");
                    request.setAttribute("requestData", JSONObject.parseObject(baseRequest.getBodyString(), clazz));
                    break;
                } else {
                    log.debug("NOT sub instance of BaseRequestData:{}", clazz);
                }
            }
        }
    }

    private BaseRequest fillBaseRequest(BaseRequest baseRequest, HandlerMethod handlerMethod) {
        baseRequest.setControllerClass(handlerMethod.getBeanType());
        baseRequest.setRequestMethod(handlerMethod.getMethod());
        return baseRequest;
    }

}