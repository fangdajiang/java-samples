package com.xxdai.starter.core.exception;

import com.xxdai.pub.constant.ResultCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by yq on 2017/4/12.
 */
@Data @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class XxdRequestException extends Exception{
	private String code;
	private String message;

	public XxdRequestException(String message){
		super(message);
	}

	public XxdRequestException(ResultCode.ResponseCode responseCode){
		this.code = responseCode.getCode();
		this.message = responseCode.getMessage();
	}

	public XxdRequestException concatInfo(String info) {
		if(!StringUtils.isBlank(info)){
			this.message += "[" + info + "]";
		}else ;
		return this;
	}
}
