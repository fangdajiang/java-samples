package com.xxdai.starter.core.config;

import com.xxdai.starter.core.config.property.ProjectProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;

import java.util.Arrays;

/**
 *
 * @author fangdajiang
 * @date 2018/5/24
 */
@Slf4j
public class XxdAutoConfiguration {
    private ProjectProperties projectProperties;
    private final static String[] EXCLUDED_SWAGGER_PATH_PATTERNS = new String[]{"/swagger-resources/**", "/v2/api-docs/**", "/swagger-ui.html", "/error", "/errorHtml"};

    @Autowired
    public void setProjectProperties(ProjectProperties projectProperties) {
        this.projectProperties = projectProperties;
    }

    void handlePathPatterns(InterceptorRegistration interceptorRegistration) {
        log.info("Excluding swagger uris from CommonInterceptor:{}", Arrays.toString(EXCLUDED_SWAGGER_PATH_PATTERNS));
        interceptorRegistration.addPathPatterns("/**").excludePathPatterns(EXCLUDED_SWAGGER_PATH_PATTERNS);
        if (CollectionUtils.isNotEmpty(projectProperties.getUriIgnore())) {
            String[] excludedUris = projectProperties.getUriIgnore().toArray(new String[]{});
            log.info("Excluding uris ignored by project properties:{}", Arrays.toString(excludedUris));
            interceptorRegistration.excludePathPatterns(excludedUris);
        }
    }
}
