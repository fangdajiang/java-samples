package com.xxdai.starter.cache.model;

import com.xxdai.pub.common.model.TokenEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor @NoArgsConstructor
public class UserCache implements Serializable {
    public UserCache(Long userId, String username) {
        this.userId = userId;
        this.username = username;
    }
    private TokenEntity tokenEntity;
    private Long userId;
    private String username;
    private String nickname;
    private String mobile;
    private String registerSource;
    private String referral;
    private Date expiryDate;
    private String terminalTypeName;
}
