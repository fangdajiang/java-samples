package com.xxdai.starter.core.web.interceptor;

import com.xxdai.pub.common.model.BaseRequest;
import com.xxdai.pub.common.model.BaseRequestHeader;
import com.xxdai.pub.common.model.BaseResponse;
import com.xxdai.pub.common.util.RequestUtil;
import com.xxdai.pub.constant.Global;
import com.xxdai.starter.core.test.BaseTest;
import com.xxdai.starter.core.web.controller.VersionSampleController;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * created by xiguoding on 2018/4/10 下午4:11
 */
@RunWith(PowerMockRunner.class)
@Slf4j
@PrepareForTest({RequestUtil.class})
public class CommonInterceptorTest extends BaseTest {
    private final static String BACKEND_URL = "http://localhost:8080/xxd-v2/account/";
    private CommonInterceptor commonInterceptor = new CommonInterceptor();

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private BaseRequest baseRequest;
    private BaseResponse baseResponse = new BaseResponse();

    @Test
    public void preHandleStaticResourceTest() throws Exception {
        ResourceHttpRequestHandler handler = mock(ResourceHttpRequestHandler.class);
        boolean result = commonInterceptor.preHandle(request, response, handler);
        assertTrue(result);
    }
    @Test
    public void preHandleForStaticResourceHandlerTest() throws Exception {
        HandlerMethod handler = mock(HandlerMethod.class);
        when(request.getAttribute(Global.BASE_REQUEST_ATTR_KEY)).thenReturn(baseRequest);
        when(baseRequest.getBaseRequestHeader()).thenReturn(new BaseRequestHeader());
        when(request.getAttribute(Global.BASE_RESPONSE_ATTR_KEY)).thenReturn(baseResponse);
        PowerMockito.mockStatic(RequestUtil.class);
        doReturn(VersionSampleController.class).when(handler).getBeanType();
        when(handler.getMethod()).thenReturn(VersionSampleController.class.getMethod("hello"));
        boolean result = commonInterceptor.preHandle(request, response, handler);
        assertTrue(result);
    }

    @Test
    public void testInterceptor() {
        ResponseEntity<String> result = restTemplateExchange();
        log.debug("result:{}", result);
    }

    @Override
    protected String getBackendUrlForRestTemplate() {
        return BACKEND_URL;
    }

    @Override
    protected MultiValueMap<String, String> getForParamsMultiValueMapRestTemplate() {
        return null;
    }

    @Override
    protected ResponseEntity<String> restTemplateExchange() {
        return accessBackEndUrlByRestTemplate(HttpMethod.GET);
    }

}
