package com.xxdai.starter.core.util.helper;

import com.xxdai.starter.cache.model.UserCache;
import com.xxdai.starter.core.service.CommonTokenService;
import com.xxdai.pub.common.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author fangdajiang
 * @date 2018/2/27
 */
@Slf4j
public enum UserCacheHelper {
    APP() {
        @Override
        public UserCache get(String terminalTypeName, Long userId) {
            log.trace("APP get");
            return tokenService.getUserCacheForApp(terminalTypeName, userId);
        }
        @Override
        public void clear(String terminalTypeName, Long userId) {
            log.trace("APP clear");
            tokenService.clearUserCacheForApp(terminalTypeName, userId);
        }
        @Override
        public void put(UserCache userCache) {
            log.trace("APP put");
            tokenService.putUserCacheForApp(userCache);
        }
    },
    NON_APP() {
        @Override
        public UserCache get(String terminalTypeName, Long userId) {
            log.trace("NON-APP get");
            return tokenService.getUserCache(terminalTypeName, userId);
        }

        @Override
        public void clear(String terminalTypeName, Long userId) {
            log.trace("NON-APP clear");
            tokenService.clearUserCache(terminalTypeName, userId);
        }

        @Override
        public void put(UserCache userCache) {
            log.trace("NON-APP put");
            tokenService.putUserCache(userCache);
        }
    };
    CommonTokenService tokenService = SpringUtil.getBean(CommonTokenService.class);
    public abstract UserCache get(String terminalTypeName, Long userId);
    public abstract void clear(String terminalTypeName, Long userId);
    public abstract void put(UserCache userCache);
}

