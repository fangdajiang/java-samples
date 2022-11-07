package com.xxdai.starter.sample.service;

import com.xxdai.pub.common.model.BaseResponse;

import java.util.Map;

/**
 * Created by fangdajiang on 2018/8/31.
 */
public interface DemoForestService {

    String acquireDwz(String url);

    BaseResponse accessInvestmentApi(String userId, String pType, String status, String currentPage, String pageSize);

    String createToken(String userId);

    void foo();
}
