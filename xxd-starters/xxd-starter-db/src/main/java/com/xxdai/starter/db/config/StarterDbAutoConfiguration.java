package com.xxdai.starter.db.config;

import com.xxdai.pub.common.service.BizService;
import com.xxdai.starter.db.service.impl.BizServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 *
 * Created by fangdajiang on 2018/11/26.
 */
@Configuration
@Slf4j
@Import(value={DruidConfig.class})
@ConditionalOnBean({DataSourceAutoConfiguration.class})
public class StarterDbAutoConfiguration {
    @Bean
    public BizService bizService(){
        BizService bizService = new BizServiceImpl();
        log.debug("initializing biz service:{}", bizService);
        return bizService;
    }

}
