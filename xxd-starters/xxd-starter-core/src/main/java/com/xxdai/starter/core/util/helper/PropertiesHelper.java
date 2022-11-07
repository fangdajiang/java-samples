package com.xxdai.starter.core.util.helper;

import com.xxdai.pub.common.model.ClientCfgObj;
import com.xxdai.pub.common.util.SpringUtil;
import com.xxdai.starter.core.config.property.ClientCfgProperties;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author fangdajiang
 * @date 2018/6/8
 */
@Slf4j
public class PropertiesHelper {

    private PropertiesHelper() {}

    public final static ClientCfgObj getClientCfgObj(String clientId) {
        ClientCfgProperties clientCfgProperties = SpringUtil.getBean(ClientCfgProperties.class);
        log.debug("clientCfgProperties:{}, clientId:{}", clientCfgProperties, clientId);
        ClientCfgObj clientCfgObj = clientCfgProperties.getCfgObj(clientId);
        return clientCfgObj;
    }
}
