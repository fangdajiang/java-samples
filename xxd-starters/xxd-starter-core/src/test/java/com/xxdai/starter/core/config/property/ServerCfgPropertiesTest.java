package com.xxdai.starter.core.config.property;

import com.xxdai.pub.common.model.ServerCfgObj;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * created by xiguoding on 2018/3/14 下午4:08
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ServerCfgPropertiesTest {
    @Autowired
    ServerCfgProperties serverCfgProperties;

    @Test
    public void testAutowired() {
        assertNotNull(serverCfgProperties);
    }

    @Test
    public void getCfgObj() {
        String rightClientId = "XXD_CLIENT";
        ServerCfgObj serverCfgObj = serverCfgProperties.getCfgObj(rightClientId);
        assertNotNull(serverCfgObj);
        ServerCfgObj defaultCfgObj = serverCfgProperties.getDefaultCfgObj();
        assertNull(defaultCfgObj);
    }
}
