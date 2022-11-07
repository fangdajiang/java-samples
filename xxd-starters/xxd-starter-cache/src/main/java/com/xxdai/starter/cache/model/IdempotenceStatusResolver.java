package com.xxdai.starter.cache.model;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 *
 * @author fangdajiang
 * @date 2017/6/14
 */
@Slf4j
@NoArgsConstructor
public class IdempotenceStatusResolver extends AbstractStatusResolver implements Serializable {
    public IdempotenceStatusResolver(String uniqueRequestId, RequestStatus requestStatus) {
        super(uniqueRequestId, requestStatus);
    }

    @Override
    public boolean proceed() {
        //状态值为 INITIATED，表明前一次请求未结束
        if (!requestStatus.isFinished()) {
            long interval = System.currentTimeMillis() - this.createTime;
            if (interval > EXPIRY_MILLIS_INTERVAL) {
                log.debug("前一次请求已过期({} > {} ms)，故接受本次请求", interval, EXPIRY_MILLIS_INTERVAL);
                return true;
            } else {
                log.debug("前一次请求未过期({} < {} ms)，故不接受本次请求", interval, EXPIRY_MILLIS_INTERVAL);
                return false;
            }
        } else { //状态值为 FINISHED，表明前一次请求已结束
            return true;
        }
    }
}

