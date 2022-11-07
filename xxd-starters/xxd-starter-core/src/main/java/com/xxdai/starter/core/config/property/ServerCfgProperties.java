package com.xxdai.starter.core.config.property;

import com.xxdai.pub.common.model.ServerCfgObj;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import java.util.Map;

/**
 * Created by yq on 2017/4/8.
 */
@Configuration @Validated @Slf4j @Data
@ConfigurationProperties(prefix = "project.servercfg")
public class ServerCfgProperties {
    private Map<String,ServerCfgObj> servers;

    public ServerCfgObj getCfgObj(String serverId){
        ServerCfgObj serverCfgObj = null;
        if(null != this.servers){
            serverCfgObj = servers.get(serverId);
            if(null != serverCfgObj){
                if(StringUtils.isEmpty(serverCfgObj.getServerId())){
                    serverCfgObj.setServerId(serverId);
                }else ;
            } else;
        }else{
            log.warn("no any server-cfg info found in spring-application config file");
        }
        return serverCfgObj;
    }

    public ServerCfgObj getDefaultCfgObj(){
        for (Map.Entry<String, ServerCfgObj> entryServerCfgObj : this.servers.entrySet()) {
            ServerCfgObj obj = servers.get(entryServerCfgObj.getKey());
            if(obj.isEnableDefault()){
                if(StringUtils.isEmpty(obj.getServerId())){
                    obj.setServerId(entryServerCfgObj.getKey());
                }else ;
                return obj;
            }else ;
        }
        return null;
    }
}
