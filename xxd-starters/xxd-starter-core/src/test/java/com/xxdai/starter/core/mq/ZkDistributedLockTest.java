package com.xxdai.starter.core.mq;

import com.xxdai.starter.core.mq.zk.LockZookeeperClientFactory;
import com.xxdai.starter.core.mq.zk.ZookeeperSharedLock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

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
public class ZkDistributedLockTest {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    //LockZookeeperClientFactory通常是通过Spring配置注入的，此处是为了Demo的简单明了才这样写的，不建议这样写
/*
        LockZookeeperClientFactory lockZookeeperClientFactory = new LockZookeeperClientFactory();
        lockZookeeperClientFactory.setZookeeperIpPort("192.168.38.163:2181,192.168.38.164:2181");
        lockZookeeperClientFactory.setBasePath("/zkfdjtest");
*/
    @Autowired
    LockZookeeperClientFactory lockZookeeperClientFactory;

    @Test
    public void testZkLockCallback() {
        String resourceId = "sharedLock1";
        Object result = new ZookeeperSharedLock(lockZookeeperClientFactory, resourceId).doLock(100, new DistributedLockCallback() {
            @Override
            public Object execute() {
                logger.debug("success, start to do logic:{}",Thread.currentThread().getName());
                Integer logicResult = 999;
                return logicResult;
            }
        });
        if (null != result) { //仅当有必要处理返回的 result 数据时才需要本判断
            Integer myResult = (Integer)result;
            logger.debug("myResult:{}, thread:{}", myResult, Thread.currentThread().getName());
        } else {
            logger.warn("result is null, thread:{}", Thread.currentThread().getName());
        }
    }

    @Test
    public void testZkLock() {
        ZookeeperSharedLock sharedLock = new ZookeeperSharedLock(lockZookeeperClientFactory, "sharedLock1");
        try {
            if (sharedLock.lock(100, TimeUnit.MILLISECONDS)) {
                logger.debug("success, start to do logic:{}",Thread.currentThread().getName());
            } else {
                logger.warn("FAILED to get the zk lock");
            }
        } catch (Exception e) {
            logger.warn("ERROR", e);
        } finally {
            try {
                sharedLock.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //lockZookeeperClientFactory.destroy(); 只在管理功能中调用
    }
    @Test
    public void testConcurrentZkLock() {
        int count = 100;
        final CountDownLatch start=new CountDownLatch(1);
        final CountDownLatch end=new CountDownLatch(count);

        final ExecutorService executorService= Executors.newFixedThreadPool(count);


        for (int i = 1; i <= count; i++){
            Runnable runnable= new Runnable() {
                public void run() {
                    ZookeeperSharedLock sharedLock = new ZookeeperSharedLock(lockZookeeperClientFactory, "sharedLock1");
                    try {
                        start.await();//等待
                        if (sharedLock.lock(100, TimeUnit.MILLISECONDS)) {
                            logger.debug("success:{}",Thread.currentThread().getName());
                        }else{
                            logger.debug("fail:{}",Thread.currentThread().getName());
                        }
                    } catch (InterruptedException e) {
                        logger.warn("start await ERROR", e);
                    } catch (Exception e) {
                        logger.warn("UNKNOWN ERROR", e);
                    } finally {
/*
                        try {
                            sharedLock.release();
                        } catch (Exception e) {
                            logger.warn("ERROR", e);
                        }
*/
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