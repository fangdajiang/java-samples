package com.xxdai.starter.core.config;

import com.rabbitmq.client.Channel;
import com.xxdai.starter.core.config.property.RabbitMqProperties;
import com.xxdai.starter.core.mq.rabbit.HandleMessageListenerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;
import org.springframework.messaging.handler.annotation.support.MessageHandlerMethodFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * ref: https://sivalabs.in/2018/02/springboot-messaging-rabbitmq/
 * http://blog.51cto.com/shangdc/1945756
 *
 * Created by fangdajiang on 2018/11/8.
 */
@Slf4j
@Configuration
@ConditionalOnBean(RabbitMqProperties.class)
public class RabbitMqConfig implements RabbitListenerConfigurer {

    public static final String DEFAULT_DEMO_USER_QUEUE = "demo.user.default.queue";
    public static final String MANUAL_DEMO_USER_QUEUE = "demo.user.manual.queue";

    private static final String QUEUE_ORDERS = "orders-queue";
    private static final String QUEUE_DEAD_ORDERS = "dead-orders-queue";
    public static final String ORDERS_EXCHANGE = "orders-exchange";

    private final static String EXCHANGE_MODE_DIRECT = "direct";
    private final static String DEAD_LETTER_ARG_NAME = "x-dead-letter-exchange";
    public final static String DEAD_EXCHANGE = "deadExchange";

    private static final int X_MESSAGE_TTL = 5000;

    @Bean
    public SimpleMessageListenerContainer listenerContainer(
            @Qualifier("handleMessageListenerAdapter") HandleMessageListenerAdapter handleMessageListenerAdapter,
            ConnectionFactory connectionFactory,
            RabbitMqProperties rabbitMqProperties) throws Exception {
        //???????????????????????????
        SimpleMessageListenerContainer container =
                new SimpleMessageListenerContainer(connectionFactory);
        container.setQueues(rabbitMqProperties.getListenedQueueArray());
        container.setExposeListenerChannel(true);
        container.setMessageListener(handleMessageListenerAdapter);
        //??????????????????????????????????????????????????????????????????????????????????????????rabbitmq ??????????????????????????????????????????
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);

        if(null != rabbitMqProperties.getConcurrentConsumers()){
            container.setConcurrentConsumers(rabbitMqProperties.getConcurrentConsumers());
        }
        if(null != rabbitMqProperties.getMaxConcurrentConsumers()){
            container.setMaxConcurrentConsumers(rabbitMqProperties.getMaxConcurrentConsumers());
        }

        return container;
    }

    @Bean
    Queue ordersQueue() {
        return QueueBuilder.durable(QUEUE_ORDERS)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", QUEUE_DEAD_ORDERS)
                .withArgument("x-message-ttl", X_MESSAGE_TTL)
                .build();
    }

    @Bean
    Queue deadLetterQueue() {
        return QueueBuilder.durable(QUEUE_DEAD_ORDERS).build();
    }

    @Bean
    Exchange ordersExchange() {
        return ExchangeBuilder.topicExchange(ORDERS_EXCHANGE).build();
    }

    /**
     * ????????????????????????????????????????????????????????????????????????????????????
     */
/*
    @Bean
    Binding binding(Queue ordersQueue, TopicExchange ordersExchange) {
        log.info("Binding queue:{} to topic exchange:{}. Routing key:{}", ordersQueue, ordersExchange, QUEUE_ORDERS);
        return BindingBuilder.bind(ordersQueue).to(ordersExchange).with(QUEUE_ORDERS);
    }
*/

    /**
     * ???????????????
     * name: ??????????????????; durable: ???????????????; auto-delete: ??????????????????????????????????????????????????????????????????
     */
/*
    @Bean
    TopicExchange topicExchange() {
        log.info("Initiating the topic exchange");
        //????????? yml ???????????????
        String name = "tradeCenterExchange";
        boolean durable = true;
        boolean autoDelete = false;
        return new TopicExchange(name, durable, autoDelete);
    }
*/

    @Bean
    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(producerJackson2MessageConverter());
        return rabbitTemplate;
    }
    @Bean
    public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Override
    public void configureRabbitListeners(RabbitListenerEndpointRegistrar registrar) {
        registrar.setMessageHandlerMethodFactory(messageHandlerMethodFactory());
    }
    @Bean
    MessageHandlerMethodFactory messageHandlerMethodFactory() {
        DefaultMessageHandlerMethodFactory messageHandlerMethodFactory = new DefaultMessageHandlerMethodFactory();
        messageHandlerMethodFactory.setMessageConverter(consumerJackson2MessageConverter());
        return messageHandlerMethodFactory;
    }
    @Bean
    public MappingJackson2MessageConverter consumerJackson2MessageConverter() {
        return new MappingJackson2MessageConverter();
    }

    @Bean
    public AmqpAdmin amqpAdmin(CachingConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public ConnectionFactory connectionFactory(RabbitMqProperties rabbitMqProperties) {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setAddresses(rabbitMqProperties.getAddress());
        connectionFactory.setUsername(rabbitMqProperties.getUsername());
        connectionFactory.setPassword(rabbitMqProperties.getPassword());
        connectionFactory.setVirtualHost(rabbitMqProperties.getVirtualHost());
        if(null != rabbitMqProperties.getChannelCacheSize()) {
            connectionFactory.setChannelCacheSize(rabbitMqProperties.getChannelCacheSize());
        }
        //???????????????
        connectionFactory.setPublisherConfirms(true);

        initExchangesAndQueues(connectionFactory, rabbitMqProperties);

        return connectionFactory;
    }
    private void initExchangesAndQueues(ConnectionFactory connectionFactory, RabbitMqProperties rabbitMqProperties){
        log.info("starting init rabbit exchanges and queues......");
        if(null == rabbitMqProperties.getExchanges() || rabbitMqProperties.getExchanges().size() == 0){
            throw new RuntimeException("?????????rabbitMq??????????????????");
        }else{
            Connection connection = connectionFactory.createConnection();
            Channel channel = connection.createChannel(false);
            try{
                for(String exchange : rabbitMqProperties.getExchanges().keySet()){
                    Map<String,Object> arguments = new HashMap<>();
                    arguments.put( DEAD_LETTER_ARG_NAME,exchange);
                    channel.exchangeDeclare(exchange,EXCHANGE_MODE_DIRECT,true);
                    for(String queue : rabbitMqProperties.getExchanges().get(exchange).keySet()){
                        String queueName = exchange + "." + queue;
                        channel.queueDeclare(queueName,true,false,false,arguments);
                        channel.queueBind(queueName,exchange,queueName);
                    }
                }
                channel.exchangeDeclare(DEAD_EXCHANGE,EXCHANGE_MODE_DIRECT,true);
                channel.queueDeclare(QUEUE_DEAD_ORDERS,true,false,false,null);
                channel.queueBind(QUEUE_DEAD_ORDERS,DEAD_EXCHANGE,"");
            } catch (IOException e) {
                log.warn("connectionFactory:{}", connectionFactory, e);
            }finally {
                try {
                    channel.close();
                    connection.close();
                } catch (Exception e) {
                    log.error("error occurred when close Rabbit Channel and Connection",e);
                }
            }
        }
    }

}