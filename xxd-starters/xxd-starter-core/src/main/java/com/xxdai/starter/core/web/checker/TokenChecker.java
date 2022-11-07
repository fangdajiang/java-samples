package com.xxdai.starter.core.web.checker;

import com.xxdai.starter.cache.model.UserCache;
import com.xxdai.starter.core.service.CommonTokenService;
import com.xxdai.starter.core.util.helper.UserCacheHelper;
import com.xxdai.pub.accountclient.util.TerminalUtil;
import com.xxdai.pub.constant.Global;
import com.xxdai.pub.constant.ResultCode;
import com.xxdai.pub.common.exception.DataNotFoundException;
import com.xxdai.pub.common.exception.TokenExpiredException;
import com.xxdai.pub.common.model.BaseRequest;
import com.xxdai.pub.common.model.BaseRequestHeader;
import com.xxdai.pub.common.model.BaseResponse;
import com.xxdai.pub.common.model.TokenEntity;
import com.xxdai.pub.common.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * 对请求的token进行验证，并将验证通过的userId放入Attribute
 * 需要配置requestHeader的token参数（方便swagger测试），用以接收token和验证；以及requestAttribute的userId参数(建议配置上ApiIgnore以在swagger中隐藏)，用以接收token验证通过之后转换成的userId
 *              如果验证没有通过，会中断所有后续验证处理，并直接返回错误信息
 * Created by yq on 2017/3/15.
 */
@Component @Slf4j
public class TokenChecker implements CommonChecker {
    protected transient final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private CommonTokenService commonTokenService;

    private UserCache getCustomizedUserCache(TokenEntity tokenEntity) {
        UserCache returnValue;
        Global.TerminalType terminalType = TerminalUtil.getTerminalType(tokenEntity.getUserAgent());
        switch (terminalType) {
            case APP:
                log.debug("an APP request, terminalType:{}", terminalType);
                returnValue = UserCacheHelper.APP.get(terminalType.name(), tokenEntity.getUserId());
                break;
            default:
                returnValue = UserCacheHelper.NON_APP.get(terminalType.name(), tokenEntity.getUserId());
        }
        return returnValue;
    }

    @Override
    /**
     * 进行token验证，并将验证通过的userId写入attribute
     * 因为token属于敏感数据，为避免因整体请求失败造成回写给response，所以在验证通过后将token抹去
     */
    public boolean preCheck(HttpServletRequest request,HandlerMethod handlerMethod) {
        BaseRequestHeader baseHeader = ((BaseRequest)request.getAttribute(Global.BASE_REQUEST_ATTR_KEY)).getBaseRequestHeader();
        BaseResponse baseResponse = (BaseResponse)request.getAttribute(Global.BASE_RESPONSE_ATTR_KEY);

        if (StringUtils.isBlank(baseHeader.getToken())) {
            baseResponse.setResponseCodeMessage(ResultCode.ResponseCode.EMPTY_TOKEN).concatInfo("EMPTY token");
            return false;
        }else{
            try{
                if (commonTokenService == null) {
                    log.warn("Spring didn't create a commonTokenService bean.");
                    commonTokenService = SpringUtil.getBean(CommonTokenService.class);
                }
                TokenEntity validatedTokenEntity = commonTokenService.validateTokenByUserAgent(baseHeader.getToken(), baseHeader.getUserAgent());
                log.debug("user agent:{}, validatedTokenEntity:{}", baseHeader.getUserAgent(), validatedTokenEntity);

                validatedTokenEntity.setUserAgent(baseHeader.getUserAgent());
                UserCache userCache = getCustomizedUserCache(validatedTokenEntity);
                log.debug("customized userCache:{}", userCache);

                TokenEntity tokenEntityInCache = userCache.getTokenEntity();
                if (tokenEntityInCache == null) {
                    baseResponse.setResponseCodeMessage(ResultCode.ResponseCode.UNKNOWN_EXCEPTION).concatInfo("tokenEntity NOT FOUND in UserCache");
                    return false;
                }
                String tokenInCache = tokenEntityInCache.getToken();
                //比较客户端传来的 token 字符串与缓存中存储的，包括：
                //1，缓存中是否含有 token 字符串
                //2，客户端的 token 字符串是否与缓存中的一致
                //3，客户端的 token 中的 userId 是否与 userCache 中的 userId 一致（很可能不用作此比较）
                if (tokenInCache == null || !baseHeader.getToken().equals(tokenInCache) ||
                        validatedTokenEntity.getUserId() != userCache.getUserId().longValue()) {
                    logger.warn("MISMATCHED token, client token:{}, token in cache:{}, tokenEntity:{}, userCache:{}",
                            baseHeader.getToken(), tokenInCache, tokenEntityInCache, userCache);
                    baseResponse.setResponseCodeMessage(ResultCode.ResponseCode.MISMATCHED_DATA_TOKEN).concatInfo("MISMATCHED token:" + tokenInCache);
                    return false;
                }
                request.setAttribute(Global.USER_ID_ATTR_KEY,validatedTokenEntity.getUserId());
                return true;
            } catch (IllegalAccessException e) { //解析异常
                baseResponse.setResponseCodeMessage(ResultCode.ResponseCode.PARSE_TOKEN_ERROR).concatInfo(e.getMessage());
                return false;
            } catch (TokenExpiredException e) { //token 中的失效时间到期
                baseResponse.setResponseCodeMessage(ResultCode.ResponseCode.EXPIRED_TOKEN).concatInfo(e.getMessage());
                return false;
            } catch (DataNotFoundException e) { //缓存中的 user 对象已被删除
                baseResponse.setResponseCodeMessage(ResultCode.ResponseCode.NOT_FOUND_TOKEN).concatInfo(e.getMessage());
                return false;
            } catch (Exception e) {
                logger.warn("baseHeader:{}, baseResponse:{}", baseHeader, baseResponse, e);
                baseResponse.setResponseCodeMessage(ResultCode.ResponseCode.UNKNOWN_EXCEPTION).concatInfo(e.getMessage());
                return false;
            } finally {
                //为避免因整体请求失败造成回写给response，所以在验证通过后将token抹去
                //baseHeader.setToken(null);
            }
        }
    }

    @Override
    public boolean postCheck(HttpServletRequest httpServletRequest, HandlerMethod handlerMethod) {
        return true;
    }
}
