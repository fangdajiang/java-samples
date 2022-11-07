package com.xxdai.starter.sample.web.controller;

import com.xxdai.starter.sample.model.DemoUser;
import com.xxdai.starter.sample.service.DemoUserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.Collections;

import static org.junit.Assert.assertTrue;

/**
 * refer: https://www.cnblogs.com/ywjy/p/9526185.html
 * 不构建整个 Spring Context，只构建指定的 Controller 进行测试。需要对相关的依赖进行mock.
 *
 * Created by fangdajiang on 2018/9/25.
 */
@RunWith(SpringRunner.class)
@WebMvcTest(DemoUserController.class)
@Slf4j
public class DemoUserControllerWebLayerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private DemoUserService service;

    @Test
    public void getAllUsers() throws Exception {
        DemoUser user = new DemoUser();
        user.setId(1);
        user.setAge(30);
        user.setName("John");

        Mockito.when(service.getAllUsers()).thenReturn(Collections.singletonList(user));

        assertTrue(mvc.perform(MockMvcRequestBuilders.get("/user/users"))
                .andDo(MockMvcResultHandlers.print())
                .andReturn()
                .getResponse()
                .getContentAsString()
                .contains("John"));
//                .andExpect(MockMvcResultMatchers.content().string(Matchers.contains("John")));
    }

}