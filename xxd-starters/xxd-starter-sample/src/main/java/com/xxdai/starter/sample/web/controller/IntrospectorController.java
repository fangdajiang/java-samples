package com.xxdai.starter.sample.web.controller;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Map;

/**
 *
 * @author fangdajiang
 * @date 2018/7/31
 */
@RestController
@Slf4j
public class IntrospectorController {
    @Autowired
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    @RequestMapping({"intro"})
    @ApiOperation(value = "查看请求对应的 url, 类, 方法信息", notes = "introspection")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "header", name = "clientId", value = "客户端 ID", defaultValue = "XXD_FRONT_END", required = true, dataType = "string"),
            @ApiImplicitParam(paramType = "header", name = "clientTime", value = "客户端时间戳", defaultValue = "1459845047000", required = true, dataType = "string"),
            @ApiImplicitParam(paramType = "header", name = "s", value = "32 位校验码", defaultValue = "f1fd61c58ad2e29dc072403144bbe78c", required = true, dataType = "string")
    })
    public String intro(){
        StringBuilder sb = new StringBuilder();
        sb.append("URL").append("--").append("Class").append("--").append("Function").append('\n').append('\n');

        Map<RequestMappingInfo, HandlerMethod> map = requestMappingHandlerMapping.getHandlerMethods();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> m : map.entrySet()) {
            RequestMappingInfo info = m.getKey();
            HandlerMethod method = m.getValue();
            sb.append(info.getPatternsCondition()).append("--");
            sb.append(method.getMethod().getDeclaringClass()).append("--");
            sb.append(method.getMethod().getName()).append('\n');
        }
        return sb.toString();
    }

}
