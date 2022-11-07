package com.xxdai.starter.core.service;

import com.google.common.collect.Maps;
import com.xxdai.pub.accountclient.util.TerminalUtil;
import com.xxdai.pub.common.exception.DataNotFoundException;
import com.xxdai.pub.common.model.BaseRequest;
import com.xxdai.pub.common.model.BaseRequestHeader;
import com.xxdai.pub.common.model.TokenEntity;
import com.xxdai.pub.constant.Global;
import com.xxdai.starter.cache.model.UserCache;
import com.xxdai.starter.core.service.impl.UserTokenServiceImpl;
import com.xxdai.starter.core.util.helper.UserCacheHelper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.junit.Assert.assertNotNull;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
@EnableCaching
public class TokenServiceTest {
    private static String iosUserAgent = "Mozilla/5.0 (iPhone; CPU iPhone OS 11_4 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15F79 Xxd/iOS_ELoan_1.0";
    private String macUserAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36";
    private static long userId = 206951L;

    @Autowired
    private CommonTokenService tokenService;
    @Autowired
    private UserTokenServiceImpl userTokenServiceImpl;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private CacheManager cacheManager;

    private RestTemplate restTemplate = new RestTemplate();

    @Test
    public void testRedisCacheManager() {
        RedisCacheManager redisCacheManager = (RedisCacheManager)cacheManager;
        log.debug("redisCacheManager:{}", redisCacheManager);
        Map<String, RedisCacheConfiguration> redisCacheConfigurationMap = redisCacheManager.getCacheConfigurations();
        for (Map.Entry<String, RedisCacheConfiguration> entry : redisCacheConfigurationMap.entrySet()) {
            RedisCacheConfiguration redisCacheConfiguration = entry.getValue();
            log.debug("entry.key:{}, value:{}", entry.getKey(), redisCacheConfiguration);
            RedisSerializationContext.SerializationPair<String> serializationPairKey = redisCacheConfiguration.getKeySerializationPair();
            log.debug("serializationPairKey:{}", serializationPairKey);
            RedisSerializationContext.SerializationPair<Object> serializationPairValue = redisCacheConfiguration.getValueSerializationPair();
            log.debug("serializationPairValue:{}", serializationPairValue);
        }
    }

    @Test
    public void testRedisTemplate() {
        log.debug("key serializer:{}, hash key serializer:{}, value serializer:{}, hash value serializer:{}"
                , redisTemplate.getKeySerializer(), redisTemplate.getHashKeySerializer()
                , redisTemplate.getValueSerializer(), redisTemplate.getHashValueSerializer());
    }

    @Test
    public void testAutowired() throws Exception {
        assertNotNull(tokenService);
    }

    @Test
    public void testUserCache() throws Exception {
        assertNotNull(tokenService.getUserCache(TerminalUtil.getTerminalType(iosUserAgent).name(), userId));
    }

    @Test
    public void validateTokenByUserAgent() throws IllegalAccessException {
        String token = "zfrKDUZXgoIgfilBtzUnu3qkpjopyJnjibjys3gKDy7XPtAO1jhqJ8j7DJf8y-gmCVmIjLjtfA5HJEI6xg0FJd0FLavj04CJ06MkYtdTWkE";
        String ua = macUserAgent;
        TokenEntity validatedTokenEntity = tokenService.validateTokenByUserAgent(token, ua);
        log.debug("validatedTokenEntity:{}", validatedTokenEntity);
    }

    @Test(expected = DataNotFoundException.class)
    public void testClearUserCache() throws Exception {
        String ua = macUserAgent;
        String terminalTypeName = TerminalUtil.getTerminalType(ua).name();
        log.debug("terminalTypeName:{}", terminalTypeName);
        UserCache userCache;
        if (Global.TerminalType.APP.name().equals(terminalTypeName)) {
            userCache = UserCacheHelper.APP.get(terminalTypeName, userId);
            log.debug("app userCache:{}", userCache);
            UserCacheHelper.APP.clear(terminalTypeName, userId);
            UserCacheHelper.APP.get(terminalTypeName, userId);
        } else {
            userCache = UserCacheHelper.NON_APP.get(terminalTypeName, userId);
            log.debug("non-app userCache:{}", userCache);
            UserCacheHelper.NON_APP.clear(terminalTypeName, userId);
            UserCacheHelper.NON_APP.get(terminalTypeName, userId);
        }
    }

    @Test
    public void testGetUserCache() throws Exception {
        String ua = iosUserAgent;
        String terminalTypeName = TerminalUtil.getTerminalType(ua).name();
        log.debug("terminalTypeName:{}", terminalTypeName);
        UserCache userCache;
        if (Global.TerminalType.APP.name().equals(terminalTypeName)) {
            userCache = UserCacheHelper.APP.get(terminalTypeName, userId);
        } else {
            userCache = UserCacheHelper.NON_APP.get(terminalTypeName, userId);
        }
        log.debug("userCache:{}", userCache);
    }

    @Test
    public void testCheckToken() throws Exception {
        String ua = iosUserAgent;
        BaseRequestHeader baseRequestHeader = new BaseRequestHeader();
        baseRequestHeader.setUserAgent(ua);
        BaseRequest baseRequest = new BaseRequest();
        baseRequest.setBaseRequestHeader(baseRequestHeader);
        String terminalTypeName = TerminalUtil.getTerminalType(baseRequest.getBaseRequestHeader().getUserAgent()).name();
        log.debug("terminalTypeName:{}", terminalTypeName);
        String token = createTokenAndPut(baseRequest, terminalTypeName);
        TokenEntity validatedTokenEntity = tokenService.validateTokenByUserAgent(token, ua);
        log.debug("validatedTokenEntity:{}", validatedTokenEntity);
        UserCache userCache;
        if (Global.TerminalType.APP.name().equals(terminalTypeName)) {
            userCache = UserCacheHelper.APP.get(terminalTypeName, userId);
        } else {
            userCache = UserCacheHelper.NON_APP.get(terminalTypeName, userId);
        }
        log.debug("userCache from Cache:{}", userCache);
        if(validatedTokenEntity.getUserId() != userCache.getUserId() ||
                !token.equals(userCache.getTokenEntity().getToken())){ //非法 token
            log.warn("Illegal clientToken:{}, validatedTokenEntity:{}, userCache:{}", token, validatedTokenEntity, userCache);
            throw new IllegalStateException("非法的token:" + token); //最好改为 IllegalAccessException
        } else {
            log.info("token is legal:{}, terminalTypeName:{}, userId:{}", token, terminalTypeName, userId);

            String userInfoUrl = "http://dev.xxd.com/userCenter/user/token/getUserInfoByToken";
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
            headers.add(HttpHeaders.USER_AGENT, ua);
            headers.add("clientId", "XXD_IOS_LENDING");
            headers.add("clientTime", String.valueOf(System.currentTimeMillis()));
            headers.add("s", "sss");
            headers.add("token", token);
            log.debug("headers:{}", headers);
            ResponseEntity<String> tokenResponseEntity = restTemplate.exchange(userInfoUrl,
                    HttpMethod.GET, new HttpEntity<>(headers), String.class, Maps.newHashMap());
            log.debug("tokenResponseEntity:{}", tokenResponseEntity);
        }
    }

    private String createTokenAndPut(BaseRequest baseRequest, String terminalTypeName) {
        long expiryTime;
        if (Global.TerminalType.APP.name().equals(terminalTypeName)) {
            expiryTime = 2592000000L;
        } else {
            expiryTime = 86400000L;
        }
        TokenEntity tokenEntity = userTokenServiceImpl.createToken(baseRequest, userId, expiryTime);
        log.debug("expiryTime:{}, tokenEntity:{}", expiryTime, tokenEntity);

        String username = "usernamefortest";
        String nickname = "nicknamefortest";
        String mobile = "mobilefortest";
        String resource = "resourcefortest";
        String referer = "refererfortest";
        UserCache userCache = new UserCache(tokenEntity, userId, username, nickname, mobile, resource, referer,
                new java.util.Date(), terminalTypeName);
        log.debug("userCache created:{}", userCache);
        if (Global.TerminalType.APP.name().equals(terminalTypeName)) {
            UserCacheHelper.APP.clear(terminalTypeName, userCache.getUserId());
            UserCacheHelper.APP.put(userCache);
        } else {
            UserCacheHelper.NON_APP.clear(terminalTypeName, userCache.getUserId());
            UserCacheHelper.NON_APP.put(userCache);
        }

        return tokenEntity.getToken();
    }
}