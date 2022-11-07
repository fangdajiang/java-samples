package com.xxdai.starter.core.service.impl;

import com.alibaba.fastjson.JSON;
import com.xxdai.pub.common.exception.CipherException;
import com.xxdai.pub.common.model.BaseRequest;
import com.xxdai.pub.common.model.TokenEntity;
import com.xxdai.pub.common.util.cipher.CryptoUtil;
import com.xxdai.pub.common.util.cipher.DigestUtil;
import com.xxdai.starter.core.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 本类拷贝自 UserCenter，仅供测试 token 之用
 */
@Service
@Slf4j
public class UserTokenServiceImpl extends TokenService {

/*
    public TokenEntity validateTokenByUserAgent(String clientToken, String newUserAgent) throws IllegalAccessException,TokenExpiredException {
        return super.validateToken(clientToken, newUserAgent);
    }
*/

/*
    @Cacheable(value = "user-cache-24hrs", key = "#terminalTypeName + #userId.toString()")
    public UserCache getUserFromCache(String terminalTypeName, Long userId) {
        return super.getUserCache(terminalTypeName, userId);
    }

    */
/**
     * 将 user 放入缓存，以 终端类型+userId 作为 key
     * @param userCache
     * @return
     *//*

    @Cacheable(value = "user-cache-24hrs", key = "#userCache.terminalTypeName + #userCache.userId.toString()")
    @CheckReturnValue
    public UserCache putUserInCache(UserCache userCache) {return userCache;}
*/

    /**
     * 令牌包含 userId, createTime, expiryTime。
     * User Agent 是加密 token 的密钥，digest 采用 SHA-512 算法生成
     * @param baseRequest
     * @param userId
     * @return 新的 tokenEntity
     */
    public TokenEntity createToken(BaseRequest baseRequest, long userId, long expiryTime) {
        //初始化 tokenEntity
        TokenEntity newTokenEntity = new TokenEntity(userId, expiryTime);
        newTokenEntity.setCreateTime(System.currentTimeMillis());
        log.debug("after setting create time:{}", newTokenEntity.getCreateTime());
        //生成 token
        String jsonTokenWithoutUserAgent = JSON.toJSONString(newTokenEntity);
        log.debug("jsonTokenWithoutUserAgent:{}", jsonTokenWithoutUserAgent);
        String encryptedTxt = null;
        try {
            encryptedTxt = CryptoUtil.aesEncryptToBase64(jsonTokenWithoutUserAgent, baseRequest.getBaseRequestHeader().getUserAgent());
        } catch (CipherException e) {
            log.warn("jsonTokenWithoutUserAgent:{}, userAgent:{}", jsonTokenWithoutUserAgent, baseRequest.getBaseRequestHeader().getUserAgent(), e);
        }
        newTokenEntity.setToken(encryptedTxt);
        log.debug("after generating token:{}", newTokenEntity.getToken());
        //生成 digest
        try {
            String sha512 = DigestUtil.sha512ToHex(String.valueOf(newTokenEntity.getUserId() + newTokenEntity.getCreateTime()));
            newTokenEntity.setDigest(sha512);
        } catch (CipherException ce) {
            log.warn("userId:{}", newTokenEntity.getUserId(), ce.toString()); //ignore
        }
        log.debug("after generating digest:{}", newTokenEntity.getDigest());
        //填入 userAgent
        newTokenEntity.setUserAgent(baseRequest.getBaseRequestHeader().getUserAgent());
        return newTokenEntity;
    }

/*
    public TokenEntity createAppToken(BaseRequest baseRequest, long userId) {
        //初始化 tokenEntity
        TokenEntity newTokenEntity = new TokenEntity(userId, 2592000000L);
        newTokenEntity.setCreateTime(System.currentTimeMillis());
        log.debug("after setting create time:{}", newTokenEntity.getCreateTime());
        //生成 token
        String jsonTokenWithoutUserAgent = JSON.toJSONString(newTokenEntity);
        log.debug("jsonTokenWithoutUserAgent:{}", jsonTokenWithoutUserAgent);
        String encryptedTxt = null;
        try {
            encryptedTxt = CryptoUtil.aesEncryptToBase64(jsonTokenWithoutUserAgent, baseRequest.getBaseRequestHeader().getUserAgent());
        } catch (CipherException e) {
            log.warn("jsonTokenWithoutUserAgent:{}, userAgent:{}", jsonTokenWithoutUserAgent, baseRequest.getBaseRequestHeader().getUserAgent(), e);
        }
        newTokenEntity.setToken(encryptedTxt);
        log.debug("after generating token:{}", newTokenEntity.getToken());
        //生成 digest
        try {
            String sha512 = DigestUtil.sha512ToHex(String.valueOf(newTokenEntity.getUserId() + newTokenEntity.getCreateTime()));
            newTokenEntity.setDigest(sha512);
        } catch (CipherException ce) {
            log.warn("userId:{}", newTokenEntity.getUserId(), ce.toString()); //ignore
        }
        log.debug("after generating digest:{}", newTokenEntity.getDigest());
        //填入 userAgent
        newTokenEntity.setUserAgent(baseRequest.getBaseRequestHeader().getUserAgent());
        return newTokenEntity;
    }
*/

/*
    @CacheEvict(value="user-cache-24hrs",key="#terminalTypeName + #userId.toString()")
    public void clearUserFromCache(String terminalTypeName, Long userId) {
        log.debug("从redis删除用户登录信息，terminalTypeName:{}, userId:{}", terminalTypeName, userId);
    }
*/

}
