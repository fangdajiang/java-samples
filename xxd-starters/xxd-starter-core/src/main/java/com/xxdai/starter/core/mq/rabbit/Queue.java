package com.xxdai.starter.core.mq.rabbit;

import lombok.Data;

@Data
public class Queue{
	private String desc;
	private boolean listen;
	private Class<? extends RabbitMsgHandler> handler;
}