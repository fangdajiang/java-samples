package com.xxdai.starter.core.config.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 *
 * @author yq
 * @date 2017/4/17
 * @Configuration
 */
@Data
@Validated
@ConfigurationProperties(prefix = "project", ignoreInvalidFields = true)
public class ProjectProperties {
	private List<String> uriIgnore;
}
