package com.xxdai.starter.core.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * Make the actuator endpoints accessible under local, dev, test, fix profiles
 * How to deal with secured endpoints: https://codecentric.github.io/spring-boot-admin/current/#securing-spring-boot-admin
 *
 * Spring Security 默认启用了 csrf 保护，通过 http.cors().and().csrf().disable() 禁掉了
 *
 * Created by fangdajiang on 2018/11/3.
 */
@Configuration
@Profile({"local","dev","test","fix"})
@Slf4j
public class SecurityPermitAllConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        log.info("Make the actuator endpoints accessible.");
        http.authorizeRequests().anyRequest().permitAll()
                .and().csrf().disable();
    }
}