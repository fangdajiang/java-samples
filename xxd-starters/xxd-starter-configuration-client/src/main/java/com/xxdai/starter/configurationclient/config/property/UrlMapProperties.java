package com.xxdai.starter.configurationclient.config.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.Map;

/**
 * Created by yq on 2017/3/22.
 */
@Configuration @Data
@ConfigurationProperties
@PropertySource(value = "classpath:urlmap.properties")
public class UrlMapProperties {
    private Map<String,String> configurationApi;
}
