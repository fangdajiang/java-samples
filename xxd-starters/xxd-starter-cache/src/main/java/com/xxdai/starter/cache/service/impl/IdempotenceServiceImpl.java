package com.xxdai.starter.cache.service.impl;

import com.xxdai.pub.common.exception.DataNotFoundException;
import com.xxdai.starter.cache.model.AbstractStatusResolver;
import com.xxdai.starter.cache.model.IdempotenceStatusResolver;
import com.xxdai.starter.cache.service.StatusService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

/**
 * Created by fangdajiang on 2017/6/15.
 */
@Slf4j
@Component
public class IdempotenceServiceImpl implements StatusService {

    @Override
    @Cacheable(value = "idempotency-cache", key = "#requestId")
    public IdempotenceStatusResolver getStatus(String requestId) throws DataNotFoundException {
        throw new DataNotFoundException("requestId:" + requestId);
    }

    @Override
    @CachePut(value = "idempotency-cache", key = "#idempotencyStatus.uniqueRequestId")
    public AbstractStatusResolver putStatus(AbstractStatusResolver statusResolver) throws DataNotFoundException {
        return statusResolver;
    }

    @Override
    @CacheEvict(value = "idempotency-cache", key = "#requestId")
    public void clearStatus(String requestId) throws DataNotFoundException {
        log.debug("从 cache 删除幂等性信息，requestId:{}", requestId);
    }

}
