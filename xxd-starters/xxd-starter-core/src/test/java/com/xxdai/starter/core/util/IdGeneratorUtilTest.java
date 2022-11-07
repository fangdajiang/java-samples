package com.xxdai.starter.core.util;

import com.xxdai.pub.common.model.IdentityObj;
import com.xxdai.pub.common.service.BizService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertNotNull;

/**
 * created by xiguoding on 2018/3/16 上午10:53
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
@ConditionalOnBean({ BizService.class })
public class IdGeneratorUtilTest {
    @Autowired
    private BizService bizService;
    @Test
    public void testAutowired() {
        assertNotNull(this.bizService);
    }

    @Test @Ignore
    public void generateOrderId() {
        IdentityObj identityObj = IdGeneratorUtil.generateOrderId("xxd", "Darkroge");
        log.debug("identityObj:{}", identityObj);
    }
}
