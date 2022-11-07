package com.xxdai.starter.core.mq.redis;

import com.xxdai.starter.cache.config.property.RedisExpireProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.io.Serializable;

@Component
@ConditionalOnBean(RedisExpireProperties.class)
public class SendMessage {
    @Resource(name="redisTemplate")
    private RedisTemplate<String, Object> redisTemplate;

    public void sendMessage(String channel, Serializable message) {
        redisTemplate.convertAndSend(channel, message);
    }
}