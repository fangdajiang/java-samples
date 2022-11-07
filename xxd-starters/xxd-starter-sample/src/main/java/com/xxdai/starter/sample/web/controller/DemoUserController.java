package com.xxdai.starter.sample.web.controller;

import com.xxdai.starter.sample.model.DemoUser;
import com.xxdai.starter.sample.service.DemoUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 *
 * @author fangdajiang
 * @date 2018/5/28
 */
@RestController
@RequestMapping(value = "/user")
@Slf4j
public class DemoUserController {

    @Autowired
    private DemoUserService demoUserService;


    /**
     * 查询用户列表
     * @return
     */
    @GetMapping(value = "users")
    public ResponseEntity<?> getAllUsers(){
        List<DemoUser> userList = demoUserService.getAllUsers();
        log.debug("DemoUserController.getAllUsers:{}", userList);
        return ResponseEntity.ok(userList);
    }

    /**
     * 添加用户
     * @param user
     * @return
     */
    @PostMapping("post")
    public String post(@ModelAttribute DemoUser user){
        demoUserService.postUser(user);
        log.debug("UserController.post, user:{}", user);
        return "success";
    }

    /**
     * 更新用户
     * @param id
     * @param user
     * @return
     */
    @PutMapping("put/{id}")
    public String put(@PathVariable Integer id, @ModelAttribute DemoUser user){
        demoUserService.putUser(id, user);
        log.debug("UserController.put, id:{}, user:{}", id, user);
        return "success";
    }

    /**
     * 查询用户详情
     * @param id
     * @return
     */
    @GetMapping(value = "detail/{id}")
    public DemoUser detail(@PathVariable Integer id){
        log.debug("UserController.detail, id:{}", id);
        return demoUserService.getUser(id);
    }

    /**
     * 删除用户
     * @param id
     * @return
     */
    @DeleteMapping(value = "del/{id}")
    public String delete(@PathVariable Integer id){
        demoUserService.deleteUser(id);
        log.debug("UserController.delete, id:{}", id);
        return "success";
    }
}
