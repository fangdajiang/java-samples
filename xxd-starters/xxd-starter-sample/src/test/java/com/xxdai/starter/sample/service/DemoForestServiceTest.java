package com.xxdai.starter.sample.service;

import com.xxdai.pub.common.model.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by fangdajiang on 2018/8/31.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class DemoForestServiceTest {
    private @Autowired
    DemoForestService demoForestService;

    private final static String userId = "115397";


    @Test
    public void acquireDwz() throws Exception {
        String dwzUrl = demoForestService.acquireDwz("http://www.xinxindai.com");
        log.debug("dwzUrl:{}", dwzUrl);
        assertFalse(dwzUrl.isEmpty());
    }

    @Test
    public void accessInvestmentApi() throws Exception {
        BaseResponse baseResponse = demoForestService.accessInvestmentApi(userId, "0", "0", "1", "5");
        log.debug("baseResponse:{}", baseResponse);
        assertTrue(baseResponse.isSuccess());
    }

    @Test
    public void createToken() throws Exception {
        String token = demoForestService.createToken(userId);
        log.debug("token:{}", token);
        assertFalse(token.isEmpty());
    }

    @Test
    public void foo() throws Exception {
        demoForestService.foo();
    }

}