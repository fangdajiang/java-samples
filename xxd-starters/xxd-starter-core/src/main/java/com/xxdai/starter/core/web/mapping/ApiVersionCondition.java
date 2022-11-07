package com.xxdai.starter.core.web.mapping;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.condition.RequestCondition;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.*;

/**
 * from: https://www.jianshu.com/p/5b820f393c62
 *
 * @author maskwang520
 * @date 2017/12/2
 */
@Slf4j
public class ApiVersionCondition implements RequestCondition<ApiVersionCondition> {
    private final static int DECIMAL_POINT_RIGHT_SIDE_NUMBER_COUNT = 4;

    /**
     * 路径中版本的前缀， 这里用 /v[0-9.1-9999]/的形式
     */
    private final static Pattern VERSION_PREFIX_PATTERN = compile("v(\\d*\\.?\\d*)/");

    private float apiVersion;

    public ApiVersionCondition(float apiVersion){
        this.apiVersion = apiVersion;
    }

    /**
     * 将不同的筛选条件合并,这里采用的覆盖，即后来的规则生效
     * @param other
     * @return
     */
    @Override
    public ApiVersionCondition combine(ApiVersionCondition other) {
        return new ApiVersionCondition(other.getApiVersion());
    }

    /**
     * 根据request查找匹配到的筛选条件
     * @param request
     * @return
     */
    @Override
    public ApiVersionCondition getMatchingCondition(HttpServletRequest request) {
        log.debug("Matching Condition Founded:{}", request.getRequestURI());
        Matcher m = VERSION_PREFIX_PATTERN.matcher(request.getRequestURI());
        if(m.find()){
            Float version = Float.valueOf(m.group(1));
            //如果请求的版本号大于配置版本号， 则满足
            if (version >= this.apiVersion) {
                return this;
            }
        } else {
            log.warn("VERSION FORMAT WRONG:{}", request.getRequestURI(), VERSION_PREFIX_PATTERN.pattern());
        }
        return null;
    }

    /**
     * 实现不同条件类的比较，从而实现优先级排序
     *
     * @param other
     * @param request
     * @return
     */
    @Override
    public int compareTo(ApiVersionCondition other, HttpServletRequest request) {
        log.debug("other.getApiVersion():{}, this.apiVersion:{}", other.getApiVersion(), this.apiVersion);
        int otherApiVersion = (int)(other.getApiVersion() * Math.pow(10, DECIMAL_POINT_RIGHT_SIDE_NUMBER_COUNT));
        int thisApiVersion = (int)(this.apiVersion * Math.pow(10, DECIMAL_POINT_RIGHT_SIDE_NUMBER_COUNT));
        log.debug("processed other.getApiVersion():{}, processed this.apiVersion:{}", otherApiVersion, thisApiVersion);
        return otherApiVersion - thisApiVersion;
    }

    public float getApiVersion() {
        return apiVersion;
    }
}