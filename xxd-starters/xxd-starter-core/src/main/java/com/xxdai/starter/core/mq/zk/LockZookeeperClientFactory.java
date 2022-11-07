package com.xxdai.starter.core.mq.zk;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 *
 * description
 *
 * @author Roadrunners
 * @version 1.0, 2015年7月9日
 */
public class LockZookeeperClientFactory implements LockZookeeperClient {
    private static final Logger logger = LoggerFactory.getLogger(LockZookeeperClientFactory.class);
    
    private boolean hasGc = true;
    private Timer gcTimer;
    private TimerTask gcTimerTask;
    private ConcurrentSkipListSet<String> gcPaths = new ConcurrentSkipListSet<String>();
    private int gcIntervalSecond = 60;

    private CuratorFramework curatorFramework;
    private String zookeeperIpPort;
    private int sessionTimeoutMs = 10000;
    private int connectionTimeoutMs = 10000;
    private String basePath = "/xxdlocks";

    public void setHasGc(boolean hasGc) {
        this.hasGc = hasGc;
    }

    public void setGcIntervalSecond(int gcIntervalSecond) {
        this.gcIntervalSecond = gcIntervalSecond;
    }

    public void setZookeeperIpPort(String zookeeperIpPort) {
        this.zookeeperIpPort = zookeeperIpPort;
    }

    public String getZookeeperIpPort() {
        return zookeeperIpPort;
    }

    public void setSessionTimeoutMs(int sessionTimeoutMs) {
        this.sessionTimeoutMs = sessionTimeoutMs;
    }

    public void setConnectionTimeoutMs(int connectionTimeoutMs) {
        this.connectionTimeoutMs = connectionTimeoutMs;
    }

    public void setBasePath(String basePath) {
        basePath = basePath.trim();
        if (basePath.endsWith("/")) {
            basePath = basePath.substring(0, basePath.length() - 1);
        }

        this.basePath = basePath;
    }

    @PostConstruct
    public void postConstruct() {
        init();
    }

    private void init() {
        if(StringUtils.isBlank(zookeeperIpPort)){
            throw new NullPointerException("zookeeperIpPort is null");
        }

        ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(1000, 3);
        curatorFramework = CuratorFrameworkFactory.newClient(zookeeperIpPort.trim(),
                sessionTimeoutMs, connectionTimeoutMs, retryPolicy);
        curatorFramework.start();
        logger.trace("CuratorFramework init successfully.");

        if (hasGc) {
            gc();
        }
    }

    public void destroy() {
        gcPaths.clear();
        gcPaths = null;
        gcStop();
        curatorFramework.close();
        curatorFramework = null;
    }

    @Override
    public void gc(String gcPath) {
        if (hasGc && StringUtils.isNotBlank(gcPath)) {
            gcPaths.add(gcPath.trim());
        }
    }

    @Override
    public CuratorFramework getCuratorFramework() {
        return this.curatorFramework;
    }

    @Override
    public String getBasePath() {
        return this.basePath;
    }

    private synchronized void gc() {
        gcStop();

        try {
            scanningGCNodes();
        } catch (Throwable e) {
            logger.warn("basePath:{}", basePath, e);
        }

        gcTimerTask = new TimerTask() {
            @Override
            public void run() {
                doingGc();
            }
        };

        Date begin = new Date();
        begin.setTime(begin.getTime() + (10 * 1000L));
        gcTimer = new Timer("lock-gc", true);
        gcTimer.schedule(gcTimerTask, begin, gcIntervalSecond * 1000L);
    }

    private synchronized void gcStop() {
        if (null != gcTimer) {
            gcTimer.cancel();
            gcTimer = null;
        }

        if (null != gcTimerTask) {
            gcTimerTask.cancel();
            gcTimerTask = null;
        }
    }

    private synchronized void scanningGCNodes() throws Exception {
        if (null == curatorFramework.checkExists().forPath(basePath)) {
            return;
        }

        List<String> paths = curatorFramework.getChildren().forPath(basePath);
        if (CollectionUtils.isEmpty(paths)) {
            gcPaths.add(basePath);
            return;
        }

        for (String path : paths) {
            try{
                String tmpPath = basePath + "/" + path;
                if (null == curatorFramework.checkExists().forPath(tmpPath)) {
                    continue;
                }

                gcPaths.add(tmpPath);
            } catch(Throwable e){
                logger.warn("scanning gc nodes error. path:{}", path, e);
            }
        }
    }

    private synchronized void doingGc() {
        logger.trace("GC begin.");

        if (CollectionUtils.isNotEmpty(gcPaths)) {
            for (String path : gcPaths) {
                try {
                    if (null != curatorFramework.checkExists().forPath(path)) {
                        if (CollectionUtils.isEmpty(curatorFramework.getChildren().forPath(path))) {
                            curatorFramework.delete().forPath(path);
                            gcPaths.remove(path);
                            logger.info("GC:{}", path);
                        }
                    } else {
                        gcPaths.remove(path);
                    }
                } catch (Throwable e) {
                    gcPaths.remove(path);
                    logger.warn("gcPaths:{}", gcPaths, e);
                }
            }
        }

        logger.trace("GC end.");
    }

}