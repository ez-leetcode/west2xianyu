package com.west2xianyu.controller;


import com.alibaba.fastjson.JSONObject;
import com.west2xianyu.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class UserController {









    @GetMapping("/user")
    public JSONObject getUser(@RequestParam("id") String id){
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
