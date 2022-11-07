package com.xxdai.starter.sample.dao;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

import static org.junit.Assert.assertTrue;

/**
 * Created by fangdajiang on 2018/4/17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class DemoForestDaoTest {

    private @Autowired
    DemoForestDao demoForestDao;
    private final static String userId = "115397";

    @Test
    public void acquireDuanWangZhiUrl() {
        log.debug("result:{}", demoForestDao.acquireDuanWangZhiUrl("http://www.xinxindai.com"));
    }

    @Test
    public void multiParamsHttp() throws InterruptedException {
        Map<String, Object> tokenResult = demoForestDao.obtainToken(userId);
        Map<String, Object> result = demoForestDao.multiParamsHttp((String)tokenResult.get("data"),"0", "0", "1", "5");
        log.debug("tokenResult:{}, result:{}", tokenResult, result);
        assertTrue("200000".equals(tokenResult.get("code")));
        assertTrue("200000".equals(result.get("code")));
    }

    @Test
    public void obtainToken() {
        Map<String, Object> tokenResult = demoForestDao.obtainToken(userId);
        assertTrue("200000".equals(tokenResult.get("code")));
    }

}
