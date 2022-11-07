package com.xxdai.starter.core.web.controller;

import com.xxdai.starter.core.web.annotation.ApiVersion;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Size;

/**
 * from: https://www.jianshu.com/p/5b820f393c62
 *
 * @author maskwang520
 * @date 2017/12/2
 */
@Api(tags = "API 多版本管理示例")
@RequestMapping("/demo/{version}/")
@RestController
@Slf4j
public class VersionSampleController {
    @RequestMapping(value = "hello", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiVersion(1F)
    @ApiOperation("获取 v[1.x, 2.0) 版本的hello")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "header", name = "clientId", value = "客户端 ID", defaultValue = "XXD_FRONT_END", required = true, dataType = "string"),
            @ApiImplicitParam(paramType = "header", name = "clientTime", value = "客户端时间戳", defaultValue = "1459845047000", required = true, dataType = "string"),
            @ApiImplicitParam(paramType = "header", name = "s", value = "32 位校验码", defaultValue = "f1fd61c58ad2e29dc072403144bbe78c", required = true, dataType = "string"),
            @ApiImplicitParam(paramType = "path", name = "version", value = "版本号，如 v1.x", required = true, dataType = "String")
    })
    public String hello(){
        return "hello version1";
    }

    @RequestMapping(value = "hello", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiVersion(2F)
    @ApiOperation(value = "获取 v[2.x, 5.0) 版本的hello", notes = "example")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "header", name = "clientId", value = "客户端 ID", defaultValue = "XXD_FRONT_END", required = true, dataType = "string"),
            @ApiImplicitParam(paramType = "header", name = "clientTime", value = "客户端时间戳", defaultValue = "1459845047000", required = true, dataType = "string"),
            @ApiImplicitParam(paramType = "header", name = "s", value = "32 位校验码", defaultValue = "f1fd61c58ad2e29dc072403144bbe78c", required = true, dataType = "string"),
            @ApiImplicitParam(paramType = "path", name = "version", value = "版本号，如 v2.x", required = true, dataType = "String")
    })
    public String hello2(){
        return "hello version2";
    }

    @RequestMapping(value = "hello", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiVersion(5F)
    @ApiOperation(value = "获取 v[5.x, 9.x) 版本的hello", notes = "example")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "header", name = "clientId", value = "客户端 ID", defaultValue = "XXD_FRONT_END", required = true, dataType = "string"),
            @ApiImplicitParam(paramType = "header", name = "clientTime", value = "客户端时间戳", defaultValue = "1459845047000", required = true, dataType = "string"),
            @ApiImplicitParam(paramType = "header", name = "s", value = "32 位校验码", defaultValue = "f1fd61c58ad2e29dc072403144bbe78c", required = true, dataType = "string"),
            @ApiImplicitParam(paramType = "path", name = "version", value = "版本号: v5.X", required = true, dataType = "String")
    })
    public String hello5(@ApiParam("给谁打招呼") @RequestParam @Size(max = 10) String toWhom){
        return "hello version5:" + toWhom;
    }
}
