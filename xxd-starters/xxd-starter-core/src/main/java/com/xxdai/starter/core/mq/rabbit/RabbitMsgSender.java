package com.xxdai.starter.core.mq.rabbit;

import com.alibaba.fastjson.JSONObject;
import com.xxdai.pub.common.model.BasicMqMessage;
import com.xxdai.starter.core.config.RabbitMqConfig;
import com.xxdai.starter.core.config.property.RabbitMqProperties;
import com.xxdai.starter.core.exception.RabbitSendException;
import lombok.Data;
import lombok.NonNull;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import static org.junit.Assert.assertTrue;

/**
 *
 * Created by yq on 2017/4/20.
 */
@Data
@Component
public class RabbitMsgSender {
	@NonNull private RabbitTemplate rabbitTemplate;
	@NonNull private RabbitMqProperties rabbitMqProperties;

	public void sendMessage(@NonNull String exchange,@NonNull String queue,@NonNull JSONObject message) throws RabbitSendException {
		assertTrue("未找到路由器["+exchange+"]", rabbitMqProperties.containsExchange(exchange));
		assertTrue("未找到队列["+queue+"]", rabbitMqProperties.containsQueue(queue));
		rabbitTemplate.convertAndSend(exchange,
				exchange + "." + queue,
				new BasicMqMessage(message));
	}

	public void sendMessage(@NonNull String queue,@NonNull JSONObject message) throws RabbitSendException {
		sendMessage(RabbitMqConfig.ORDERS_EXCHANGE, queue, message);
	}
}
