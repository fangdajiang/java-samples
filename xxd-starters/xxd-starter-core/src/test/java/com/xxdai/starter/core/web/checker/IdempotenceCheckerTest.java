package com.xxdai.starter.core.web.checker;

import com.xxdai.starter.cache.service.StatusService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertNotNull;

/**
 * Created by fangdajiang on 2017/6/15.
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
@EnableCaching
public class IdempotenceCheckerTest {
    @Autowired
    private StatusService statusService;

    @Test
    public void autowiredBean() throws Exception {
        log.debug("idempotenceServiceImpl:{}", statusService);
        assertNotNull(statusService);
//        assertNotNull(commonInterceptor);
    }

    @Test
    public void preCheck() throws Exception {

    }

}