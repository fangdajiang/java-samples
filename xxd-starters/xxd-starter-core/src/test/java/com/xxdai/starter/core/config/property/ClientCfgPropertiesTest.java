package com.xxdai.starter.core.config.property;

import com.xxdai.pub.common.model.ClientCfgObj;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * created by xiguoding on 2018/3/13 下午5:27
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ClientCfgPropertiesTest {
    @Autowired
    ClientCfgProperties clientCfgProperties;

    @Test
    public void testAutowired() {
        assertNotNull(clientCfgProperties);
    }

    @Test
    public void getCfgObjTest() {
        String rightClientId = "XXD_FRONT_END";
        ClientCfgObj cfgObj = clientCfgProperties.getCfgObj(rightClientId);
        assertNotNull(cfgObj);

        String wrongClientId = "XXD";
        ClientCfgObj cfgObj1 = clientCfgProperties.getCfgObj(wrongClientId);
        assertNull(cfgObj1);
    }
}
