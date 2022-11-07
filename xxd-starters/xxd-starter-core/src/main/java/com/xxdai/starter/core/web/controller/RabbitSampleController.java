package com.xxdai.starter.core.web.controller;

import com.xxdai.starter.core.service.RabbitSampleService;
import com.xxdai.starter.sample.model.DemoUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * ref: https://blog.battcn.com/2018/05/22/springboot/v2-queue-rabbitmq/
 *
 * Created by fangdajiang on 2018/11/8.
 */
@RestController
@RequestMapping(value = "/rabbit")
public class RabbitSampleController {
    @Autowired
    private RabbitSampleService rabbitSampleService;

    @RequestMapping(value = "demoUser", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String sendDemoUser() {
        DemoUser user = new DemoUser();
        user.setId(123);
        user.setName("David");
        user.setAge(30);

        rabbitSampleService.sendAutoAckMessage(user);
        rabbitSampleService.sendManualAckMessage(user);

        return "JSON user";
    }
}
