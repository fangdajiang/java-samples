package com.xxdai.starter.core.web.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xxdai.pub.common.exception.*;
import com.xxdai.pub.common.model.BaseRequest;
import com.xxdai.pub.common.model.BaseRequestHeader;
import com.xxdai.pub.common.model.BaseResponse;
import com.xxdai.pub.common.model.BaseResponseHeader;
import com.xxdai.pub.constant.Global;
import com.xxdai.pub.constant.ResultCode;
import com.xxdai.starter.core.config.property.ClientCfgProperties;
import com.xxdai.starter.core.exception.XxdRequestException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ValidationException;
import java.net.ConnectException;
import java.sql.SQLException;

/**
 * 根据拦截器处理的结果打包成返回客户端的结果
 * Created by fangdajiang on 2017/3/13.
 */
@Slf4j
public abstract class BaseController {
    private static final Logger BIG_DATA_LOGGER = LoggerFactory.getLogger("bigdata");
    @Autowired private ClientCfgProperties clientCfgProperties;

    /**
     * 只检查当前 controller 里面各方法都会需要检查的参数。而各方法自己的参数检查，需要在自己方法中实现。
     * @param baseRequest
     */
    protected abstract void checkParameters(BaseRequest baseRequest);
    /**
     * 对controller定义的业务代码进行执行，并对返回内容进行组装
     * @param bizCallback 装载业务代码段的执行方法
     * @return
     */
    protected ResponseEntity<String> assembleResp(BizCallback bizCallback) {
        log.trace("ready to assemble response");
        ServletRequestAttributes requestAttributes = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes());
        HttpServletRequest request = requestAttributes.getRequest();
        HttpServletResponse response = requestAttributes.getResponse();
        BaseRequest baseRequest = (BaseRequest)request.getAttribute(Global.BASE_REQUEST_ATTR_KEY);
        //请求日志收集
        BIG_DATA_LOGGER.info("{\"timestamp\":{},\"baseRequest\":{}}",System.currentTimeMillis(),JSONObject.toJSONString(baseRequest));
        BaseResponse baseResponse = (BaseResponse)request.getAttribute(Global.BASE_RESPONSE_ATTR_KEY);
        //将业务结果放到 data 节点中
        try {
            checkParameters(baseRequest);
            baseResponse.setData(JSONObject.toJSON(bizCallback.execute()));
        } catch (IllegalArgumentException e) {
            baseResponse.setResponseCodeMessage(ResultCode.ResponseCode.ILLEGAL_ARGS).concatInfo(e.getMessage());
            log.info("baseRequest:{}, baseResp:{}", baseRequest, baseResponse, e);
        } catch (IllegalStateException e) {
            baseResponse.setResponseCodeMessage(ResultCode.ResponseCode.ILLEGAL_DATA).concatInfo(e.getMessage());
            log.warn("baseRequest:{}, baseResp:{}", baseRequest, baseResponse, e);
        } catch (IllegalAccessException e) {
            baseResponse.setResponseCodeMessage(ResultCode.ResponseCode.ILLEGAL_OPERATION).concatInfo(e.getMessage());
            log.warn("baseRequest:{}, baseResp:{}", baseRequest, baseResponse, e);
        } catch (ConnectException e) {
            baseResponse.setResponseCodeMessage(ResultCode.ResponseCode.NETWORK_EXCEPTION).concatInfo(e.getMessage());
            log.warn("baseRequest:{}, baseResp:{}", baseRequest, baseResponse, e);
        } catch (SQLException e) {
            baseResponse.setResponseCodeMessage(ResultCode.ResponseCode.DB_EXCEPTION).concatInfo(e.getMessage());
            log.warn("baseRequest:{}, baseResp:{}", baseRequest, baseResponse, e);
        } catch (UnsupportedOperationException e) {
            baseResponse.setResponseCodeMessage(ResultCode.ResponseCode.METHOD_NOT_SUPPORTED).concatInfo(e.getMessage());
            log.warn("baseRequest:{}, baseResp:{}", baseRequest, baseResponse, e);
        } catch (DataNotFoundException e) {
            baseResponse.setResponseCodeMessage(ResultCode.ResponseCode.DATA_NOT_FOUND).concatInfo(e.getMessage());
            log.warn("baseRequest:{}, baseResp:{}", baseRequest, baseResponse, e);
        } catch (RepetitiveOperationException e) {
            baseResponse.setResponseCodeMessage(ResultCode.ResponseCode.REPETITIVE_OPERATION).concatInfo(e.getMessage());
            log.warn("baseRequest:{}, baseResp:{}", baseRequest, baseResponse, e);
        }catch (ValidationException e){
            baseResponse.setResponseCodeMessage(ResultCode.ResponseCode.ILLEGAL_ARGS).concatInfo(e.getMessage());
            log.warn("baseRequest:{}, baseResp:{}", baseRequest, baseResponse, e);
        }catch (XxdRequestException e) {
            baseResponse.setCode(e.getCode());
            baseResponse.setMessage(e.getMessage());
            log.warn("baseRequest:{}, baseResp:{}", baseRequest, baseResponse, e);
        }catch (TokenExpiredException e) {
            baseResponse.setResponseCodeMessage(ResultCode.ResponseCode.EXPIRED_TOKEN).concatInfo(e.getMessage());
            log.info("baseRequest:{}, baseResp:{}", baseRequest, baseResponse, e);
        }catch (BizException e) {
            baseResponse.setResponseCodeMessage(ResultCode.ResponseCode.BIZ_EXCEPTION).concatInfo(e.getMessage());
            log.warn("baseRequest:{}, baseResp:{}", baseRequest, baseResponse, e);
        }catch (InvalidResponseException e) {
            baseResponse.setResponseCodeMessage(ResultCode.ResponseCode.INVALID_RESPONSE_EXCEPTION).concatInfo(e.getMessage());
            log.warn("baseRequest:{}, baseResp:{}", baseRequest, baseResponse, e);
        }catch (Exception e) {
            baseResponse.setResponseCodeMessage(ResultCode.ResponseCode.UNKNOWN_EXCEPTION).concatInfo(e.getMessage());
            log.warn("baseRequest:{}, baseResp:{}", baseRequest, baseResponse, e);
        } finally {
            //不成功时才返回 request 节点
            if (!baseResponse.isSuccess()) {
                baseResponse.setRequest(baseRequest);
            }
        }
        String responseDataString = baseResponse.getData() == null ? "" : JSONObject.toJSONString(baseResponse.getData());
        BaseResponseHeader baseResponseHeader = createResponseHeader(baseRequest.getBaseRequestHeader(),responseDataString);
        response.addHeader("clientId",baseResponseHeader.getClientId());
        response.addHeader("serverTime",baseResponseHeader.getServerTime());
        response.addHeader("s",baseResponseHeader.getServerSign());
        //结果日志收集
        BIG_DATA_LOGGER.info("{\"timestamp\":{},\"baseResponse\":{}}",System.currentTimeMillis(),JSONObject.toJSONString(baseResponse));
        return new ResponseEntity<>(JSON.toJSONString(baseResponse), null, HttpStatus.OK);
    }

    private BaseResponseHeader createResponseHeader(BaseRequestHeader baseRequestHeader, String responseDataString){
        long currentTimeMillis = System.currentTimeMillis();
        String assembleString = baseRequestHeader.getClientId() + currentTimeMillis + responseDataString + clientCfgProperties.getCfgObj(baseRequestHeader.getClientId()).getKey();
        String responseSign = DigestUtils.md5Hex(assembleString);
        BaseResponseHeader baseResponseHeader = new BaseResponseHeader(baseRequestHeader.getClientId(),currentTimeMillis+"", responseSign);
        return baseResponseHeader;
    }
}
