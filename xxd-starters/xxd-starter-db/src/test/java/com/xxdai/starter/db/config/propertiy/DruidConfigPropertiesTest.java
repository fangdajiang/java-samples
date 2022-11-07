package com.xxdai.starter.db.config.propertiy;

import com.xxdai.starter.db.config.property.DruidConfigProperties;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertNotNull;

/**
 * created by xiguoding on 2018/4/9 下午5:11
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class DruidConfigPropertiesTest {

    @Autowired
    private DruidConfigProperties druidConfigProperties;

    @Test
    public void testAutowired() {
        assertNotNull(druidConfigProperties);
        log.info("druidConfigProperties:{}", druidConfigProperties);
    }



}
