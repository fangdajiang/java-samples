package com.xxdai.starter.cache.config;

import com.xxdai.starter.cache.config.property.RedisExpireProperties;
import com.xxdai.starter.cache.util.CacheUtil;
import com.xxdai.pub.common.annotation.CacheKey;
import com.xxdai.pub.common.annotation.XxdCachePut;
import com.xxdai.pub.common.annotation.XxdCacheable;
import com.xxdai.pub.common.model.ExpiryTime;
import com.xxdai.pub.common.model.KeyMode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author fangdajiang
 * @date 2017/5/18
 */
@Aspect
@Component
@Slf4j
@ConditionalOnBean(RedisExpireProperties.class)
public class CacheAspect {
    private final static int CACHE_PUT_METHOD_ARGS_COUNT = 2;

    @SuppressWarnings("rawtypes")
    @Autowired
    private RedisTemplate redisTemplate;

    @Around("@annotation(put)")
    public Object xxdCachePut(final ProceedingJoinPoint pjp, XxdCachePut put) throws Throwable {
        log.debug("pjp:{}, put.key():{}, put.value():{}, put.expiry().getTime():{}", pjp, put.key(), put.value(), put.expiry().getTime());
        if (pjp.getArgs().length != CACHE_PUT_METHOD_ARGS_COUNT) {
            throw new IllegalArgumentException("count of cachePut method's args should be:" + CACHE_PUT_METHOD_ARGS_COUNT);
        }
        for (int i = 0; i < pjp.getArgs().length; i++) {
            log.debug("pjp.getArgs({}):{}", i, pjp.getArgs()[i]);
        }
        String key = getCacheKey(pjp, put.value(), put.key(), put.keyMode());
        log.debug("key result:{}", key);
        @SuppressWarnings("unchecked")
        ValueOperations<String, Object> valueOper = redisTemplate.opsForValue();
        if (put.expiry().getTime() <= 0) {
            log.debug("put.expiry().getTime() <= 0");
            valueOper.set(key, pjp.getArgs()[1]);
        } else {
            log.debug("put.expiry().getTime():{}", put.expiry().getTime());
            valueOper.set(key, pjp.getArgs()[1], put.expiry().getTime(), TimeUnit.SECONDS);
        }
        return pjp.getArgs()[1];
    }

    @Around("@annotation(cache)")
    public Object xxdCacheable(final ProceedingJoinPoint pjp, XxdCacheable cache) throws Throwable {
        log.debug("pjp:{}, cache.key():{}, cache.value():{}", pjp, cache.key(), cache.value());
        log.debug("pjp.getArgs().length:{}", pjp.getArgs().length);
        String key = getCacheKey(pjp, cache.value(), cache.key(), cache.keyMode());
        log.debug("key result:{}", key);
//       // ??????????????????????????????????????????????????????
//       Object value = CacheUtils.getObj(key);// ?????????????????????
//       if (value != null) {
//       return value; // ???????????????, ???????????????
//       }
//       value = pjp.proceed(); // ??????, ?????????????????????
//       if (value != null) {
//       CacheUtils.set(key, value, cache.expire());
//       }

        // ?????????????????? redisTemplate ????????????
        @SuppressWarnings("unchecked")
        ValueOperations<String, Object> valueOper = redisTemplate.opsForValue();
        Object value =  valueOper.get(key); // ?????????????????????
        log.debug("value:{}", value);
        if (value != null) {
            return value; //???????????????, ???????????????
        } else {
            value = pjp.proceed(); //????????????,?????????????????????
            log.debug("value2:{}", value);
            CacheUtil.set(key, value, ExpiryTime.NONE);
/* ??? CacheUtil.set ??????
            if (cache.expiry().getTime() <= 0) { // ??????????????????????????????, ??????????????????
                valueOper.set(key, value);
            } else { // ????????????????????????
                valueOper.set(key, value, cache.expiry().getTime(), TimeUnit.SECONDS);
            }
*/
            return value;
        }
    }

    /**
     * ??????????????? key ???
     * @param pjp
     * @param key
     * @param keyMode
     * @return
     */
    private String getCacheKey(ProceedingJoinPoint pjp, String[] cacheName, String key, KeyMode keyMode) {

        StringBuilder buf=new StringBuilder();
        if(cacheName.length > 0) {
            buf.append(cacheName[0]);
            for (int i = 1; i < cacheName.length; i++) {
                if(StringUtils.isNotEmpty(cacheName[i])) {
                    buf.append(".").append(cacheName[i]);
                }
            }
        }
        if(StringUtils.isNotEmpty(key)) {
            buf.append(".").append(key);
        }
        log.debug("buf:{}", buf.toString());

        Object[] args=pjp.getArgs();
        if(keyMode== KeyMode.DEFAULT) {
            log.debug("KeyMode is DEFAULT");
            Annotation[][] pas=((MethodSignature)pjp.getSignature()).getMethod().getParameterAnnotations();
            log.debug("pas.length:{}", pas.length);
            for(int i=0;i<pas.length;i++) {
                log.debug("1st for i:{}", i);
                for(Annotation an:pas[i]) {
                    log.debug("2nd for i:{}", i);
                    if(an instanceof CacheKey) {
                        buf.append(".").append(args[i].toString());
                        log.debug("buf appending:{}", buf.toString());
                        break;
                    }
                }
            }
        } else if(keyMode==KeyMode.BASIC) {
            log.debug("KeyMode is BASIC");
            for(Object arg:args) {
                if(arg instanceof String) {
                    buf.append(".").append(arg);
                } else if(arg instanceof Integer || arg instanceof Long || arg instanceof Short) {
                    buf.append(".").append(arg.toString());
                } else if(arg instanceof Boolean) {
                    buf.append(".").append(arg.toString());
                }
            }
        } else if(keyMode==KeyMode.ALL) {
            log.debug("KeyMode is ALL");
            for(Object arg:args) {
                buf.append(".").append(arg.toString());
            }
        }

        return buf.toString();
    }
}
