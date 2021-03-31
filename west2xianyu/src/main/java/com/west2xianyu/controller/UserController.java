package com.west2xianyu.controller;


import com.alibaba.fastjson.JSONObject;
import com.west2xianyu.pojo.User;
import com.west2xianyu.service.MailService;
import com.west2xianyu.service.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@ApiOperation("用户控制类")
@Slf4j
@RestController
public class UserController {


    @Autowired
    private UserService userService;


    @Autowired
    private MailService mailService;



    @GetMapping("/test")
    //注释用户名
    public String test(@ApiParam("用户名") @RequestParam("user") User user){
        return "test" + user;
    }











    @GetMapping("/user")
    public JSONObject getUser(@RequestParam("id") String id){
        User user = new User();
        log.info("正在获取用户信息，id：" + id);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("getUserStatus","success");
        return jsonObject;
    }

    @PostMapping("/user")
    public JSONObject saveUser(@RequestParam("user") User user){
        log.info("正在保存用户信息，id：" + user.getId());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("saveUserStatus","success");
        return jsonObject;
    }


}
