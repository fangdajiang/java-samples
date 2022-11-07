package com.xxdai.starter.core.mq.zk;

import com.xxdai.starter.core.mq.DistributedLockCallback;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * yml 中需要添加 zookeeper 属性形如： <br/>
 * zookeeper: <br/>
 *   ipPort: 192.168.38.163:2181,192.168.38.164:2181 <br/>
 *   basePath: /activity <br/>
 * 或者 <br/>
 * properties 中需要添加 zookeeper 属性形如： <br/>
 * zookeeper.ipPort=192.168.38.163:2181,192.168.38.164:2181 <br/>
 * zookeeper.basePath=/activity <br/>
 * WebMvcConfig 中已初始化了 bean lockZookeeperClientFactory 可直接 autowired <br/>
 * 或者 <br/>
 * 添加 bean 形如： <br/>
 * <bean id="lockZookeeperClientFactory" class="com.xxdai.client.mq.zk.LockZookeeperClientFactory" p:zookeeperIpPort="${zookeeper.ipPort}" p:basePath="${zookeeper.basePath}"/> <br/>
 * 最后根据 test 用例来使用。
 *
 * Created by fangdajiang on 2017/3/7.
 */
public class ZookeeperSharedLock {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private InterProcessLock interProcessLock;

    public ZookeeperSharedLock(LockZookeeperClient lockZookeeperClient, String resourceId) {
        super();

        if (StringUtils.isBlank(resourceId)) {
            throw new NullPointerException("resourceId");
        }
        String path = lockZookeeperClient.getBasePath();
        path += ("/" + resourceId.trim());

        interProcessLock = new InterProcessMutex(lockZookeeperClient.getCuratorFramework(), path);
        lockZookeeperClient.gc(path);
    }

    public Object doLock(long waitMillis, DistributedLockCallback distributedLockCallback) {
        try {
            if (this.lock(waitMillis, TimeUnit.MILLISECONDS)) {
                logger.debug("SUCCESSFULLY to get the zk lock");
                Object object = distributedLockCallback.execute();
                release();
                logger.debug("ready to return object:{}", object);
                return object;
            } else {
                logger.warn("FAILED to get the zk lock in {} ms", waitMillis);
            }
        } catch (Exception e) {
            logger.warn("ERROR when acquire a lock, waitMillis:{}", waitMillis, e);
        }
        logger.debug("ready to return null");
        return null;
    }

    /**
     * Acquire the mutex - blocking until it's available. Each call to acquire must be balanced by a call
     * to {@link #release()}
     *
     * @throws Exception ZK errors, connection interruptions
     */
    public void lock() throws Exception {
        interProcessLock.acquire();
    }

    /**
     * Acquire the mutex - blocks until it's available or the given time expires. Each call to acquire that returns true must be balanced by a call
     * to {@link #release()}
     *
     * @param time time to wait
     * @param unit time unit
     * @return true if the mutex was acquired, false if not
     * @throws Exception ZK errors, connection interruptions
     */
    public boolean lock(long time, TimeUnit unit) throws Exception {
        long start = System.currentTimeMillis();
        boolean result = interProcessLock.acquire(time, unit);
        long end = System.currentTimeMillis();
        logger.debug("spent {} ms to get a zk lock:{}", (end - start), result);
        return result;
    }

    /**
     * Perform one release of the mutex.
     *
     * @throws Exception ZK errors, interruptions, current thread does not own the lock
     */
    public void release() throws Exception {
        logger.debug("thread {} is going to release the lock", Thread.currentThread().getName());
        interProcessLock.release();
    }

    /**
     * Returns true if the mutex is acquired by a thread in this JVM
     *
     * @return true/false
     */
    public boolean isAcquiredInThisProcess() {
        return interProcessLock.isAcquiredInThisProcess();
    }
}
