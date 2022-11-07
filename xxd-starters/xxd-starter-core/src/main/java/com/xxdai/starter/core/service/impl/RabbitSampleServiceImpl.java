package com.xxdai.starter.core.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.xxdai.starter.core.config.RabbitMqConfig;
import com.xxdai.starter.core.exception.RabbitSendException;
import com.xxdai.starter.core.mq.rabbit.RabbitMsgSender;
import com.xxdai.starter.core.service.RabbitSampleService;
import com.xxdai.starter.sample.model.DemoUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * For Demo User
 *
 * Created by fangdajiang on 2018/11/9.
 */
@Service
@Slf4j
public class RabbitSampleServiceImpl implements RabbitSampleService {
    private RabbitMsgSender rabbitMsgSender;

    public RabbitSampleServiceImpl(RabbitMsgSender rabbitMsgSender) {
        this.rabbitMsgSender = rabbitMsgSender;
    }

    /**
     * 对应 {@link com.xxdai.starter.core.mq.rabbit.RabbitSampleHandler#listenerAutoAck}
     *
     */
    public void sendAutoAckMessage(DemoUser user) {
        this.sendAckMessage(RabbitMqConfig.DEFAULT_DEMO_USER_QUEUE, user);
    }

    /**
     * 对应 {@link com.xxdai.starter.core.mq.rabbit.RabbitSampleHandler#listenerManualAck}
     *
     */
    public void sendManualAckMessage(DemoUser user) {
        this.sendAckMessage(RabbitMqConfig.MANUAL_DEMO_USER_QUEUE, user);
    }

    private void sendAckMessage(String queue, DemoUser user) {
        JSONObject userJson = (JSONObject)JSONObject.toJSON(user);
        try {
            this.rabbitMsgSender.sendMessage(queue, userJson);
        } catch (RabbitSendException e) {
            log.warn("queue:{}, user:{}", queue, user, e);
        }
    }

}
