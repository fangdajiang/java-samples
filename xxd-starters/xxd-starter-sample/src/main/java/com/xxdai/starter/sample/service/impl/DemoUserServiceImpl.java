package com.xxdai.starter.sample.service.impl;

import com.xxdai.starter.sample.model.DemoUser;
import com.xxdai.starter.sample.service.DemoUserService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fangdajiang on 2018/9/25.
 */
@Service
public class DemoUserServiceImpl implements DemoUserService {
    private static List<DemoUser> users = new ArrayList<>();

    private void initDemoUsers() {
        users.add(new DemoUser(1, "Peter", 15));
        users.add(new DemoUser(2, "Jacob", 25));
        users.add(new DemoUser(3, "Matt", 35));
        users.add(new DemoUser(4, "John", 45));
    }

    @Override
    public List<DemoUser> getAllUsers() {
        if (users.isEmpty()) {
            initDemoUsers();
        }
        return users;
    }

    @Override
    public void postUser(DemoUser demoUser) {
        users.add(demoUser);
    }

    @Override
    public void putUser(Integer id, DemoUser demoUser) {
        DemoUser u = users.get(id);
        u.setName(demoUser.getName());
        u.setAge(demoUser.getAge());
        users.set(id,u);
    }

    @Override
    public DemoUser getUser(Integer id) {
        return users.get(id);
    }

    @Override
    public void deleteUser(Integer id) {
        users.remove(this.getUser(id));
    }
}
