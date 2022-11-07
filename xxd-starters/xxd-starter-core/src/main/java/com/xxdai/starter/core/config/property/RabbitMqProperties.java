package com.xxdai.starter.core.config.property;

import com.xxdai.starter.core.mq.rabbit.Queue;
import com.xxdai.starter.core.mq.rabbit.RabbitMsgHandler;
import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by yq on 2017/4/20.
 */
@Data @Configuration @ConfigurationProperties(prefix = "rabbitmq") @Validated
@ConditionalOnProperty(prefix = "rabbitmq",name = "address")
public class RabbitMqProperties {
	@NotNull private String address;
	@NotNull private String username;
	@NotNull private String password;
	@NotNull private String virtualHost;
	@NotNull private Integer maxRetryTime;
	private Integer channelCacheSize;
	private Integer concurrentConsumers;
	private Integer maxConcurrentConsumers;
	private Integer prefetchCount;
	private Map<String,Map<String,Queue>> exchanges;

	public boolean containsExchange(String exchange){
		return null != exchange && exchanges.size() > 0 && exchanges.containsKey(exchange);
	}

	public boolean containsQueue(String queue){
		if(null == exchanges || exchanges.size() == 0){
			return false;
		}else{
			for (Map.Entry<String, Map<String,Queue>> entryMap : this.exchanges.entrySet()) {
				if(exchanges.get(entryMap.getKey()).containsKey(queue)){
					return true;
				}
			}
			return false;
		}
	}

	public org.springframework.amqp.core.Queue[] getListenedQueueArray(){
		if(null != this.exchanges && this.exchanges.size() > 0){
			List<org.springframework.amqp.core.Queue> queueList = new ArrayList<org.springframework.amqp.core.Queue>();
			for (Map.Entry<String, Map<String,Queue>> entryMap : this.exchanges.entrySet()) {
				Map<String,Queue> exchangeMap = this.exchanges.get(entryMap.getKey());
				for (Map.Entry<String, Queue> entryQueue : exchangeMap.entrySet()) {
					Queue queue = exchangeMap.get(entryQueue.getKey());
					if(queue.isListen()){
						queueList.add(new org.springframework.amqp.core.Queue(entryMap.getKey() + "." + entryQueue.getKey(),true));
					}else ;
				}

			}
			return queueList.toArray(new org.springframework.amqp.core.Queue[]{});
		}else{
			return null;
		}
	}

	public Class<? extends RabbitMsgHandler> getHandlerBean(String exchange, String queue){
		if(null != this.exchanges && null != this.exchanges.get(exchange) && null != this.exchanges.get(exchange).get(queue)){
			return this.exchanges.get(exchange).get(queue).getHandler();
		}else{
			return null;
		}
	}
}
