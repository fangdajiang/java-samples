package com.xxdai.starter.core.util.helper;

import com.google.common.collect.Maps;
import com.xxdai.pub.common.model.BaseRequest;
import com.xxdai.pub.common.model.BaseRequestHeader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static com.xxdai.pub.common.util.RequestUtil.*;

/**
 *
 * @author fangdajiang
 * @date 2017/11/20
 */
@Slf4j
public class BaseRequestHelper {
    private BaseRequestHelper() {}

    public static Map<String, String> getHeadersMap(BaseRequestHeader baseRequestHeader) {
        Map<String, String> headersMap = Maps.newHashMap();
        headersMap.put("clientId", baseRequestHeader.getClientId());
        headersMap.put("clientTime", baseRequestHeader.getClientTime());
        headersMap.put("s", baseRequestHeader.getClientSign());
        headersMap.put("token", baseRequestHeader.getToken());
        headersMap.put("clientIp", baseRequestHeader.getClientIp());
        headersMap.put("User-Agent", baseRequestHeader.getUserAgent());
        return headersMap;
    }

    /**
     * 获取requestHeader的内容
     */
    public static BaseRequestHeader getBaseHeader(HttpServletRequest request){
        return new BaseRequestHeader(request.getHeader("clientId"),request.getHeader("clientTime"),getRealIpAddress(request),request.getHeader("User-Agent"),request.getHeader("s"),request.getHeader("token"));
    }

    public static BaseRequest createBaseRequest(HttpServletRequest request, HandlerMethod handlerMethod){
        BaseRequest baseRequest = new BaseRequest();
        baseRequest.setBaseRequestHeader(getBaseHeader(request));
        baseRequest.setParamJson(getParamJsonFromRequest(request));
        baseRequest.setBodyString(getBodyContent(request));
        baseRequest.setControllerClass(handlerMethod.getBeanType());
        baseRequest.setRequestMethod(handlerMethod.getMethod());
        return baseRequest;
    }

}
