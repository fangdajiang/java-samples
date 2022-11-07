package com.xxdai.starter.core.config.property;

import com.xxdai.pub.common.model.ClientCfgObj;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.util.Map;

import static com.xxdai.pub.constant.Global.DEFAULT_CLIENT_ID;

/**
 *
 * @author yq
 * @date 2017/4/8
 */
@Validated @Slf4j @Data
@ConfigurationProperties(prefix = "project.clientcfg")
public class ClientCfgProperties {
    private String defaultKey = DEFAULT_CLIENT_ID;
    private Boolean checkSign = true;
    private @NotNull
    Map<String,ClientCfgObj> clients;

    public ClientCfgObj getCfgObj(String clientId){
        ClientCfgObj clientCfgObj = null;
        if(null != this.clients){
            clientCfgObj = clients.get(clientId);
            if(null != clientCfgObj){
                if(StringUtils.isEmpty(clientCfgObj.getClientId())){
                    log.info("clientCfgObj.getClientId() is empty, clientId:{} will be set in clientCfgObj", clientId);
                    clientCfgObj.setClientId(clientId + DEFAULT_CLIENT_ID);
                }
                if(StringUtils.isEmpty(clientCfgObj.getKey())){
                    log.info("clientCfgObj.getKey() is empty, key:{} will be set in clientCfgObj", defaultKey);
                    clientCfgObj.setKey(defaultKey);
                }
                if(null == clientCfgObj.getCheckSign()){
                    log.info("clientCfgObj.getCheckSign() is empty, checkSign:{} will be set in clientCfgObj", this.checkSign);
                    clientCfgObj.setCheckSign(this.checkSign);
                }
            } else {
                log.warn("clientCfgObj is null, clientId:{}", clientId);
            }
        }else{
            log.warn("no any client-cfg info found in spring-application config file");
        }
        return clientCfgObj;
    }
}
