package com.xxdai.starter.cache.service;

import com.xxdai.starter.cache.model.AbstractStatusResolver;
import com.xxdai.pub.common.exception.DataNotFoundException;

/**
 *
 * @author fangdajiang
 * @date 2018/2/6
 */
public interface StatusService {
    AbstractStatusResolver getStatus(String requestId) throws DataNotFoundException;
    AbstractStatusResolver putStatus(AbstractStatusResolver abstractStatusResolver) throws DataNotFoundException;
    void clearStatus(String requestId) throws DataNotFoundException;
}
