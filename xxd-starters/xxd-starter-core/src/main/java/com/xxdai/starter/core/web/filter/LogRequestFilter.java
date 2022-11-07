package com.xxdai.starter.core.web.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xxdai.pub.common.util.DateUtil;
import com.xxdai.pub.common.util.RequestUtil;
import com.xxdai.pub.common.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

/**
 * Spring Web filter for logging request and response.
 *
 * @author Hidetake Iwata
 * @see org.springframework.web.filter.AbstractRequestLoggingFilter
 * @see ContentCachingRequestWrapper
 * @see ContentCachingResponseWrapper
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "fw.web.logEnabled", havingValue = "true")
public class LogRequestFilter extends OncePerRequestFilter {
    private static final List<MediaType> VISIBLE_TYPES = Arrays.asList(
            MediaType.valueOf("text/*"),
            MediaType.APPLICATION_FORM_URLENCODED,
            MediaType.APPLICATION_JSON,
            MediaType.valueOf("application/*+json"),
            MediaType.MULTIPART_FORM_DATA
    );

    private static final List<String> IGNORE_URI_PREFIXS = Arrays
            .asList("/webjars/", "/swagger-ui.html", "/csrf", "/swagger-resources", "/v2/api-docs");
    private static final String NAME_COSE_TIME = "costTime";


    private static ContentCachingRequestWrapper wrapRequest(HttpServletRequest request) {
        if (request instanceof ContentCachingRequestWrapper) {
            return (ContentCachingRequestWrapper) request;
        } else {
            return new ContentCachingRequestWrapper(request);
        }
    }

    private static ContentCachingResponseWrapper wrapResponse(HttpServletResponse response) {
        if (response instanceof ContentCachingResponseWrapper) {
            return (ContentCachingResponseWrapper) response;
        } else {
            return new ContentCachingResponseWrapper(response);
        }
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (isAsyncDispatch(request) || !log.isInfoEnabled() || canIgnore(request.getRequestURI())) {
            filterChain.doFilter(request, response);
        } else {
            doFilterWrapped(wrapRequest(request), wrapResponse(response), filterChain);
        }
    }

    private boolean canIgnore(String requestURI) {
        return requestURI.equals("/") || IGNORE_URI_PREFIXS.stream().anyMatch(requestURI::startsWith);
    }

    private void doFilterWrapped(ContentCachingRequestWrapper request, ContentCachingResponseWrapper response, FilterChain filterChain) throws ServletException, IOException {
        JSONObject jsonObject = new JSONObject(true);
        try {
            beforeRequest(request, jsonObject);
            filterChain.doFilter(request, response);
        } finally {
            afterRequest(request, response, jsonObject);
            response.copyBodyToResponse();
            log.info(jsonObject.toJSONString());
        }
    }

    private void beforeRequest(ContentCachingRequestWrapper request, JSONObject jsonObject) {
        Instant now = Instant.now();
        jsonObject.put("requestURI", request.getRequestURI());
        jsonObject.put(NAME_COSE_TIME, now.toEpochMilli());
        if (log.isDebugEnabled()) {
            jsonObject.put("requestHeaders", RequestUtil.getRequestHeaderMap(request));
        } else {
            jsonObject.put("requestId", request.getHeader(RequestUtil.REQUEST_ID_HEADER_KEY));
        }
        jsonObject.put("queryStr", request.getQueryString());
        jsonObject.put("clientIP", RequestUtil.getRealIpAddress(request));
        jsonObject.put("requestTime", DateUtil.format(LocalDateTime
                .ofInstant(now, ZoneId.systemDefault()), DateUtil.FullDateFormat));
    }

    private void afterRequest(ContentCachingRequestWrapper request, ContentCachingResponseWrapper response, JSONObject jsonObject) throws UnsupportedEncodingException {
        Instant now = Instant.now();
        jsonObject.put("responseTime", DateUtil.format(LocalDateTime
                .ofInstant(now, ZoneId.systemDefault()), DateUtil.FullDateFormat));
        jsonObject.put(NAME_COSE_TIME, now.toEpochMilli() - jsonObject.getLong(NAME_COSE_TIME));
        byte[] requestContent = request.getContentAsByteArray();
        byte[] responseContent = response.getContentAsByteArray();
        if (requestContent.length > 0) {
            boolean requestBodyVisible = VISIBLE_TYPES.stream()
                    .anyMatch(visibleType -> visibleType.includes(MediaType.valueOf(request.getContentType())));
            if (requestBodyVisible) {
                String bodyString = new String(requestContent, request
                        .getCharacterEncoding()).replaceAll("\r\n|\r|\n", "");
                try {
                    JSONObject bodyJson = JSON.parseObject(bodyString);
                    jsonObject.put("requestBody", bodyJson);
                } catch (Exception e) {
                    log.warn("error occurred when parse bodyString({}) to json", bodyString);
                    jsonObject.put("requestBody", bodyString);
                }
            }
        }
        if (responseContent.length > 0) {
            boolean responseBodyVisible = VISIBLE_TYPES.stream()
                    .anyMatch(visibleType -> visibleType.includes(MediaType.valueOf(response.getContentType())));
            if (responseBodyVisible) {
                String bodyString = new String(responseContent, response
                        .getCharacterEncoding()).replaceAll("\r\n|\r|\n", "");
                try {
                    JSONObject bodyJson = JSON.parseObject(bodyString);
                    jsonObject.put("responseBody", bodyJson);
                } catch (Exception e) {
                    log.warn("error occurred when parse bodyString({}) to json", bodyString);
                    jsonObject.put("responseBody", bodyString);
                }
            }
        }
        if (log.isDebugEnabled()) {
            jsonObject.put("responseHeaders", ResponseUtil.getResponseHeaderMap(response));
        }
    }
}
