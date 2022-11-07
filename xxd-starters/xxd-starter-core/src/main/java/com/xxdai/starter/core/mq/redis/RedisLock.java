package com.xxdai.starter.core.mq.redis;

import com.xxdai.starter.cache.config.property.RedisExpireProperties;
import com.xxdai.starter.core.mq.DistributedLockCallback;
import com.xxdai.pub.constant.Global;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeUnit;

/**
 * Redis distributed lock implementation.
 * Created by yantingbin on 2017/2/23.
 */
@Component
@Scope("singleton")
@ConditionalOnBean(RedisExpireProperties.class)
public class RedisLock {
    private static final long EXPIRE_MILLIS = 200;
    private static long TIMEOUT_NANOS = 100L * 1000000; //100 ms 之内没有成功获得锁则会持续不断地尝试获取
    private static int FAIL_SLEEP_MILLIS = 5;
    private static final Logger logger = LoggerFactory.getLogger(RedisLock.class);

    @Autowired
    private RedisTemplate<String, String> template;

    public Object doLock(final String key, final String uuid, DistributedLockCallback redisLockCallback) {
        logger.trace("key:{}, uuid:{}", key, uuid);
        long nanoTime = System.nanoTime();
        long start = System.currentTimeMillis();
        try {
            while (System.nanoTime() - nanoTime < TIMEOUT_NANOS) {
                Boolean result = tryLock(key, uuid);
                long end = System.currentTimeMillis();
                if (result) {
                    logger.debug("spent {} ms to successfully get a redis lock", (end - start));
                    return redisLockCallback.execute();
                } else {
                    logger.warn("failed to get a redis lock spending {} ms", (end - start));
                }
                TimeUnit.MILLISECONDS.sleep(FAIL_SLEEP_MILLIS);
            }
        } catch (InterruptedException e) {
            logger.warn("key:{}, uuid:{}, msg:{}", key, uuid, e.getMessage());
            // Restore interrupted state...
            Thread.currentThread().interrupt();
        }
        return null;
    }

    public Boolean lock(final String key, final String uuid) {
        long nanoTime = System.nanoTime();
        long start = System.currentTimeMillis();
        try {
            while (System.nanoTime() - nanoTime < TIMEOUT_NANOS) {
                Boolean result = tryLock(key, uuid);
                long end = System.currentTimeMillis();
                if (result) {
                    logger.debug("spent {} ms to successfully get a redis lock", (end - start));
                    return true;
                } else {
                    logger.warn("failed to get a redis lock spending {} ms", (end - start));
                }
                TimeUnit.MILLISECONDS.sleep(FAIL_SLEEP_MILLIS);
            }
        } catch (InterruptedException e) {
            // Restore interrupted state...
            Thread.currentThread().interrupt();
            logger.warn("key:{}, uuid:{}, msg:{}", key, uuid, e.getMessage());
        }
        return false;
    }

    public void unlock(final String key, final String uuid) {
        Boolean bool = tryUnlock(key, uuid);
        if (bool) {
            logger.debug("Distributed UnLock successfully...key:{}, uuid:{}", key, uuid);
        } else {
            logger.warn("Distributed UnLock failed...key:{}, uuid:{}", key, uuid);
        }
    }

    /**
     * 可能需要解决的问题：假如在setnx后，redis崩溃了，expire就没有执行（不确定），
     * 结果就是死锁了。锁永远不会超时。
     * @param key
     * @param uuid
     * @return
     */
    private Boolean tryLock(final String key, final String uuid) {
        return template.execute(new RedisCallback<Boolean>() {
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer<String> serializer = template.getStringSerializer();
                byte[] keyBytes = serializer.serialize(key + ".lock");
                byte[] valueBytes = serializer.serialize("locked:" + uuid);
                Boolean boolLock = connection.setNX(keyBytes, valueBytes);
                logger.trace("boolLock:{}, key:{}, uuid:{}", boolLock, key, uuid);
                if (boolLock) {
                    connection.pExpire(keyBytes, EXPIRE_MILLIS);
                }
                return boolLock;
            }
        });
    }

    /**
     * 可能需要解决的问题：为了让分布式锁的算法更稳键些，持有锁的客户端在解锁
     * 之前应该再检查一次自己的锁是否已经超时，再去做DEL操作，因为可能客户端
     * 因为某个耗时的操作而挂起，操作完的时候锁因为超时已经被别人获得，这时就不必解锁了。
     * @param key
     * @param uuid
     * @return
     */
    private Boolean tryUnlock(final String key, final String uuid) {
        return template.execute(new RedisCallback<Boolean>() {
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer<String> serializer = template.getStringSerializer();
                byte[] keyBytes = serializer.serialize(key + ".lock");
                byte[] bs = connection.get(keyBytes);
                if(bs == null){
                    return false;
                }
                try {
                    if(("locked:" + uuid).equals(new String(bs, Global.DEFAULT_CHARSET))){
                        return connection.del(keyBytes) == 1;
                    }else{
                        return false;
                    }
                } catch (UnsupportedEncodingException e) {
                    logger.warn("key:{}, uuid:{}", key, uuid, e);
                    return false;
                }
            }
        });
    }
}
