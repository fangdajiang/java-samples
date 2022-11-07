package com.xxdai.starter.core.web.controller;

import com.xxdai.pub.common.exception.BizException;
import com.xxdai.pub.common.exception.DataNotFoundException;
import com.xxdai.pub.common.exception.InvalidResponseException;
import com.xxdai.pub.common.exception.RepetitiveOperationException;
import com.xxdai.starter.core.exception.XxdRequestException;

import java.net.ConnectException;
import java.sql.SQLException;

/**
 * Created by fangdajiang on 2017/3/14.
 */
public interface BizCallback {
    /**
     * 回调接口
     * @return 业务逻辑对象
     */
    Object execute() throws IllegalArgumentException, IllegalStateException,
            IllegalAccessException, ConnectException, SQLException, UnsupportedOperationException,DataNotFoundException,RepetitiveOperationException,XxdRequestException, BizException, InvalidResponseException;
}
