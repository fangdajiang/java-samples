package com.xxdai.starter.sample.service;

import com.xxdai.starter.sample.model.DemoUser;

import java.util.List;

/**
 * Created by fangdajiang on 2018/9/25.
 */
public interface DemoUserService {

    List<DemoUser> getAllUsers();

    void postUser(DemoUser demoUser);

    void putUser(Integer id, DemoUser demoUser);

    DemoUser getUser(Integer id);

    void deleteUser(Integer id);
}
