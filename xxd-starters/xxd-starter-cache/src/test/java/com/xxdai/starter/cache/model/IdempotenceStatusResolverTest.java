package com.xxdai.starter.cache.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static com.xxdai.starter.cache.model.AbstractStatusResolver.EXPIRY_MILLIS_INTERVAL;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * created by xiguoding on 2018/3/23 下午3:37
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class IdempotenceStatusResolverTest {

    @Test
    public void idempotenceStatusResolver() {
        IdempotenceStatusResolver resolver = new IdempotenceStatusResolver();
        resolver.setUniqueRequestId("xgd");
        resolver.setRequestStatus(RequestStatus.FINISHED);
        assertTrue(resolver.proceed());

        resolver.setRequestStatus(RequestStatus.INITIATED);
        resolver.setCreateTime(System.currentTimeMillis());
        assertFalse(resolver.proceed());

        resolver.setCreateTime(System.currentTimeMillis() - EXPIRY_MILLIS_INTERVAL);
        assertFalse(resolver.proceed());

        resolver.setCreateTime(System.currentTimeMillis() - EXPIRY_MILLIS_INTERVAL - 1);
        assertTrue(resolver.proceed());
    }
}
