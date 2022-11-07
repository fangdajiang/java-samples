package com.xxdai.starter.sample.config;

import com.xxdai.starter.sample.dao.DemoForestDao;
import org.forest.config.ForestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author fangdajiang
 * @date 2018/6/22
 */
@Configuration
public class SampleConfiguration {
    @Bean
    public DemoForestDao demoForestDao() {
        ForestConfiguration forestConfiguration = ForestConfiguration.configuration();
        //设置自定义参数
        forestConfiguration.setTimeout(3000);
        forestConfiguration.setRetryCount(3);
        forestConfiguration.setConnectTimeout(5000);
        forestConfiguration.setMaxConnections(1024);
        forestConfiguration.setMaxRouteConnections(1024);
        //设置全局变量
        return forestConfiguration.createInstance(DemoForestDao.class);
    }

}
