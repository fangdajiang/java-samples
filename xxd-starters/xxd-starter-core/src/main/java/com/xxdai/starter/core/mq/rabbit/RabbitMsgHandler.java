package com.xxdai.starter.core.mq.rabbit;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by yq on 2017/4/20.
 */
public interface RabbitMsgHandler {
	void handlerMsg(JSONObject messageJson);
}
