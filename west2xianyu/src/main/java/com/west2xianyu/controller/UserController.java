package com.west2xianyu.controller;


import com.alibaba.fastjson.JSONObject;
import com.west2xianyu.pojo.User;
import com.west2xianyu.service.UserService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@Api(tags = "用户控制类",protocols = "https")
@Slf4j
@RestController
public class UserController {


    @Autowired
    private UserService userService;


    @GetMapping("/test")
    //注释用户名
    public String test(@ApiParam("用户名") User user){
        return "test" + user.toString();
    }


    @ApiOperation(value = "注册帐号请求",notes = "管理员注册请多带一个isAdministrator为1")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "用户学号",required = true,dataType = "string"),
            @ApiImplicitParam(name = "password",value = "用户密码",required = true,dataType = "string"),
            @ApiImplicitParam(name = "isAdministrator",value = "是否是管理员",required = true,dataType = "int")

    })
    @ApiResponse(code = 200, message = "返回registerStatus，repeatWrong：用户名重复，verifyWrong：验证码错误，success：成功")
    @PostMapping("/register")
    public JSONObject register(User user){
        JSONObject jsonObject = new JSONObject();
        String status = userService.register(user);
        if(status.equals("repeatWrong") || status.equals("verifyWrong")){
            jsonObject.put("registerStatus",status);
            return jsonObject;
        }
        log.info("注册成功，用户id：" + user.getId());
        jsonObject.put("registerStatus","success");
        return jsonObject;
    }


    //4.1
    @ApiOperation(value = "登录账号请求",notes = "登录成功会返回一个token，接下来登录时需要带上~")
    @PostMapping("/login")
    public JSONObject login(User user){
        JSONObject jsonObject = new JSONObject();
        return jsonObject;
    }



    @ApiOperation(value = "获取用户信息")
    @ApiImplicitParam(name = "id",value = "用户学号",required = true,dataType = "string")
    @GetMapping("/user")
    public JSONObject getUser(@RequestParam("id") String id){
        log.info("正在获取用户信息，id：" + id);
        JSONObject jsonObject = new JSONObject();
        User user = userService.getUser(id);
        if(user == null){
            log.warn("获取用户信息失败，用户不存在：" + id);
            jsonObject.put("getUserStatus","userWrong");
            return jsonObject;
        }
        log.info("获取用户信息成功，用户：" + user.toString());
        jsonObject.put("getUserStatus","success");
        jsonObject.put("user",user);
        return jsonObject;
    }

    @ApiOperation(value = "用于修改界面，保存用户信息",notes = "必带id，可以修改：username,sex,campus,address,email,phone,introduction")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "用户学号",required = true,dataType = "string"),
            @ApiImplicitParam(name = "username",dataType = "string"),
            @ApiImplicitParam(name = "password",value = "密码(MD5加密)",dataType = "string"),
            @ApiImplicitParam(name = "sex",value = "性别M/W",dataType = "string"),
            @ApiImplicitParam(name = "campus",value = "校区",dataType = "string"),
            @ApiImplicitParam(name = "address",dataType = "string"),
            @ApiImplicitParam(name = "email",dataType = "string"),
            @ApiImplicitParam(name = "phone",dataType = "string"),
            @ApiImplicitParam(name = "introduction",value = "不超过200字",dataType = "string")
    })
    @PostMapping("/user")
    public JSONObject saveUser(User user){
        log.info("正在保存用户信息，id：" + user.getId());
        JSONObject jsonObject = new JSONObject();
        int result = userService.saveUser(user);
        if(result == 1){
            log.info("修改成功");
            jsonObject.put("saveUserStatus","success");
        }else{
            log.info("修改失败，可能是提交重复信息所致");
            jsonObject.put("saveUserStatus","repeatWrong");
        }
        return jsonObject;
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "number",value = "闲置物品编号",required = true,paramType = "long"),
            @ApiImplicitParam(name = "id",paramType = "string")
    })
    @ApiOperation(value = "用户添加闲置物品到购物车",notes = "闲置物品被冻结")
    @PostMapping("/shopping")
    public JSONObject addShopping(@RequestParam("number") Long number,@RequestParam("id") String id){
        JSONObject jsonObject = new JSONObject();
        String status = userService.addShopping(number,id);
        jsonObject.put("addShoppingStatus",status);
        return jsonObject;
    }
}