package com.xxdai.starter.core.mq.zk;

import org.apache.curator.framework.CuratorFramework;

/**
 *
 * description
 *
 * @author Roadrunners
 * @version 1.0, 2015年7月9日
 */
public interface LockZookeeperClient {
    /**
     *
     * @return
     */
    CuratorFramework getCuratorFramework();

    /**
     *
     * @return
     */
    String getBasePath();

    /**
     * garbage collector
     *
     * @param gcPath
     */
    void gc(String gcPath);
}