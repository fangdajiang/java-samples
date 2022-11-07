package com.xxdai.starter.cache.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xxdai.pub.common.util.SpringUtil;
import com.xxdai.starter.cache.config.property.RedisExpireProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Redis 缓存配置
 * refer: https://blog.csdn.net/guokezhongdeyuzhou/article/details/79789629
 *        https://www.jianshu.com/p/be2c09cd27d8
 *        https://juejin.im/post/5ba0a098f265da0adb30c684
 *
 * Created by fangdajiang on 2018/10/30.
 */
@Configuration
@ConditionalOnProperty(prefix = "spring.cache", name = "type", havingValue = "redis", matchIfMissing = true)
@EnableCaching
@Slf4j
@EnableConfigurationProperties({RedisExpireProperties.class})
@Import(value={SpringUtil.class})
public class StarterCacheAutoConfiguration extends CachingConfigurerSupport {
    private static final int DEFAULT_CACHE_CONFIG_TTL_SECS = 30;

    /**
     * Key Serializer
     */
    @Bean
    public StringRedisSerializer stringRedisSerializer() {
        return new StringRedisSerializer();
    }

    /**
     * Value Serializer
     */
    @Bean
    public Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer() {
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        return jackson2JsonRedisSerializer;
    }

    /**
     * key 值生成工具
     */
    @Bean
    public KeyGenerator generateKey() {
        return (target, method, params) -> {
            StringBuilder sb = new StringBuilder();
            sb.append(target.getClass().getName());
            sb.append(method.getName());
            for (Object obj : params) {
                sb.append(obj.toString());
            }
            return sb.toString();
        };
    }

    /**
     * 设置 value 的序列化为 jackson2JsonRedisSerializer
     * 设置默认超时时间为 30 秒；根据配置设置各自的超时时间
     * 因 Spring Boot 1.5.x 和 2.0.x 的差异，导致这里的 prefix key 中需要手动增加一个冒号 : （而 1.5.x 中会自动产生）
     *
     * RedisCacheConfiguration 默认就使用了 StringRedisSerializer 序列化 key，
     * 使用了 JdkSerializationRedisSerializer 序列化 value。
     * 以下注释代码为默认实现
     *
     * <pre>{@code
     *   ClassLoader loader = this.getClass().getClassLoader();
     *   JdkSerializationRedisSerializer jdkSerializer = new JdkSerializationRedisSerializer(loader);
     *   RedisSerializationContext.SerializationPair<Object> pair = RedisSerializationContext.SerializationPair.fromSerializer(jdkSerializer);
     *   RedisCacheConfiguration defaultCacheConfig=RedisCacheConfiguration.defaultCacheConfig().serializeValuesWith(pair);
     * }</pre>
     *
     */
    @Bean
    public CacheManager cacheManager(LettuceConnectionFactory connectionFactory
            , RedisExpireProperties redisExpireProperties
            , StringRedisSerializer stringRedisSerializer
            , Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer) {
        log.info("initiating cache manager, connectionFactory:{}, redisExpireProperties:{}", connectionFactory, redisExpireProperties);
        RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig();
        //设置默认超过期时间是30秒
        defaultCacheConfig.entryTtl(Duration.ofSeconds(DEFAULT_CACHE_CONFIG_TTL_SECS));
        //把 过期属性值 放到 cacheConfigurations map 里
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        for (Map.Entry<String, Long> entry : redisExpireProperties.getExpireMap().entrySet()) {
            log.debug("entry.key:{}, value:{}", entry.getKey(), entry.getValue());
            cacheConfigurations.put(entry.getKey()
                    , defaultCacheConfig.entryTtl(Duration.ofSeconds(entry.getValue()))
                            //设置 key 为 string 序列化
                            .serializeKeysWith(RedisSerializationContext.SerializationPair
                                    .fromSerializer(stringRedisSerializer))
                            //设置 value 为 json 序列化
                            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer))
                            //不缓存空值
                            .disableCachingNullValues()
                            .prefixKeysWith(entry.getKey() + ":"));
        }
        log.info("defaultCacheConfig:{}", defaultCacheConfig);
        //初始化RedisCacheManager
        return RedisCacheManager
                .builder(RedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory))
                .cacheDefaults(defaultCacheConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }

    /**
     * set value/hashvalue Serializer with Jackson2JsonRedisSerializer
     */
    @Bean
    public RedisTemplate<String, String> redisTemplate(
            LettuceConnectionFactory factory,
            StringRedisSerializer stringRedisSerializer,
            Jackson2JsonRedisSerializer jackson2JsonRedisSerializer) {
        StringRedisTemplate template = new StringRedisTemplate(factory);
        template.setKeySerializer(stringRedisSerializer);
        template.setValueSerializer(jackson2JsonRedisSerializer);
        template.setHashValueSerializer(jackson2JsonRedisSerializer);
        template.afterPropertiesSet();
        log.info("RedisTemplate is using {} and {} as key/value serializer, using {} and {} as HASH key/value serializer."
                , template.getKeySerializer()
                , template.getValueSerializer()
                , template.getHashKeySerializer()
                , template.getHashValueSerializer());
        return template;
    }

   /* @Bean for spring boot 1.5.x
    public CacheManager cacheManager(RedisTemplate redisTemplate) {
        RedisCacheManager manager = new RedisCacheManager(redisTemplate);
        manager.setUsePrefix(true);
        RedisCachePrefix cachePrefix = new RedisPrefix("prefix");
        manager.setCachePrefix(cachePrefix);
        // 整体缓存过期时间
        manager.setDefaultExpiration(3600L);
        // 设置缓存过期时间。key和缓存过期时间，单位秒
        Map<String, Long> expiresMap = new HashMap<>();
        expiresMap.put("user", 1000L);
        manager.setExpires(expiresMap);
        return manager;
    }*/

}
