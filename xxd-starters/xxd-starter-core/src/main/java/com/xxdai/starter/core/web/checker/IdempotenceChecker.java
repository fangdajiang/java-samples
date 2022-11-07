package com.xxdai.starter.core.web.checker;

import com.xxdai.starter.cache.model.AbstractStatusResolver;
import com.xxdai.starter.cache.model.IdempotenceStatusResolver;
import com.xxdai.starter.cache.model.RequestStatus;
import com.xxdai.starter.cache.service.StatusService;
import com.xxdai.starter.cache.service.impl.IdempotenceServiceImpl;
import com.xxdai.pub.constant.Global;
import com.xxdai.pub.common.exception.CipherException;
import com.xxdai.pub.common.exception.DataNotFoundException;
import com.xxdai.pub.common.model.BaseRequest;
import com.xxdai.pub.common.util.SpringUtil;
import com.xxdai.pub.common.util.cipher.DigestUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * 对请求加以幂等性检查。详细逻辑见 wiki。
 *
 *
 * @author fangdajiang
 * @date 2017/6/14
 */
@Component @Slf4j
public class IdempotenceChecker implements CommonChecker {

    @Autowired
    private StatusService statusService;

    @Override
    public boolean postCheck(HttpServletRequest request, HandlerMethod handlerMethod) {
        String idempotenceId = (String)request.getAttribute(Global.IDEMPOTENCE_ID);
        log.debug("ready to postCheck, idempotenceId:{}, statusService:{}", idempotenceId, statusService);
        if (idempotenceId == null) {
            log.info("idempotenceId is null, needless to postCheck");
        } else {
            request.removeAttribute(Global.IDEMPOTENCE_ID);
            if (statusService == null) {
                //preCheck 中曾经设置过，这里却仍然无法获得，只能再次设置，不知何故
                statusService = SpringUtil.getBean(IdempotenceServiceImpl.class);
            }
            log.debug("statusService re-required in postCheck:{}", statusService);
            try {
                log.debug("IdempotenceStatusResolver in postCheck:{}", statusService.getStatus(idempotenceId));
            } catch (DataNotFoundException dnfe) {
                log.error("DataNotFoundException in postCheck, idempotenceId:{}", idempotenceId, dnfe);
            }
            try {
                statusService.clearStatus(idempotenceId);
            } catch (DataNotFoundException dnfe) {
                log.warn("clearStatus ERROR, idempotenceId:{}", idempotenceId, dnfe);
            }
            if (log.isDebugEnabled()) {
                try {
                    //本句不应被打印，故设置为 warn
                    log.warn("IdempotenceStatusResolver after clearing:{}", statusService.getStatus(idempotenceId));
                } catch (DataNotFoundException dnfe) {
                    log.debug("idempotenceId:{} has been removed successfully.", idempotenceId);
                }
            }
        }
        return true;
    }

    /**
     * 幂等性检查（POST）
     */
    @Override
    public boolean preCheck(HttpServletRequest request,HandlerMethod handlerMethod) {
        if (isIdempotentMethod(request.getMethod())) {
            //忽略对幂等方法请求的检查，如 GET, PUT 等，故继续处理该请求
            log.debug("ignore idempotent method:{}", request.getMethod());
            return true;
        } else {
            BaseRequest baseRequest = (BaseRequest)request.getAttribute(Global.BASE_REQUEST_ATTR_KEY);
            String uniqueRequestId = createUniqueRequestId(baseRequest);
            log.debug("uniqueRequestId:{}", uniqueRequestId);
            if (statusService == null) {
                statusService = SpringUtil.getBean(IdempotenceServiceImpl.class);
            }
            try {
                log.debug("statusService:{}", statusService);
                AbstractStatusResolver idempotenceStatusResolver = statusService.getStatus(uniqueRequestId);
                if (!idempotenceStatusResolver.proceed()) {
                    //上一次请求的状态值为 INITIATED 且 未过期，表明前一次请求未结束，故不处理该请求
                    log.info("Found a INITIATED status request && it is NOT expired," +
                            " baseRequest:{}, IdempotenceStatusResolver:{}", baseRequest, idempotenceStatusResolver);
                    return false;
                } else {
                    //上一次请求的状态值为 FINISHED 或者 过期，表明有前一次请求，只是已结束或过期，故处理该请求
                    log.debug("Found a FINISHED status request or it is expired, baseRequest:{}, IdempotenceStatusResolver:{}",
                            baseRequest, idempotenceStatusResolver);
                    request.setAttribute(Global.IDEMPOTENCE_ID,uniqueRequestId);
                    return true;
                }
            } catch (DataNotFoundException dnfe) {
                //缓存中没有数据，表明该请求为第一次（或相隔一段时间后的新一次）
                log.debug("DataNotFoundException, baseRequest:{}, uniqueRequestId:{}", baseRequest, uniqueRequestId);
                statusService.putStatus(new IdempotenceStatusResolver(uniqueRequestId, RequestStatus.INITIATED));
                request.setAttribute(Global.IDEMPOTENCE_ID,uniqueRequestId);
                return true;
            } catch (AssertionError ae) {
                log.error("ERROR, baseRequest:{}", baseRequest, ae);
                return false;
            }
        }
    }

    private boolean isIdempotentMethod(String methodName) {
        return !methodName.equals(RequestMethod.PUT.name());
    }

    /**
     * 根据 baseHeader 的各属性（不包括 clientTime）生成该 request 唯一 ID
     */
    private String createUniqueRequestId(BaseRequest baseRequest) {
        String returnValue = null;
        StringBuilder sb = new StringBuilder(256);
        String paramsJson = baseRequest.getParamJson().toJSONString();
        log.debug("paramsJson:{}", paramsJson);
        sb.append(baseRequest.getBaseRequestHeader().getClientId()).append(paramsJson);
        if (StringUtils.isNotBlank(baseRequest.getBodyString())) {
            log.debug("body is not empty:{}", baseRequest.getBodyString());
            sb.append(baseRequest.getBodyString());
        }
        sb.append(baseRequest.getControllerClass().toString());
        sb.append(baseRequest.getRequestMethod().toString());
        log.debug("sb:{}", sb);
        try {
            returnValue = DigestUtil.md5ToHex(sb.toString());
        } catch (CipherException e) {
            log.warn("baseRequest:{}", baseRequest, e);
        }
        return returnValue;
    }
}
