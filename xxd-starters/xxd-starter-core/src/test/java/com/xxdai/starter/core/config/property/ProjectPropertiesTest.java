package com.xxdai.starter.core.config.property;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertNotNull;

/**
 * created by xiguoding on 2018/3/13 下午5:29
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ProjectPropertiesTest {
    @Autowired
    ProjectProperties projectProperties;

    @Test
    public void testAutowired() {
        assertNotNull(projectProperties);
    }
}
