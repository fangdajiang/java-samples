package com.xxdai.starter.core.util;

import com.xxdai.pub.common.model.BaseRequestHeader;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * Created by fangdajiang on 2017/6/1.
 */
@Slf4j
public class CheckerUtil {
    private boolean enabled = false;
    private static int CLIENT_TIME_TOLERANCE = 300000;
    private static int CLIENT_TIME_LENGTH = 13;

    public CheckerUtil(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * 暂时仅对 BaseRequestHeader.clientTime 进行检查，允许前后有一定范围的容错
     * @param baseRequestHeader
     */
    public void checkBaseRequestHeader(BaseRequestHeader baseRequestHeader) {
        if (enabled) {
            if (baseRequestHeader.getClientTime().length() == CLIENT_TIME_LENGTH &&
                    StringUtils.isNumeric(baseRequestHeader.getClientTime())) {
                Long clientTime = Long.valueOf(baseRequestHeader.getClientTime());
                Long currentTime = System.currentTimeMillis();
                Long currentTolerance = Math.abs(currentTime - clientTime);
                if (currentTolerance > CLIENT_TIME_TOLERANCE) { //clientTime 已超出有效时间范围
                    log.warn("currentTime:{}-{}, INVALID clientTime, cause the tolerance({}) of clientTime:{}-{}" +
                                    " exceeded the max value:{}",
                            currentTime, new Date(currentTime), currentTolerance, clientTime, new Date(clientTime),
                            CLIENT_TIME_TOLERANCE);
                    throw new IllegalArgumentException("当前系统时间:" + currentTime + "-" + new Date(currentTime) +
                            ", the tolerance(" + currentTolerance + ") of clientTime:" + clientTime + "-" +
                            new Date(clientTime) + " exceeded the max value:" + CLIENT_TIME_TOLERANCE);
                } else {};
            } else {
                throw new IllegalArgumentException("clientTime is illegal, length is not " + CLIENT_TIME_LENGTH +
                    " or not numeric:" + baseRequestHeader.getClientTime());
            }
        } else {
            log.info("CheckerUtil.enabled is false, stop to check BaseRequestHeader.");
        }
    }
}
