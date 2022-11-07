package com.xxdai.starter.core.web.filter;

import com.alibaba.fastjson.JSONObject;
import com.xxdai.pub.common.model.BaseRequest;
import com.xxdai.pub.common.model.BaseRequestHeader;
import com.xxdai.pub.common.model.BaseResponse;
import com.xxdai.pub.common.model.ClientCfgObj;
import com.xxdai.pub.common.util.RequestUtil;
import com.xxdai.pub.common.util.ResponseUtil;
import com.xxdai.pub.common.util.SpringUtil;
import com.xxdai.pub.constant.Global;
import com.xxdai.pub.constant.ResultCode;
import com.xxdai.starter.core.util.CheckerUtil;
import com.xxdai.starter.core.util.comm.HttpServletResponseUtil;
import com.xxdai.starter.core.util.helper.BaseRequestHelper;
import com.xxdai.starter.core.util.helper.PropertiesHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 *
 * 对 /xxd-v2 和 /demo 和 /register 打头的 URI 进行过滤，从而不为基础业务做准备（不创建 baseRequest & baseResponse，不验证请求中基本参数的合法性，不向 request 中添加 baseRequest 和 baseResponse 属性）
 *
 * @author fangdajiang
 * @date 2018/6/7
 */
@Slf4j
@WebFilter(filterName = "commonFilter", urlPatterns = "/*", dispatcherTypes = {DispatcherType.REQUEST, DispatcherType.FORWARD})
public class CommonFilter implements Filter {
    private static final Set<String> TO_PROCESS_PATHS = Collections.unmodifiableSet(new HashSet<>(
            Arrays.asList("/xxd-v2", "/demo", "/register")));

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        String path = RequestUtil.getProcessablePath(request);
        log.trace("CommonFilter entered...incoming uri: {}, final path:{}", request.getRequestURI(), path);

        boolean toProcessPath = TO_PROCESS_PATHS.contains(path);

        if (toProcessPath) {
            //初始化BaseRequest对象，检查后放入 request 中
            BaseRequest baseRequest = createBaseRequest(request);
            //初始化BaseResponse对象，准备放入 request 中
            BaseResponse baseResponse = new BaseResponse();

            //请求参数检查
            boolean result = checkRequestArguments(baseRequest,baseResponse);
            if(!result){
                log.info("checkRequestArguments failed, baseRequest:{}, baseResponse:{}, uri:{}, path:{}", baseRequest, baseResponse, request.getRequestURI(), path);
                HttpServletResponse response = (HttpServletResponse)servletResponse;
                HttpServletResponseUtil.writeResponseWhenError(baseRequest,baseResponse,response);
                return;
            }else {
                log.debug("won't check the request arguments, baseRequest:{}", baseRequest);
            }
            log.trace("ready to set baseRequest:{}, baseResponse:{}", baseRequest, baseResponse);
            //为检查器准备好数据
            request.setAttribute(Global.BASE_REQUEST_ATTR_KEY,baseRequest);
            request.setAttribute(Global.BASE_RESPONSE_ATTR_KEY,baseResponse);

        } else {
            log.debug("requests from path [{}] will NOT be filtered," +
                    " meaning system will NOT produce baseRequest & baseResponse.", path);
        }
        log.trace("leaving CommonFilter for next filter...");
        filterChain.doFilter(servletRequest, servletResponse);
    }

    /**
     * 检查 clientId, clientTime 的合法性。body 中有数据的话也会检查其合法性（含 data 节点）。如果有 currentPage 和 pageSize 也
     * 进行检查。检查失败则设置返回码到 baseResponse 中。
     */
    private boolean checkRequestArguments(BaseRequest baseRequest,BaseResponse baseResponse){
        BaseRequestHeader baseRequestHeader = baseRequest.getBaseRequestHeader();
        if(StringUtils.isEmpty(baseRequestHeader.getClientId()) || StringUtils.isEmpty(baseRequestHeader.getClientTime())){
            String _msg = "clientId or clientTime is empty";
            log.warn(_msg);
            baseResponse.setResponseCodeMessage(ResultCode.ResponseCode.ILLEGAL_ARGS).concatInfo(_msg);
            return false;
        }else{
            ClientCfgObj clientCfgObj = PropertiesHelper.getClientCfgObj(baseRequestHeader.getClientId());
            boolean hasClient = Optional.ofNullable(clientCfgObj)
                    .map(ClientCfgObj::getClientId)
                    .map(clientId -> !clientId.isEmpty())
                    .orElse(false);
            log.debug("clientCfgObj:{}, hasClient:{}", clientCfgObj, hasClient);

            if (hasClient) {
                //检查 client time 的合法性
                try {
                    SpringUtil.getBean(CheckerUtil.class).checkBaseRequestHeader(baseRequestHeader);
                } catch (IllegalArgumentException iae) {
                    baseResponse.setResponseCodeMessage(ResultCode.ResponseCode.ILLEGAL_ARGS).concatInfo(iae.getMessage());
                    return false;
                }
            } else {
                return ResponseUtil.foundClientId(baseRequestHeader.getClientId(), baseResponse);
            }
        }
        String bodyString = baseRequest.getBodyString();
        log.debug("bodyString:{}", bodyString);
        if(!StringUtils.isEmpty(bodyString)){
            JSONObject bodyJson;
            try{
                bodyJson = JSONObject.parseObject(bodyString);
            }catch (Exception e){
                String errMsg = "Exception occured when parse requestBodyString to JSON";
                log.warn("body:{}, errMsg:{}", bodyString, errMsg);
                baseResponse.setResponseCodeMessage(ResultCode.ResponseCode.ILLEGAL_ARGS).concatInfo(errMsg);
                return false;
            }
            if(!bodyJson.containsKey(Global.REQUEST_JSON_BODY_ROOT_NODE)){
                String errMsg = "requestBodyString must contain \"data\" node";
                log.warn(errMsg);
                baseResponse.setResponseCodeMessage(ResultCode.ResponseCode.ILLEGAL_ARGS).concatInfo(errMsg);
                return false;
            }else {
                log.debug("body json {} doesn't contain {}", bodyJson, Global.REQUEST_JSON_BODY_ROOT_NODE);
            }
        }
        JSONObject jsonObject= baseRequest.getParamJson();
        log.debug("jsonObject:{}", jsonObject);
//检查 currentPage 和 pageSize，有值但非法的话，用默认值覆盖
        if (null != jsonObject) {
            checkRequestParams(Global.PARAMETER_KEY_CURRENT_PAGE, Global.DEFAULT_CURRENT_PAGE, Global.MAX_CURRENT_PAGE, jsonObject);
            checkRequestParams(Global.PARAMETER_KEY_PAGE_SIZE, Global.DEFAULT_PAGE_SIZE, Global.MAX_PAGE_SIZE, jsonObject);
        }
        if (null == baseResponse.getData()) {
            log.debug("baseResponse.getData() is null when checkRequestArguments()");
            baseResponse.setData("");
        }
        return true;
    }

    /**
     * 检查 currentPage 和 pageSize 值的合法性
     */
    private static void checkRequestParams(String paramName, int defaultParamValue, int maxParamValue, JSONObject jsonObject) {
        String paramValue = jsonObject.getString(paramName);
        //有参数，继续检查值是否合法
        if (null != paramValue) {
            try {
                int paramValueInt = Integer.parseInt(paramValue);
                if (paramValueInt <= 0 || paramValueInt >= maxParamValue) {
                    throw new IllegalArgumentException("paramValueInt:" + paramValueInt + " is out of range(0," + maxParamValue + ")");
                }
            } catch (Exception e) { //仅在抛出异常时才覆盖原值
                log.warn("illegal param value, paramName:{}, jsonObject:{}, msg:{}", paramName, jsonObject, e.getMessage());
                jsonObject.put(paramName, String.valueOf(defaultParamValue));
            }
        } else {
            log.debug("paramName:{}, paramValue is null", paramName);
        }
    }

    private BaseRequest createBaseRequest(HttpServletRequest request){
        BaseRequest baseRequest = new BaseRequest();
        baseRequest.setBaseRequestHeader(BaseRequestHelper.getBaseHeader(request));
        baseRequest.setParamJson(RequestUtil.getParamJsonFromRequest(request));
        baseRequest.setBodyString(RequestUtil.getBodyContent(request));
        return baseRequest;
    }


    @Override
    public void destroy() {

    }
}
