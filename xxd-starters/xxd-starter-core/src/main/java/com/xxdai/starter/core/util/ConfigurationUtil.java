package com.xxdai.starter.core.util;

import com.xxdai.starter.core.config.property.ServerCfgProperties;
import com.xxdai.pub.common.model.ServerCfgObj;
import com.xxdai.pub.common.util.SpringUtil;

/**
 * Created by fangdajiang on 2017/12/28.
 */
public class ConfigurationUtil {

    private ConfigurationUtil() {}

    public static ServerCfgObj getServerCfgObj(String clientId) {
        ServerCfgProperties serverCfgProperties = SpringUtil.getBean(ServerCfgProperties.class);
        if (clientId == null) {
            return serverCfgProperties.getDefaultCfgObj();
        } else {
            return serverCfgProperties.getCfgObj(clientId);
        }
    }

    /**
     * 复用性改进：改为调用 getServerCfgObj(null)
     * @return
     */
    public static ServerCfgObj getDefaultCfgObj() {
        return getServerCfgObj(null);
    }

}
