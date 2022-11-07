package com.xxdai.starter.core.mq;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.RetryNTimes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Created by fangdajiang on 2017/2/15.
 */
public class CuratorDistributedLockTest {
    private static final Logger logger = LoggerFactory.getLogger(CuratorDistributedLockTest.class);

    private static final String ZK_ADDRESS = "192.168.129.50:2181,192.168.129.51:2181,192.168.129.52:2181";
    public static final String ZK_LOCK_PATH = "/zkfdjtest";

    public static void main(String[] args) throws InterruptedException {
        CuratorFramework client = CuratorFrameworkFactory.newClient(
                ZK_ADDRESS,
                new RetryNTimes(10, 5000)
        );
        client.start();
        logger.debug("zk client start successfully!");

        Thread t1 = new Thread(new DoWithLock(client), "t1");
        Thread t2 = new Thread(new DoWithLock(client), "t2");
        t1.start();
        t2.start();
/*
        //for jdk1.8
        Thread t1 = new Thread(() -> {
            doWithLock(client);
        }, "t1");
        Thread t2 = new Thread(() -> {
            doWithLock(client);
        }, "t2");

        t1.start();
        t2.start();
*/
    }
/*
    //for jdk 1.8
    private static void doWithLock(CuratorFramework client) {
        InterProcessMutex lock = new InterProcessMutex(client, ZK_LOCK_PATH);
        try {
            if (lock.acquire(10 * 1000, TimeUnit.SECONDS)) {
                System.out.println(Thread.currentThread().getName() + " hold lock");
                Thread.sleep(5000L);
                System.out.println(Thread.currentThread().getName() + " release lock");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                lock.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
*/

}

class DoWithLock implements Runnable {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private CuratorFramework client;
    public DoWithLock(CuratorFramework client) {
        this.client = client;
    }

    public void run() {
        InterProcessLock lock = new InterProcessMutex(client, CuratorDistributedLockTest.ZK_LOCK_PATH);
        try {
            if (lock.acquire(10 * 1000, TimeUnit.SECONDS)) {
                logger.debug(Thread.currentThread().getName() + " is holding the lock");
                Thread.sleep(5000L);
                logger.debug(Thread.currentThread().getName() + " release the lock");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                lock.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
