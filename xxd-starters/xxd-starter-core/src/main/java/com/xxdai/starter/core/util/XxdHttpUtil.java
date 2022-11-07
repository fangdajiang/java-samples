package com.xxdai.starter.core.util;

import com.alibaba.fastjson.JSONObject;
import com.xxdai.starter.core.exception.XxdRequestException;
import com.xxdai.pub.constant.Global;
import com.xxdai.pub.constant.ResultCode;
import com.xxdai.pub.common.exception.InvalidResponseException;
import com.xxdai.pub.common.model.BaseResponse;
import com.xxdai.pub.common.model.ServerCfgObj;
import com.xxdai.pub.common.util.comm.HttpUtil;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

/**
 * Created by yq on 2017/4/10.
 */
@Slf4j
public class XxdHttpUtil {

    private XxdHttpUtil() {}

    private static ServerCfgObj setDefaultIfNull(ServerCfgObj serverCfgObj){
        if(null == serverCfgObj){
            return ConfigurationUtil.getDefaultCfgObj();
        }else{
            return serverCfgObj;
        }
    }

    /**
     * XXD模块间请求GetRESTFul接口的方法
     * @param url 请求地址
     * @param serverCfgObj 请求客户端ID对象 为空时使用默认
     * @param headers 身份验证相关信息
     * @return 将返回信息转成Json后，取出data节点的jsonstr返回  为空则返回null
     * @throws InvalidResponseException 返回信息不能转换成json时抛出
     * @throws XxdRequestException 返回信息状态码不为200000时抛出
     */
    public static String sendHttpGet(@NonNull String url,ServerCfgObj serverCfgObj, Header... headers) throws InvalidResponseException, XxdRequestException {
        serverCfgObj = setDefaultIfNull(serverCfgObj);
        return getResponseData(HttpUtil.sendHttpGet(url, genHeader(serverCfgObj, null, headers).toArray(new Header[]{})), serverCfgObj);
    }

    /**
     * XXD模块间请求GetRESTFul接口的方法
     * @param url 请求地址
     * @param paramMap 请求参数,会转码后附加到URL后
     * @param serverCfgObj 请求客户端ID对象 为空时使用默认
     * @param headers 身份验证相关信息
     * @return 将返回信息转成Json后，取出data节点的jsonstr返回  为空则返回null
     * @throws InvalidResponseException 返回信息不能转换成json时抛出
     * @throws XxdRequestException 返回信息状态码不为200000时抛出
     */
    public static String sendHttpGet(@NonNull String url, Map<String,?> paramMap,ServerCfgObj serverCfgObj, Header... headers) throws InvalidResponseException, XxdRequestException {
        serverCfgObj = setDefaultIfNull(serverCfgObj);
        if(url.contains("?")){
            throw new XxdRequestException(ResultCode.ResponseCode.ILLEGAL_ARGS).concatInfo("url cannot has the \"?\" charactor");
        }else {
            if(!MapUtils.isEmpty(paramMap)){
                StringBuilder urlBuilder = new StringBuilder(url);
                urlBuilder.append("?");
                for (Map.Entry<String, ?> entry : paramMap.entrySet()) {
                    urlBuilder.append(entry.getKey() + "=");
                    try {
                        urlBuilder.append(URLEncoder.encode(paramMap.get(entry.getKey()).toString(),Global.DEFAULT_CHARSET));
                    } catch (UnsupportedEncodingException e) {
                        log.error("UnsupportedEncodingException when send Get,url:{},data:{}",url,paramMap,e);
                        throw new XxdRequestException(ResultCode.ResponseCode.UNKNOWN_EXCEPTION);
                    }
                    urlBuilder.append("&");
                }
                url = urlBuilder.toString();
                //去掉最后一个&
                url = url.substring(0,url.length()-1);
            }
            return getResponseData(HttpUtil.sendHttpGet(url, genHeader(serverCfgObj, null, headers).toArray(new Header[]{})), serverCfgObj);
        }
    }

    /**
     * XXD模块间请求PutRESTFul接口的方法
     * @param url 请求地址
     * @param data 提交的数据，会转成json后放到requestBody中提交  需有data根节点
     * @param serverCfgObj 请求客户端ID对象 为空时使用默认
     * @param headers 身份验证相关信息
     * @return 将返回信息转成Json后，取出data节点的jsonstr返回  为空则返回null
     * @throws InvalidResponseException 返回信息不能转换成json时抛出
     * @throws XxdRequestException 返回信息状态码不为200000时抛出
     */
    public static String sendHttpPut(@NonNull String url, Object data,ServerCfgObj serverCfgObj, Header... headers) throws InvalidResponseException, XxdRequestException {
        serverCfgObj = setDefaultIfNull(serverCfgObj);
        String jsonBodyString = createJsonBodyString(data);
        return getResponseData(HttpUtil.sendHttpPut(url, genHeader(serverCfgObj, jsonBodyString, headers).toArray(new Header[]{}), jsonBodyString), serverCfgObj);
    }

    /**
     * 请求PutRESTFul接口的方法
     * @param url 请求地址
     * @param headers 附加的header信息
     * @param paramMap 提交的数据，无需data根节点，会在请求时放入data根节点 并转成json后放到requestBody中提交
     * @return 返回请求接口的原始内容
     */
    public static String sendHttpPut(@NonNull String url, @NonNull Header[] headers, @NonNull Map<?, ?> paramMap) {
        Map<String, Object> dataMap = new HashMap<>(16);
        dataMap.put(Global.REQUEST_JSON_BODY_ROOT_NODE, paramMap);
        return HttpUtil.sendPlainHttpPut(url, headers, dataMap);
    }

    /**
     * XXD模块间请求PostRESTFul接口的方法
     * @param url 请求地址
     * @param data 提交的数据，会转成json后放到requestBody中提交  需有data根节点
     * @param serverCfgObj 请求客户端ID对象 为空时使用默认
     * @param headers 身份验证相关信息
     * @return 将返回信息转成Json后，取出data节点的jsonstr返回  为空则返回null
     * @throws InvalidResponseException 返回信息不能转换成json时抛出
     * @throws XxdRequestException 返回信息状态码不为200000时抛出
     */
    public static String sendHttpPost(@NonNull String url, Object data,ServerCfgObj serverCfgObj, Header... headers) throws InvalidResponseException, XxdRequestException {
        serverCfgObj = setDefaultIfNull(serverCfgObj);
        String jsonBodyString = createJsonBodyString(data);
        return getResponseData(HttpUtil.sendHttpPost(url, genHeader(serverCfgObj, jsonBodyString, headers).toArray(new Header[]{}), jsonBodyString), serverCfgObj);
    }

    /**
     * 请求PostRESTFul接口的方法
     * @param url 请求地址
     * @param headers 附加的header信息
     * @param paramMap 提交的数据，无需data根节点，会在请求时放入data根节点 并转成json后放到requestBody中提交
     * @return 返回请求接口的原始内容
     */
    public static String sendHttpPost(@NonNull String url, @NonNull Header[] headers, @NonNull Map<?, ?> paramMap) {
        Map<String, Object> dataMap = new HashMap<>(16);
        dataMap.put(Global.REQUEST_JSON_BODY_ROOT_NODE, paramMap);
        return HttpUtil.sendPlainHttpPost(url, headers, dataMap);
    }

    /**
     * XXD模块间请求PatchRESTFul接口的方法
     * @param url 请求地址
     * @param data 提交的数据，会转成json后放到requestBody中提交  需有data根节点
     * @param serverCfgObj 请求客户端ID对象 为空时使用默认
     * @param headers 身份验证相关信息
     * @return 将返回信息转成Json后，取出data节点的jsonstr返回  为空则返回null
     * @throws InvalidResponseException 返回信息不能转换成json时抛出
     * @throws XxdRequestException 返回信息状态码不为200000时抛出
     */
    public static String sendHttpPatch(@NonNull String url, Object data,ServerCfgObj serverCfgObj, Header... headers) throws InvalidResponseException, XxdRequestException {
        serverCfgObj = setDefaultIfNull(serverCfgObj);
        String jsonBodyString = createJsonBodyString(data);
        return getResponseData(HttpUtil.sendHttpPatch(url, genHeader(serverCfgObj, jsonBodyString, headers).toArray(new Header[]{}), jsonBodyString), serverCfgObj);
    }

    /**
     * 请求PatchRESTFul接口的方法
     * @param url 请求地址
     * @param headers 附加的header信息
     * @param paramMap 提交的数据，无需data根节点，会在请求时放入data根节点 并转成json后放到requestBody中提交
     * @return 返回请求接口的原始内容
     */
    public static String sendHttpPatch(@NonNull String url, @NonNull Header[] headers, @NonNull Map<?, ?> paramMap) {
        Map<String, Object> dataMap = new HashMap<>(16);
        dataMap.put(Global.REQUEST_JSON_BODY_ROOT_NODE, paramMap);
        return HttpUtil.sendPlainHttpPatch(url, headers, dataMap);
    }

    /**
     * XXD模块间请求DeleteRESTFul接口的方法
     * @param url 请求地址
     * @param serverCfgObj 请求客户端ID对象 为空时使用默认
     * @param headers 身份验证相关信息
     * @return 将返回信息转成Json后，取出data节点的jsonstr返回  为空则返回null
     * @throws InvalidResponseException 返回信息不能转换成json时抛出
     * @throws XxdRequestException 返回信息状态码不为200000时抛出
     */
    public static String sendHttpDelete(@NonNull String url,ServerCfgObj serverCfgObj, Header... headers) throws InvalidResponseException, XxdRequestException {
        serverCfgObj = setDefaultIfNull(serverCfgObj);
        return getResponseData(HttpUtil.sendHttpDelete(url, genHeader(serverCfgObj, null, headers).toArray(new Header[]{})), serverCfgObj);
    }

    /**
     * 根据传入的data数据生成BodyString
     * @param data
     * @return
     */
    private static String createJsonBodyString(Object data) {
        Object dataNode = null;
        if (data == null) {
            dataNode = new JSONObject();
        } else if (data instanceof String && StringUtils.isEmpty((String) data)) {
            dataNode = new JSONObject();
        } else {
            dataNode = data;
        }
        return JSONObject.toJSONString(Collections.singletonMap(Global.REQUEST_JSON_BODY_ROOT_NODE, dataNode));
    }

    /**
     * 当 responseString 的 code 是成功时，把其中的 data 转为普通字符串/JSON字符串
     *
     * @return
     */
    private static String getResponseData(@NonNull String responseString,@NonNull ServerCfgObj serverCfgObj) throws InvalidResponseException, XxdRequestException {
        BaseResponse baseResponse = JSONObject.parseObject(responseString, BaseResponse.class);
        if(!baseResponse.isSuccess()){
            throw new XxdRequestException(baseResponse.getCode(),baseResponse.getMessage());
        }else ;
        return baseResponse.getData()==null?null:((baseResponse.getData() instanceof String) ? baseResponse.getData().toString() : (JSONObject.toJSONString(baseResponse.getData())));
/* 这里要检查 s 有一点麻烦，因为 s 等存储于 header 中，无法从 responseString 中解析出 header

        BaseRequestHeader baseRequestHeader = baseResponse.getRequest().getHeader();
        if (!serverCfgObj.getServerId().equals(baseRequestHeader.getClientId())) {
            String _msg = "clientId and serverId not match,responseString:[" + responseString + "]";
            log.warn(_msg);
            throw new InvalidResponseException(_msg);
        } else {
            String responseDataString = baseResponse.getData()==null?null:((baseResponse.getData() instanceof String) ? baseResponse.getData().toString() : (JSONObject.toJSONString(baseResponse.getData())));
            //暂时不验证serverSign
//            String assembleString = baseRequestHeader.getClientId() + baseRequestHeader.getServerTime() + responseDataString==null?"":responseDataString + serverCfgObj.getKey();
//            String responseSign = DigestUtils.md5Hex(assembleString);
//            if(!responseSign.equals(baseRequestHeader.getServerSign())){
//                String _msg = "invalid serverSign,responseString:[" + responseString + "]";
//                log.warn(_msg);
//                throw new InvalidResponseException(_msg);
//            }else{
            return responseDataString;
//            }
        }
*/
    }

    /**
     * 根据serverCfgObj生成请求header
     *
     * @param serverCfgObj
     * @param jsonBodyString
     * @param headers
     * @return
     */
    private static List<Header> genHeader(@NonNull ServerCfgObj serverCfgObj, String jsonBodyString, Header... headers) {
        String curMillis = String.valueOf(System.currentTimeMillis());
        List<Header> headerList = new ArrayList<Header>();
        headerList.add(new BasicHeader("clientId", serverCfgObj.getServerId()));
        headerList.add(new BasicHeader("clientTime", curMillis));
        headerList.add(new BasicHeader("s", genClientSign(serverCfgObj.getServerId(), curMillis, serverCfgObj.getKey(), jsonBodyString == null ? "" : jsonBodyString)));
        for (Header header : headers) {
            headerList.add(header);
        }
        return headerList;
    }

    private static String genClientSign(@NonNull String clientId, @NonNull String timeMillis, @NonNull String clientKey, String bodyString) {
        return DigestUtils.md5Hex(clientId + timeMillis + (bodyString == null ? "" : bodyString) + clientKey);
    }
}
