package com.xxdai.starter.core.test;

import com.xxdai.starter.sample.util.RequestDemo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Rule;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 *
 * 子类调用 restTemplateExchange() 即可
 *
 * @author fangdajiang
 * @date 2018/5/30
 */
@Slf4j
public abstract class BaseTest {
    protected MockMvc mockMvc;
    protected RestTemplate restTemplate = new RestTemplate();
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Before
    public void setupMockMvc() throws Exception {
//mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    protected abstract String getBackendUrlForRestTemplate();

    protected abstract MultiValueMap<String, String> getForParamsMultiValueMapRestTemplate();

    protected HttpHeaders getHttpHeaders() {
        return RequestDemo.getXxdHeaders();
    }

    /**
     * 默认调用 accessBackEndUrlByRestTemplate(HttpMethod.GET)
     * @return
     */
    protected ResponseEntity<String> restTemplateExchange() {
        return accessBackEndUrlByRestTemplate(HttpMethod.GET);
    }

    protected ResponseEntity<String> accessBackEndUrlByRestTemplate(HttpMethod httpMethod) {
        return restTemplate.exchange(
                RequestDemo.getFullUrl(getBackendUrlForRestTemplate(), getForParamsMultiValueMapRestTemplate()),
                httpMethod,
                new HttpEntity<>(getHttpHeaders()),
                String.class);
    }

}
