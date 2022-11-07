package com.xxdai.starter.db.service.impl;

import com.xxdai.pub.common.service.BizService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertTrue;

/**
 * Created by fangdajiang on 2017/9/30.
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class BizServiceImplTest {
    @Autowired
    private BizService bizService;

    @Test
    public void querySequence() throws Exception {
        long seq = bizService.querySequence("SEQ_REALNAME_APPRO");
        log.debug("seq:{}", seq);
        assertTrue(seq+"".length() > 0);
    }

}