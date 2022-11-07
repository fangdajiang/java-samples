package com.xxdai.starter.sample.util;

import com.xxdai.pub.common.model.BaseRequest;
import com.xxdai.pub.common.model.BaseRequestHeader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

/**
 *
 * @author fangdajiang
 * @date 2018/5/28
 */
@Slf4j
public class RequestDemo {

    public static final String DEMO_CLIENT_ID = "XXD_DEMO";
    public static final String DEMO_CLIENT_IP = "1.2.3.4";
    public static final String DEMO_USER_AGENT = UserAgent.PC.getValue();
    public static final String DEMO_S = "demo-mock-s";
    public static final String DEMO_TOKEN = "demo-mock-token";
    public static final String DEMO_USER_ID = "115397";
    public static final String DEMO_BODY_STRING = "demo body string";

    public enum UserAgent {
        PC("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.87 Safari/537.36"),
        IOS("iOS-xxd"),
        ANDROID("okhttp/3.4.1"),
        H5("Mozilla/5.0 (iPhone; CPU iPhone OS 8_4 like Mac OS X) AppleWebKit/600.1.4 (KHTML, like Gecko) Mobile/12H143 MicroMessenger/6.5.15 NetType/WIFI Language/zh_CN"),
        UNKNOWN_TERMINAL("UNKNOWN_TERMINAL");

        private final String value;

        UserAgent(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }

    }

    public static final String getFullUrl(String prefixUrl, MultiValueMap<String, String> params) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(prefixUrl)
                .queryParams(params);
        return builder.toUriString();
    }

    /**
     * 默认 clientId 为 XXD_DEMO, clientIp 为 1.2.3.4, User-Agent 为 PC, s 和 token 任意值
     * @return
     */
    public static final HttpHeaders getXxdHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        httpHeaders.add("clientId", DEMO_CLIENT_ID);
        httpHeaders.add("clientIp", DEMO_CLIENT_IP);
        httpHeaders.add("clientTime", String.valueOf(System.currentTimeMillis()));
        httpHeaders.add("User-Agent", DEMO_USER_AGENT);
        httpHeaders.add("s", DEMO_S);
        httpHeaders.add("token", DEMO_TOKEN);
        return httpHeaders;
    }

    public static final HttpHeaders getXxdHeaders(String token) {
        HttpHeaders httpHeaders = getXxdHeaders();
        httpHeaders.set("token",  token);
        return httpHeaders;
    }

    public static final HttpHeaders getXxdHeaders(UserAgent userAgent) {
        HttpHeaders httpHeaders = getXxdHeaders();
        httpHeaders.set("User-Agent",  userAgent.getValue());
        return httpHeaders;
    }

    public static final HttpHeaders getXxdHeaders(String clientId, String token, UserAgent userAgent) {
        HttpHeaders httpHeaders = getXxdHeaders();
        httpHeaders.set("clientId",  clientId);
        httpHeaders.set("token",  token);
        httpHeaders.set("User-Agent",  userAgent.getValue());
        return httpHeaders;
    }

    public static final BaseRequest getBaseRequest(Class controller, String clientId, String token, UserAgent userAgent) {
        BaseRequest baseRequest = getBaseRequest(controller);
        baseRequest.setBaseRequestHeader(getBaseRequestHeader(getXxdHeaders(clientId, token, userAgent)));
        return baseRequest;
    }

    public static final BaseRequest getBaseRequest(Class controller, UserAgent userAgent) {
        BaseRequest baseRequest = getBaseRequest(controller);
        baseRequest.setBaseRequestHeader(getBaseRequestHeader(getXxdHeaders(userAgent)));
        return baseRequest;
    }

    public static final BaseRequest getBaseRequest(Class controller, String token) {
        BaseRequest baseRequest = getBaseRequest(controller);
        baseRequest.setBaseRequestHeader(getBaseRequestHeader(getXxdHeaders(token)));
        return baseRequest;
    }

    public static final BaseRequest getBaseRequest(Class controller) {
        BaseRequestHeader baseRequestHeader = getBaseRequestHeader(getXxdHeaders());
        BaseRequest baseRequest = new BaseRequest();
        baseRequest.setBodyString(DEMO_BODY_STRING);
        baseRequest.setControllerClass(controller);
        baseRequest.setRequestMethod(controller.getMethods()[0]);
        baseRequest.setBaseRequestHeader(baseRequestHeader);
        return baseRequest;
    }

    public static BaseRequestHeader getBaseRequestHeader(HttpHeaders demoHttpHeaders) {
        BaseRequestHeader baseRequestHeader = new BaseRequestHeader();
        baseRequestHeader.setClientId(demoHttpHeaders.getFirst("clientId"));
        baseRequestHeader.setClientTime(demoHttpHeaders.getFirst("clientTime"));
        baseRequestHeader.setClientIp(demoHttpHeaders.getFirst("clientIp"));
        baseRequestHeader.setClientSign(demoHttpHeaders.getFirst("s"));
        baseRequestHeader.setUserAgent(demoHttpHeaders.getFirst("User-Agent"));
        baseRequestHeader.setToken(demoHttpHeaders.getFirst("token"));
        return baseRequestHeader;
    }

}
