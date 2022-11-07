package com.xxdai.starter.core.service;

import com.alibaba.fastjson.JSON;
import com.xxdai.starter.cache.model.UserCache;
import com.xxdai.pub.common.exception.CipherException;
import com.xxdai.pub.common.exception.DataNotFoundException;
import com.xxdai.pub.common.exception.TokenExpiredException;
import com.xxdai.pub.common.model.TokenEntity;
import com.xxdai.pub.common.util.cipher.CryptoUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 本类拷贝自 UserCenter，仅供测试 token 之用
 *
 * @author fangdajiang
 * @date 2017/4/20
 */
@Slf4j
@Service
public class TokenService {

    @Cacheable(value = "user-cache-24hrs", key = "#terminalTypeName + #userId.toString()")
    public UserCache getUserCache(String terminalTypeName, Long userId) {
        throw new DataNotFoundException("terminalTypeName:" + terminalTypeName + ", userId:" + userId);
    }
    @Cacheable(value = "user-cache-app", key = "#terminalTypeName + #userId.toString()")
    public UserCache getUserCacheForApp(String terminalTypeName, Long userId) {
        throw new DataNotFoundException("FAILED to fetch long-time data from redis for APP, terminalTypeName:" +
                terminalTypeName + ", userId:" + userId);
    }
    @Cacheable(value = "user-cache-24hrs", key = "#userCache.terminalTypeName + #userCache.userId.toString()")
    public UserCache putUserCache(UserCache userCache) {
        return userCache;
    }
    @Cacheable(value = "user-cache-app", key = "#userCache.terminalTypeName + #userCache.userId.toString()")
    public UserCache putUserCacheForApp(UserCache userCache) {
        return userCache;
    }
    @CacheEvict(value="user-cache-24hrs",key="#terminalTypeName + #userId.toString()")
    public void clearUserCache(String terminalTypeName, Long userId) {
        log.debug("从redis删除用户登录信息(non-APP)，terminalTypeName:{}, userId:{}", terminalTypeName, userId);
    }
    @CacheEvict(value="user-cache-app",key="#terminalTypeName + #userId.toString()")
    public void clearUserCacheForApp(String terminalTypeName, Long userId) {
        log.debug("从redis删除用户登录信息(APP)，terminalTypeName:{}, userId:{}", terminalTypeName, userId);
    }

    /**
     * 调用 validateToken 方法实现
     */
    public final TokenEntity getTokenEntity(String clientToken, String newUserAgent) throws IllegalAccessException {
        return validateToken(clientToken, newUserAgent);
    }

    /**
     * 根据客户端传来的 token 和新的 userAgent 将 token 解密，验证其中的 expiryTime 是否过期。
     * 还可以考虑比较缓存中的 userAgent 和新的 userAgent 来进行判定。
     * @return 仅包含 userId, 过期日期，创建时间的 TokenEntity
     */
    protected TokenEntity validateToken(String clientToken, String newUserAgent) throws IllegalAccessException {
        String decryptedTxt;
        try {
            decryptedTxt = CryptoUtil.aesDecryptFromBase64(clientToken, newUserAgent);
        } catch (CipherException e) {
            log.warn("ERROR token:{} or user agent:{}", clientToken, newUserAgent, e);
            throw new IllegalAccessException("解析token时发生异常，token:" + clientToken +
                    ",agent:" + newUserAgent);
        }
        log.debug("decrypted txt:{}", decryptedTxt);
        TokenEntity tokenEntity = JSON.parseObject(decryptedTxt, TokenEntity.class);
        if (tokenEntity.getExpiryTime() < System.currentTimeMillis()) {
            throw new TokenExpiredException("invalid expiry time:" + tokenEntity.getExpiryTime() + "(" +
                    new Date(tokenEntity.getExpiryTime()) + ")");
        }
        return tokenEntity;
    }

    /**
     * 直接调用 validateToken()
     */
    protected void validateDigest(String tokenInCache, String newUserAgent) throws IllegalAccessException {
        try {
            validateToken(tokenInCache, newUserAgent);
        } catch (TokenExpiredException | DataNotFoundException e) { //token 已过期 或 cache 中的 token 已被删除，但由于是验证 digest 动作，不在乎。只要不是解密错误即可。
            log.info("token expired or be deleted:{}, user agent:{}", tokenInCache, newUserAgent);
        }
    }
}
