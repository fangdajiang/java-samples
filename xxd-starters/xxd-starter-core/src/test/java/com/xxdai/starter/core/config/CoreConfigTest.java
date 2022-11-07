package com.xxdai.starter.core.config;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

/**
 * 检查 stringRedisTemplate, redisTemplate, cacheManager 里的值
 *
 * Created by fangdajiang on 2017/8/29.
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class CoreConfigTest {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private CacheManager cacheManager;

    @Test
    public void test() throws Exception {
        log.debug("stringRedisTemplate:{}, redisTemplate:{}, cacheManager:{}", stringRedisTemplate, redisTemplate, cacheManager);

        long sleepSeconds = 1;
        String initKey = "aaa";
        String initValue = "111";
        stringRedisTemplate.opsForValue().set(initKey, initValue);
        TimeUnit.SECONDS.sleep(sleepSeconds);
        String aaaValue = stringRedisTemplate.opsForValue().get("aaa");
        log.debug("after {} seconds, key[{}]'s value:{}", sleepSeconds, initKey, aaaValue);
        assertTrue(initValue.equals(aaaValue));

        log.debug("cacheNames.size:{}", cacheManager.getCacheNames().size());
        for (String cacheName : cacheManager.getCacheNames()) {
            log.debug("cacheName:{}", cacheName);
        }

        log.debug("redisTemplate's keySerializer:{}, valueSerializer:{}, defaultSerializer:{}",
                redisTemplate.getKeySerializer(), redisTemplate.getValueSerializer(),
                redisTemplate.getDefaultSerializer());

        LettuceConnectionFactory lettuceConnectionFactory = (LettuceConnectionFactory)redisTemplate.getConnectionFactory();
        log.debug("lettuceConnectionFactory:{}", lettuceConnectionFactory);
        assertTrue(lettuceConnectionFactory != null);
        RedisStandaloneConfiguration redisStandaloneConfiguration = lettuceConnectionFactory.getStandaloneConfiguration();
        String hostName = redisStandaloneConfiguration.getHostName();
        int port = lettuceConnectionFactory.getPort();
        long timeout = lettuceConnectionFactory.getTimeout();
        long shutdownTimeout = lettuceConnectionFactory.getShutdownTimeout();
        LettucePoolingClientConfiguration lettucePoolingClientConfiguration = (LettucePoolingClientConfiguration)lettuceConnectionFactory.getClientConfiguration();
        int maxIdle = lettucePoolingClientConfiguration.getPoolConfig().getMaxIdle();
        int maxTotal = lettucePoolingClientConfiguration.getPoolConfig().getMaxTotal();
        long maxWaitMillis = lettucePoolingClientConfiguration.getPoolConfig().getMaxWaitMillis();
        log.debug("redisTemplate's hostName:{}, port:{}, timeout:{}, shutdownTimeout:{}, maxWaitMillis:{}, maxTotal:{}, maxIdle:{}",
                hostName, port, timeout, shutdownTimeout, maxWaitMillis, maxTotal, maxIdle);
    }

}