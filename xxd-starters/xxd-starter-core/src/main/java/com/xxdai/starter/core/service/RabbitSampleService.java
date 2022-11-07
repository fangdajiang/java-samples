package com.xxdai.starter.core.service;

import com.xxdai.starter.sample.model.DemoUser;

/**
 *
 * Created by fangdajiang on 2018/11/9.
 */
public interface RabbitSampleService {
    void sendAutoAckMessage(DemoUser user);
    void sendManualAckMessage(DemoUser user);
}
