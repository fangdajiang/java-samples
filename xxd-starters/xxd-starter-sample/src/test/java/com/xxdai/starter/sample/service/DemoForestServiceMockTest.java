package com.xxdai.starter.sample.service;

import com.xxdai.pub.common.model.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

/**
 * 对 sample 中的 ForestService 服务进行测试
 * Created by fangdajiang on 2018/9/4.
 */
@RunWith(MockitoJUnitRunner.class)
@Slf4j
public class DemoForestServiceMockTest {

    @Mock
    private DemoForestService demoForestService;
    private final static String userId = "115397";

    @Test
    public void acquireDwz() throws Exception {
        when(demoForestService.acquireDwz("http://www.xinxindai.com")).thenReturn("http://a.b/c");
        String dwzUrl = demoForestService.acquireDwz("http://www.xinxindai.com");
        log.debug("dwzUrl:{}", dwzUrl);
        assertEquals("http://a.b/c", dwzUrl);
    }

    @Test
    public void accessInvestmentApi() throws Exception {
        BaseResponse successfulBaseResponse = new BaseResponse();
        when(demoForestService.accessInvestmentApi(userId, "0", "0", "1", "5")).thenReturn(successfulBaseResponse);
        BaseResponse baseResponse = demoForestService.accessInvestmentApi(userId, "0", "0", "1", "5");
        log.debug("baseResponse:{}", baseResponse);
        assertTrue(baseResponse.isSuccess());
    }

    @Test
    public void createToken() throws Exception {
        when(demoForestService.createToken(userId)).thenReturn("abcdefg");
        String token = demoForestService.createToken(userId);
        log.debug("token:{}", token);
        assertEquals("abcdefg", token);
    }

}
