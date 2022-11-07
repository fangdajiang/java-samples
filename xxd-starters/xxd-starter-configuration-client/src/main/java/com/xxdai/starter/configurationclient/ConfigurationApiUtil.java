package com.xxdai.starter.configurationclient;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xxdai.pub.constant.Global;
import com.xxdai.pub.common.exception.InvalidResponseException;
import com.xxdai.pub.common.model.ServerCfgObj;
import com.xxdai.pub.common.util.SpringUtil;
import com.xxdai.starter.configurationclient.config.ServerUrls;
import com.xxdai.starter.configurationclient.config.property.UrlMapProperties;
import com.xxdai.starter.configurationclient.model.DictionaryItem;
import com.xxdai.starter.core.exception.XxdRequestException;
import com.xxdai.starter.core.util.ConfigurationUtil;
import com.xxdai.starter.core.util.XxdHttpUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Created by yq on 2017/3/22.
 */
public class ConfigurationApiUtil {
    private transient final static Logger LOGGER = LoggerFactory.getLogger(ConfigurationApiUtil.class);
    private static String CONFIGURATION_URL = null;
    private static final String SYSCONFIG_URI = "/sysconfig";
    private static final String SERVERURL_URI = "/serverUrl";
    private static final String REDISMSG_URI = "/RedisMsg";

    static{
        UrlMapProperties urlMapProperties = SpringUtil.getBean(UrlMapProperties.class);
        CONFIGURATION_URL = urlMapProperties.getConfigurationApi().get(SpringUtil.getActiveProfile());
    }

    private static ServerCfgObj setDefaultIfNull(ServerCfgObj serverCfgObj){
        if(null == serverCfgObj){
            return ConfigurationUtil.getDefaultCfgObj();
        }else{
            return serverCfgObj;
        }
    }
    /**
     * 根据字典编码获取字典项列表
     * @param typeCode 字典编码
     * @param serverCfgObj 请求身份ID  为空时使用默认
     */
    public static List<DictionaryItem> getDictionaryItemList(String typeCode, ServerCfgObj serverCfgObj){
        serverCfgObj = setDefaultIfNull(serverCfgObj);
        if(!StringUtils.isBlank(typeCode)) {
            String requestUrl = CONFIGURATION_URL + "/dictionaryList?code=" + typeCode;
            try{
                String responseDataString = XxdHttpUtil.sendHttpGet(requestUrl,serverCfgObj);
                return JSONObject.parseObject(responseDataString).getJSONArray(Global.RETURN_KEY_LIST).toJavaList(DictionaryItem.class);
            }catch (Exception e){
                LOGGER.error("Error occured when sending dictionary request to configurationApi,url={},typeCode={}",requestUrl,typeCode,e);
            }
        }
        return null;
    }

    /**
     * 根据字典编码获取字典项Map
     * @param typeCode 字典编码
     * @param serverCfgObj 请求身份ID  为空时使用默认
     */
    public static Map<String,DictionaryItem> getDictionaryItemMap(String typeCode,ServerCfgObj serverCfgObj){
        serverCfgObj = setDefaultIfNull(serverCfgObj);
        if(!StringUtils.isBlank(typeCode)) {
            String requestUrl = CONFIGURATION_URL + "/dictionaryMap?code=" + typeCode;
            try{
                String responseDataString = XxdHttpUtil.sendHttpGet(requestUrl,serverCfgObj);
                return JSON.parseObject(responseDataString, Map.class);
            }catch (Exception e){
                LOGGER.error("Error occured when sending dictionary request to configurationApi,url={},typeCode={}",requestUrl,typeCode,e);
            }
        }
        return null;
    }


    /**
     * 根据字典编码获取字典项
     * @param typeCode 字典编码
     * @param serverCfgObj 请求身份ID  为空时使用默认
     */
    public static DictionaryItem getDictionaryItem(String typeCode,String key,ServerCfgObj serverCfgObj){
        serverCfgObj = setDefaultIfNull(serverCfgObj);
        if(!StringUtils.isBlank(typeCode)) {
            String requestUrl = CONFIGURATION_URL + "/dictionaryItem?code=" + typeCode + "&key=" + key;
            try{
                String responseDataString = XxdHttpUtil.sendHttpGet(requestUrl,serverCfgObj);
                return JSON.parseObject(responseDataString, DictionaryItem.class);
            }catch (Exception e){
                LOGGER.error("Error occured when sending dictionary request to configurationApi,url={},typeCode={}",requestUrl,typeCode,e);
            }
        }
        return null;
    }


    /**
     * 根据字典编码获取字典项Map
     * @param typeCode 字典编码
     * @param key 字典项编码
     * @param serverCfgObj 请求身份ID  为空时使用默认
     */
    public static String getDictionaryItemValue(String typeCode,String key,ServerCfgObj serverCfgObj){
        serverCfgObj = setDefaultIfNull(serverCfgObj);
        if(!StringUtils.isBlank(typeCode)) {
            String requestUrl = CONFIGURATION_URL + "/dictionaryItemValue?code=" + typeCode + "&key=" + key;
            try{
                return XxdHttpUtil.sendHttpGet(requestUrl,serverCfgObj);
            }catch (Exception e){
                LOGGER.error("Error occured when sending dictionary request to configurationApi,url={},typeCode={}",requestUrl,typeCode,e);
            }
        }
        return null;
    }

    /**
     * 根据key获取SysConfig表中的value
     * @param key
     */
    public static String getSysConfigValue(String key,ServerCfgObj serverCfgObj){
        serverCfgObj = setDefaultIfNull(serverCfgObj);
        if(!StringUtils.isBlank(key)) {
            String requestUrl = CONFIGURATION_URL + SYSCONFIG_URI + "?key=" + key;
            try{
                return XxdHttpUtil.sendHttpGet(requestUrl,serverCfgObj);
            }catch (Exception e){
                LOGGER.error("Error occured when sending sysconfig request to configurationApi,url={},key={}",requestUrl,key,e);
            }
        }
        return null;
    }

    /**
     * 获取所有服务模块的请求地址
     * @param serverCfgObj  请求身份ID  为空时使用默认
     * @return
     */
    public static ServerUrls getServerUrls(ServerCfgObj serverCfgObj){
        serverCfgObj = setDefaultIfNull(serverCfgObj);
        String requestUrl = CONFIGURATION_URL + SERVERURL_URI;
        try{
            return JSONObject.parseObject(XxdHttpUtil.sendHttpGet(requestUrl+"?profile=" + SpringUtil.getActiveProfile(),serverCfgObj),ServerUrls.class);
        }catch (Exception e){
            LOGGER.error("Error occured when sending sysconfig request to configurationApi,url={},key={}",requestUrl,e);
            return null;
        }

    }

    /**
     * 发布Redis消息给订阅者
     * @param serverCfgObj  请求身份ID  为空时使用默认
     * @return
     */
    public static void sendRedisMsg(String topic, JSONObject messageJson, ServerCfgObj serverCfgObj) throws InvalidResponseException, XxdRequestException {
        serverCfgObj = setDefaultIfNull(serverCfgObj);
        String requestUrl = CONFIGURATION_URL + REDISMSG_URI;
        JSONObject requestDataJson = new JSONObject();
        requestDataJson.put("topic",topic);
        requestDataJson.put("messageJson",messageJson);
        XxdHttpUtil.sendHttpPost(requestUrl,requestDataJson,serverCfgObj);
    }

    public static ServerUrls getServerUrls(){
        return getServerUrls(null);
    }
    public static String getSysConfigValue(String key){
        return getSysConfigValue(key,null);
    }
    public static String getDictionaryItemValue(String typeCode,String key){
        return getDictionaryItemValue(typeCode,key,null);
    }
    public static DictionaryItem getDictionaryItem(String typeCode,String key){
        return getDictionaryItem(typeCode,key,null);
    }
    public static Map<String,DictionaryItem> getDictionaryItemMap(String typeCode){
        return getDictionaryItemMap(typeCode,null);
    }
    public static List<DictionaryItem> getDictionaryItemList(String typeCode){
        return getDictionaryItemList(typeCode,null);
    }
    public static void sendRedisMsg(String topic,JSONObject messageJson) throws InvalidResponseException, XxdRequestException {
        sendRedisMsg(topic,messageJson,null);
    }
}
