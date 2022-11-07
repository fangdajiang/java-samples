package com.xxdai.starter.cache.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author fangdajiang
 * @date 2018/2/6
 */
@Data
@NoArgsConstructor
public abstract class AbstractStatusResolver {
    protected String uniqueRequestId;
    protected RequestStatus requestStatus;
    protected long createTime;
    /**
     * 5分钟
     */
    protected final static long EXPIRY_MILLIS_INTERVAL = 300000;

    public AbstractStatusResolver(String uniqueRequestId, RequestStatus requestStatus) {
        this.uniqueRequestId = uniqueRequestId;
        this.requestStatus = requestStatus;
        this.createTime = System.currentTimeMillis();
    }

    public abstract boolean proceed();

}
