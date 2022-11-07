package com.xxdai.starter.core.web.controller;

import com.xxdai.starter.core.test.BaseTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

/**
 * Created by fangdajiang on 2018/7/31.
 */
@Slf4j
public class VersionSampleControllerTest extends BaseTest {
    private static String BACKEND_URL = "http://localhost:8080/demo/v10/hello";

    private static void setBackendUrl(String url) {
        BACKEND_URL = url;
    }

    @Test
    public void getHello1() throws Exception {
        setBackendUrl("http://localhost:8080/demo/v1/hello");
        ResponseEntity<String> result = restTemplateExchange();
        log.debug("result:{}", result);
    }

    @Test
    public void getHello2() throws Exception {
        setBackendUrl("http://localhost:8080/demo/v2/hello");
        ResponseEntity<String> result = restTemplateExchange();
        log.debug("result:{}", result);
    }

    @Test
    public void getHello5() throws Exception {
        setBackendUrl("http://localhost:8080/demo/v5/hello");
        ResponseEntity<String> result = restTemplateExchange();
        log.debug("result:{}", result);
    }

    @Test
    public void getHello10() throws Exception {
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
}
