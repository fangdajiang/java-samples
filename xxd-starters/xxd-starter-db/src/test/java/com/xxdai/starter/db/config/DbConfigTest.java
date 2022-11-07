package com.xxdai.starter.db.config;

import com.xxdai.starter.db.config.property.DruidConfigProperties;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;

import static org.junit.Assert.assertNotNull;

/**
 * created by xiguoding on 2018/4/10 上午10:32
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class DbConfigTest {
    @Autowired
    private DruidConfig druidConfig;
    @Autowired
    private DruidConfigProperties druidConfigProperties;

    @Test
    public void testAutowired() {
        assertNotNull(druidConfig);
    }

    @Test
    public void dbConfigTest() {
        DataSource dataSource = druidConfig.dataSource(druidConfigProperties);
        assertNotNull(dataSource);

        ServletRegistrationBean bean = druidConfig.druidStatViewServlet(druidConfigProperties);
        assertNotNull(bean);

        FilterRegistrationBean filter = druidConfig.druidStatFilter();
        assertNotNull(filter);
    }
}
