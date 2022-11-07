package com.xxdai.starter.core.util.comm;

import com.alibaba.fastjson.JSONObject;
import com.xxdai.pub.common.model.BaseRequest;
import com.xxdai.pub.common.model.BaseResponse;
import com.xxdai.pub.common.model.ClientCfgObj;
import com.xxdai.pub.constant.Global;
import com.xxdai.starter.core.util.helper.PropertiesHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

import static com.xxdai.pub.constant.Global.DEFAULT_CLIENT_ID;
import static com.xxdai.pub.constant.Global.DEFAULT_CLIENT_KEY;

/**
 *
 * @author fangdajiang
 * @date 2018/6/22
 */
@Slf4j
public class HttpServletResponseUtil {
    private static final String NULL_RESPONSE_DATA = "";

    private HttpServletResponseUtil() {}

    public final static void writeResponseWhenError(BaseRequest baseRequest, BaseResponse baseResponse, HttpServletResponse response){
        response.setCharacterEncoding(Global.DEFAULT_CHARSET);
        response.setContentType("application/json; charset=utf-8");
        long currentTimeMillis = System.currentTimeMillis();
        response.addHeader("serverTime", String.valueOf(currentTimeMillis));
        String key = DEFAULT_CLIENT_KEY;
        String clientId = baseRequest.getBaseRequestHeader().getClientId();
        if (StringUtils.isEmpty(clientId)) {
            clientId = DEFAULT_CLIENT_ID;
        } else {
            ClientCfgObj clientCfgObj = PropertiesHelper.getClientCfgObj(clientId);
            key = Optional.ofNullable(clientCfgObj)
                    .map(c -> c.getClientId())
                    .map(cId -> clientCfgObj.getKey())
                    .orElseGet(() -> {
                        log.error("clientCfgObj is {}, clientId/key not set in yml? baseRequest:{}, baseResponse:{}", clientCfgObj, baseRequest, baseResponse);
                        return null;
                    });
            log.debug("clientId:{}, clientCfgObj:{}, key:{}", clientId, clientCfgObj, key);
        }
        response.addHeader("clientId", clientId);
        String assembleString = clientId + currentTimeMillis +
                (baseResponse.getData() == null ? NULL_RESPONSE_DATA : JSONObject.toJSONString(baseResponse.getData())) + key;
        log.info("baseRequest:{}, baseResponse:{}", baseRequest, baseResponse);
        log.info("assembleString when error:{}", assembleString);
        response.addHeader("s", DigestUtils.md5Hex(assembleString));
        PrintWriter out = null;
        try {
            out = response.getWriter();
            out.append(JSONObject.toJSONString(baseResponse));
        } catch (IOException e) {
            log.error("进行JsonResponse输出时发生错误, baseRequest:{}, baseResponse:{}", baseRequest, baseResponse, e);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }
}
