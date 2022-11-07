package com.xxdai.starter.core.mq;

import com.xxdai.starter.core.mq.redis.RedisLock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by fangdajiang on 2017/2/27.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
//@ComponentScan(basePackages = {"com.xxdai.client"}) //为何无效？
public class RedisDistributedLockTest {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private RedisLock redisLock;

    @Test
    public void testRedisLockCallback() {
        String key = "name6";
        String uuid = UUID.randomUUID().toString();
        Object result = redisLock.doLock(key, uuid, new DistributedLockCallback() {
            @Override
            public Object execute() {
                logger.debug("success, start to do logic:{}",Thread.currentThread().getName());
                Integer logicResult = 100;
                return logicResult;
            }
        });
        if (null != result) {
            Integer myResult = (Integer)result;
            logger.debug("myResult:{}, thread:{}", myResult, Thread.currentThread().getName());
        } else {
            logger.warn("result is null, thread:{}", Thread.currentThread().getName());
        }
    }

    @Test
    public void testRedisLock() {
        String key = "name6";
        String uuid = UUID.randomUUID().toString();
        if (redisLock.lock(key, uuid)) {
            logger.debug("success, start to do something:{}",Thread.currentThread().getName());
        }else{
            logger.debug("fail:{}",Thread.currentThread().getName());
        }
    }

    @Test
    public void testConcurrentRedisLock() {
        final CountDownLatch start=new CountDownLatch(1);
        final CountDownLatch end=new CountDownLatch(100);

        final ExecutorService executorService= Executors.newFixedThreadPool(100);


        for (int i = 1; i <= 100; i++){
            Runnable runnable= new Runnable() {
                public void run() {
                    try {
                        start.await();//等待
                        //logger.info(Thread.currentThread().getName()+":"+System.currentTimeMillis());
                        if (redisLock.lock("name7", UUID.randomUUID().toString())) {
                            TimeUnit.MILLISECONDS.sleep(100);
                            logger.debug("success:{}",Thread.currentThread().getName());
                        }else{
                            logger.debug("fail:{}",Thread.currentThread().getName());
                        }
                    } catch (InterruptedException e) {
                        logger.warn("start await ERROR", e);
                    }finally {
                        end.countDown();
                    }
                }
            };
            executorService.submit(runnable);
        }
        start.countDown();

        try {
            end.await();
            logger.debug("all thread run over....");
        } catch (InterruptedException e) {
            logger.warn("end await ERROR", e);
        } finally {
            //redisLock.unlock();
        }
    }
}