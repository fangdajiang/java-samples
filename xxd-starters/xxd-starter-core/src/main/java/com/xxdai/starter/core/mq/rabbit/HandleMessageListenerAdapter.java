package com.xxdai.starter.core.mq.rabbit;

import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.Channel;
import com.xxdai.pub.common.model.BasicMqMessage;
import com.xxdai.pub.common.util.SpringUtil;
import com.xxdai.pub.constant.Global;
import com.xxdai.starter.core.config.property.RabbitMqProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.xxdai.starter.core.config.RabbitMqConfig.DEAD_EXCHANGE;
import static org.junit.Assert.assertNotNull;

/**
 *
 * 监听消息的处理适配器
 *
 * ref: http://blog.51cto.com/shangdc/1945974
 *
 * Created by fangdajiang on 2018/11/9.
 */
@Component("handleMessageListenerAdapter")
@Slf4j
public class HandleMessageListenerAdapter extends MessageListenerAdapter {
    private RabbitMsgSender rabbitMsgSender;
    private RabbitMqProperties rabbitMqProperties;
    public HandleMessageListenerAdapter(RabbitMsgSender rabbitMsgSender, RabbitMqProperties rabbitMqProperties) {
        this.rabbitMsgSender = rabbitMsgSender;
        this.rabbitMqProperties = rabbitMqProperties;
    }

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        String messageBody = new String(message.getBody(), Global.DEFAULT_CHARSET);
        log.debug("消息消费:{}", messageBody);

        if (isOutRetryTime(message)) {
            JSONObject msgJson = (JSONObject)JSONObject.toJSON(message);
            rabbitMsgSender.sendMessage(DEAD_EXCHANGE, "", msgJson);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
            log.warn("message[{}] is out of retry time,please check it in the deadQueue",messageBody);
        } else {
            try{
                RabbitMsgHandler rabbitMsgHandler = obtainHandler(message);
                assertNotNull("bean class [" + message.getMessageProperties().getReceivedExchange() + "] not found", rabbitMsgHandler);
                rabbitMsgHandler.handlerMsg(JSONObject.parseObject(messageBody,BasicMqMessage.class).getMessageJson());
                channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
            }catch (Exception e){
                log.error("err occurred when handler message[{}]",messageBody,e);
                channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,false);
            }
        }

        // 手动ACK
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    private RabbitMsgHandler obtainHandler(Message message) {
        String exchange = message.getMessageProperties().getReceivedExchange();
        String consumerQueue = message.getMessageProperties().getConsumerQueue();
        String originalQueue = consumerQueue.replaceFirst(exchange+"\\.","");
        Class<? extends RabbitMsgHandler> handlerClass = rabbitMqProperties.getHandlerBean(exchange,originalQueue);
        return SpringUtil.getBean(handlerClass);
    }

    private boolean isOutRetryTime(Message message){
        Map<String,Object> headers = message.getMessageProperties().getHeaders();
        Object o = headers.get("x-death");
        if(o != null && o instanceof ArrayList){
            List<Map<String,Object>> list = (List) o;
            if(list.size() > 0 && list.get(0) instanceof HashMap){
                Map<String,Object> map = list.get(0);
                int i = Integer.parseInt(map.get("count").toString());
                return i >= rabbitMqProperties.getMaxRetryTime();
            }else{
                return false;
            }
        }else{
            return false;
        }
    }

}