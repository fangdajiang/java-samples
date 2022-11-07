package com.xxdai.starter.core.mq;

/**
 * Created by fangdajiang on 2017/2/28.
 */
public interface DistributedLockCallback {
    /**
     * 回调接口
     * @return 业务逻辑的结果
     */
    Object execute();
}
