package com.xxdai.starter.core.exception;

/**
 * Created by yq on 2017/4/20.
 */
public class RabbitSendException extends Exception{
	public RabbitSendException(String msg){
		super(msg);
	}

	public RabbitSendException(Exception e){
		super(e);
	}
}
