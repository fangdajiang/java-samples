package com.xxdai.starter.sample.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import static org.junit.Assert.assertTrue;

/**
 * Created by fangdajiang on 2018/9/25.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class DemoUserControllerWithRunningServer {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void getAllUsers() {
        String response = restTemplate.getForObject("/user/users", String.class);
        log.debug("response:{}", response);
        assertTrue(response.contains("John"));
    }
}
