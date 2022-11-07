package com.xxdai.starter.core.service;

import com.xxdai.pub.common.exception.TokenExpiredException;
import com.xxdai.pub.common.model.TokenEntity;
import org.springframework.stereotype.Service;

/**
 * Created by fangdajiang on 2017/6/12.
 */
@Service
public class CommonTokenService extends TokenService {

    public TokenEntity validateTokenByUserAgent(String clientToken, String newUserAgent) throws IllegalAccessException,TokenExpiredException {
        return super.validateToken(clientToken, newUserAgent);
    }
}
