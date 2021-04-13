package com.west2xianyu.handler;


import com.alibaba.fastjson.JSONObject;
import com.west2xianyu.pojo.User;
import com.west2xianyu.utils.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


@Slf4j
@Component
public class MyAuthenticationSuccessHandler implements AuthenticationSuccessHandler {


    //登录成功
    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        httpServletResponse.setHeader("Content-Type","application/json;charset=utf-8");
        PrintWriter printWriter = httpServletResponse.getWriter();
        User user = (User) authentication.getPrincipal();
        log.info(user.toString());
        //返回时把密码清空
        user.setPassword(null);
        JSONObject jsonObject = new JSONObject();
        //输出信息
        printWriter.write(ResultUtils.getResult(jsonObject,"success").toString());
        printWriter.flush();
        printWriter.close();
    }



}
