package com.xxdai.starter.core.util;

import com.xxdai.pub.common.model.ServerCfgObj;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * created by xiguoding on 2018/3/15 下午2:08
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class ConfigurationUtilTest {

    @Test
    public void getServerCfgObjTest() {
        ServerCfgObj serverCfgObj = ConfigurationUtil.getServerCfgObj("XXD_CLIENT");
        assertNotNull(serverCfgObj);
        log.debug("serverCfgObj:{}", serverCfgObj);

        ServerCfgObj defaultCfgObj = ConfigurationUtil.getDefaultCfgObj();
        assertNull(defaultCfgObj);
    }
}
