package com.xxdai.starter.sample.service.impl;

import com.xxdai.pub.common.model.BaseResponse;
import com.xxdai.starter.sample.dao.DemoForestDao;
import com.xxdai.starter.sample.service.DemoForestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.xxdai.pub.common.util.ResponseUtil.map2BaseResponse;

/**
 * Created by fangdajiang on 2018/8/31.
 */
@Slf4j
@Service
public class DemoForestServiceImpl implements DemoForestService {

    private @Autowired
    DemoForestDao demoForestDao;

    @Override
    public String acquireDwz(String url) {
        Map<String, Object> result = demoForestDao.acquireDuanWangZhiUrl(url);
        return (String)result.get("tinyurl");
    }

    @Override
    public BaseResponse accessInvestmentApi(String userId, String pType, String status, String currentPage, String pageSize) {
        Map<String, Object> result = demoForestDao.multiParamsHttp(createToken(userId),pType, status, currentPage, pageSize);
        log.debug("result:{}", result);
        return map2BaseResponse(result);
    }

    @Override
    public String createToken(String userId) {
        Map<String, Object> result = demoForestDao.obtainToken(userId);
        log.debug("result:{}", result);
        BaseResponse baseResponse = map2BaseResponse(result);
        return (String)baseResponse.getData();
    }

    @Override
    public void foo() {
        log.debug("method foo is called.");
    }
}
