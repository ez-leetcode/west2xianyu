package com.west2xianyu.handler;


import com.alibaba.fastjson.JSONObject;
import com.west2xianyu.mapper.UserMapper;
import com.west2xianyu.pojo.User;
import com.west2xianyu.utils.JwtUtils;
import com.west2xianyu.utils.RedisUtils;
import com.west2xianyu.utils.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


//登录成功后被调用
@Slf4j
@Component
public class MyAuthenticationSuccessHandler implements AuthenticationSuccessHandler {


    @Autowired
    private RedisUtils redisUtils;

    //登录成功
    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        httpServletResponse.setHeader("Content-Type", "application/json;charset=utf-8");
        PrintWriter printWriter = httpServletResponse.getWriter();
        User user = (User) authentication.getPrincipal();
        log.info(user.toString());

        //生成新的token
        String token = JwtUtils.createToken(user.getUsername(),user.getPassword());
        log.info("新生成token：" + token);
        //保存token
        redisUtils.saveByTime(user.getId(),token,8);
        //返回时把密码清空
        user.setPassword(null);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("token",token);
        //输出信息
        printWriter.write(ResultUtils.getResult(jsonObject, "loginSuccess").toString());
        printWriter.flush();
        printWriter.close();
    }

}
