package com.xxdai.starter.core.mq.rabbit;

import com.alibaba.fastjson.JSONObject;
import com.xxdai.starter.core.exception.RabbitSendException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by key on 2017/4/19.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class RabbitMsgSenderTest {
	@Autowired
	private RabbitMsgSender rabbitMsgSender;

	@Test(expected = RabbitSendException.class)
	public void test1() throws RabbitSendException {
		rabbitMsgSender.sendMessage("testExchange","testQueue",(JSONObject) JSONObject.toJSON(new ServerUrls("aa","b","c","d","e","f","g","h","i","j","i","1","k","m")));
	}
}
