package com.xxdai.starter.cache.config.property;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * created by xiguoding on 2018/3/13 下午2:41
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class RedisExpirePropertiesTest {
    @Autowired
    RedisExpireProperties redisExpireProperties;

    private HashMap<String, Long> expiredMap() {
        HashMap<String, Long> map = new HashMap<>();
        map.put("user-cache-24hrs", 86400L);
        map.put("user-cache-app", 315360000L);
        return map;
    }

    @Test
    public void testAutowired() {
        assertNotNull(redisExpireProperties);
    }

    @Test
    public void redisExpireProperties() {
        assertEquals(expiredMap(), redisExpireProperties.getExpireMap());
        redisExpireProperties.setExpireMap(null);
        assertNull(redisExpireProperties.getExpireMap());
    }
}
