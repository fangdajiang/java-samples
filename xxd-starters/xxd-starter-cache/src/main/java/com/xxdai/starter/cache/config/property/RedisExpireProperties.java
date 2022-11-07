package com.xxdai.starter.cache.config.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * Created by yq on 2017/3/24.
 */
@Data
@ConfigurationProperties(prefix = "spring.redis")
public class RedisExpireProperties {
    /**
     * 自定义 Redis 过期配置值
     */
    private Map<String,Long> expireMap;
}
