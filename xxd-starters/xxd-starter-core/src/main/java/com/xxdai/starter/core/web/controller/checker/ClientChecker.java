package com.xxdai.starter.core.web.controller.checker;

import com.xxdai.pub.common.model.BaseRequest;
import com.xxdai.pub.common.model.BaseRequestHeader;
import com.xxdai.pub.common.model.BaseResponse;
import com.xxdai.pub.common.model.ClientCfgObj;
import com.xxdai.pub.common.util.ResponseUtil;
import com.xxdai.pub.constant.Global;
import com.xxdai.pub.constant.ResultCode;
import com.xxdai.starter.core.util.helper.PropertiesHelper;
import com.xxdai.starter.core.web.checker.CommonChecker;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * 对用户请求的客户签名信息进行验证
 * 需要在请求的header中提供clientId，clientTime，s三个非空参数用以验证，参数有误/验证不通过会中断后续验证并直接返回错误信息
 *              参数可按需配置（方便swagger测试）
 * Created by yq on 2017/3/17.
 */
@Slf4j
public class ClientChecker implements CommonChecker {
    private final static Pattern IP_PATTERN = Pattern.compile("^([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5]|\\*)(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5]|\\*)){3}");
    private final static String LOCAL_HOST_IP = "0:0:0:0:0:0:0:1";

    @Override
    public boolean postCheck(HttpServletRequest httpServletRequest, HandlerMethod handlerMethod) {
        return true;
    }

    @Override
    public boolean preCheck(HttpServletRequest request,HandlerMethod handlerMethod) {
        BaseRequest baseRequest = (BaseRequest)request.getAttribute(Global.BASE_REQUEST_ATTR_KEY);
        BaseRequestHeader baseHeader = baseRequest.getBaseRequestHeader();
        BaseResponse baseResponse = (BaseResponse)request.getAttribute(Global.BASE_RESPONSE_ATTR_KEY);
        if (StringUtils.isBlank(baseHeader.getClientId()) || StringUtils.isBlank(baseHeader.getClientTime())) {
            String _msg = "EMPTY clientId or clientTime";
            log.info(_msg);
            baseResponse.setResponseCodeMessage(ResultCode.ResponseCode.ILLEGAL_ARGS).concatInfo(_msg);
            return false;
        } else { //client验证主流程
            ClientCfgObj clientCfgObj = PropertiesHelper.getClientCfgObj(baseHeader.getClientId());
            boolean hasClient = Optional.ofNullable(clientCfgObj)
                    .map(c -> c.getClientId())
                    .map(clientId -> !clientId.isEmpty())
                    .orElse(false);
            log.debug("clientCfgObj:{}, hasClient:{}", clientCfgObj, hasClient);
            if (hasClient) {
                log.debug("Ready to check clientId");
                //验证sign
                if (clientCfgObj.getCheckSign()){
                    if (StringUtils.isEmpty(baseHeader.getClientSign())){
                        String _msg = "EMPTY s";
                        log.info(_msg);
                        baseResponse.setResponseCodeMessage(ResultCode.ResponseCode.ILLEGAL_ARGS).concatInfo(_msg);
                        return false;
                    } else{
                        if(checkClientSign(baseResponse,baseHeader.getClientId(),baseHeader.getClientTime(),baseRequest.getBodyString(),baseHeader.getClientSign(),clientCfgObj.getKey())){
                            return false;
                        }else ;
                    }
                } else {
                }
                //验证IP
                if(!checkIpList(baseResponse,baseHeader.getClientIp(),clientCfgObj)){
                    return false;
                } else {

                }
            } else {
                return ResponseUtil.foundClientId(baseHeader.getClientId(), baseResponse);
            }
        }
        return false;
    }

    private boolean checkClientSign(BaseResponse baseResponse,String clientId,String clientTime,String bodyString,String clientSign,@NonNull String clientKey){
        String verificationSign = DigestUtils.md5Hex(clientId + clientTime + bodyString + clientKey);
        if(verificationSign.equals(clientSign)){
            return true;
        }else{
            String _msg = String.format("Invalid sign:%s, should be:md5Hex(clientId + clientTime + bodyString + clientKey)=%s",clientSign, verificationSign);
            log.info(_msg);
            baseResponse.setResponseCodeMessage(ResultCode.ResponseCode.ILLEGAL_DATA).concatInfo("Invalid sign:" + clientSign);
            return false;
        }
    }

    private boolean checkIpList(BaseResponse baseResponse, String clientIp, ClientCfgObj clientCfgObj){
        //本地访问方式直接通过
        if(LOCAL_HOST_IP.equals(clientIp)){
            return true;
        }else;

        String[] clientIpCutArr = clientIp.split("\\.");
        //黑名单优先级高
        if(CollectionUtils.isNotEmpty(clientCfgObj.getIpBlackList())){
            for(String blackIp : clientCfgObj.getIpBlackList()){
                if(!IP_PATTERN.matcher(blackIp).find()){
                    String _msg = "wrong ip pattern str[" + blackIp + "] in clientCfg item[ipBlackList]";
                    log.error(_msg);
                    baseResponse.setResponseCodeMessage(ResultCode.ResponseCode.UNKNOWN_EXCEPTION).concatInfo(_msg);
                    return false;
                }else{
                    String[] ipPatternCutArr = blackIp.split("\\.");
                    for(int i = 0;i < 4;i ++){
                        if(!"*".equals(ipPatternCutArr[i]) && !clientIpCutArr[i].equals(ipPatternCutArr[i])){
                            //没匹配上，继续下一个黑名单IP
                            break;
                        }else {
                            //如果是第四位，说明匹配成功
                            if(i == 3){
                                String _msg = "ip [" + clientIp + "] is in the ipBlackList";
                                log.info(_msg);
                                baseResponse.setResponseCodeMessage(ResultCode.ResponseCode.SERVICE_NOT_AVAILABLE).concatInfo(_msg);
                                return false;
                            }else ;
                        }
                    }
                }
            }
        }else;
        //白名单处理
        if(!CollectionUtils.isEmpty(clientCfgObj.getIpWhiteList())){
            for(String whiteIp : clientCfgObj.getIpWhiteList()){
                if(!IP_PATTERN.matcher(whiteIp).find()){
                    String _msg = "wrong ip pattern str[" + whiteIp + "] in clientCfg item[ipWhiteList]";
                    log.error(_msg);
                    baseResponse.setResponseCodeMessage(ResultCode.ResponseCode.UNKNOWN_EXCEPTION).concatInfo(_msg);
                    return false;
                }else{
                    String[] ipPatternCutArr = whiteIp.split("\\.");
                    for(int i = 0;i < 4;i ++){
                        if(!"*".equals(ipPatternCutArr[i]) && !clientIpCutArr[i].equals(ipPatternCutArr[i])){
                            //没匹配上，继续下一个黑名单IP
                            break;
                        }else {
                            //如果是第四位，说明匹配成功
                            if(i == 3){
                                return true;
                            }else ;
                        }
                    }
                }
            }
            String _msg = "ip [" + clientIp + "] is not in the ipWhiteList";
            log.info(_msg);
            baseResponse.setResponseCodeMessage(ResultCode.ResponseCode.SERVICE_NOT_AVAILABLE).concatInfo(_msg);
            return false;
        }else ;
        return true;
    }
}
