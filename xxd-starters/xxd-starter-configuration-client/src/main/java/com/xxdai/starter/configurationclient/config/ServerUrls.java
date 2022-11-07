package com.xxdai.starter.configurationclient.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by yq on 2017/3/22.
 */
 @Data @NoArgsConstructor @AllArgsConstructor
public class ServerUrls {
    private String loanApi;
    private String interationPlatform;
    private String investmentAPI;
    private String activityCenter;
    private String payGate;
    private String tradeCenter;
    private String loanRelationshipCenter;
    private String fileCenter;
    private String webServiceModule;
    private String tradeWsModule;
    private String szrUrl;
    private String szrCreditUrl;
    private String userCenterUrl;
    private String messageCenter;
}
