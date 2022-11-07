package com.xxdai.starter.core.config;

import com.spring4all.swagger.EnableSwagger2Doc;
import org.springframework.context.annotation.Profile;

/**
 * 考虑用 swagger.enabled 替换 @Profile
 */
@Profile({"local","dev","test","stage"})
@EnableSwagger2Doc
public class Swagger2Config {

}
