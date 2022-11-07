package com.xxdai.starter.sample.dao;

import org.forest.annotation.DataParam;
import org.forest.annotation.DataVariable;
import org.forest.annotation.Request;

import java.util.Map;

/**
 *
 * @author fangdajiang
 * @date 2018/4/18
 */
public interface DemoForestDao {

    /**
     * 访问短网址示例
     * @param url 原 url
     * @return 包含缩短后的 url 的 map
     */
    @Request(
            url = "http://dwz.cn/create.php",
            type = "post",
            dataType = "json"
    )
    Map<String, Object> acquireDuanWangZhiUrl(@DataParam("url") String url);

    /**
     * 访问新新贷 investmentAPI api 示例
     * @param token token
     * @param pType 产品类型
     * @param status 产品状态
     * @param currentPage 第几页
     * @param pageSize 每页条数
     * @return map
     */
    @Request(
            headers = {"clientId:XXD_FRONT_END", "clientTime:1459845047000", "token:${token}"},
            url = "http://dev.xxd.com/investmentAPI/invest/expireHoldList",
            type = "get",
            dataType = "json"
    )
    Map<String, Object> multiParamsHttp(@DataVariable("token") String token,
                        @DataParam("productType") String pType,
                        @DataParam("status") String status,
                        @DataParam("currentPage") String currentPage,
                        @DataParam("pageSize") String pageSize);

    /**
     * 获取 token
     * @param userId 用户 id
     * @return 包含 token 的 map
     */
    @Request(
            headers = {"clientId:XXD_FRONT_END", "clientTime:1459845047000"},
            url = "http://dev.xxd.com/userCenter/user/token/createToken",
            type = "get",
            dataType = "json"
    )
    Map<String, Object> obtainToken(@DataParam("userId") String userId);
}
