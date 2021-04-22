package com.west2xianyu.handler;


import com.alibaba.fastjson.JSONObject;
import com.west2xianyu.utils.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


//登录失败后被调用
@Slf4j
@Component
public class MyAuthenticationFailureHandler implements AuthenticationFailureHandler {


    @Override
    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        //同样返回一个403
        //相应状态，可能会有权限问题
        //返回json格式
        //登录失败情况，可能用户名或者密码错误
        //
        httpServletResponse.setHeader("Content-Type","application/json;charset=utf-8");
        JSONObject jsonObject = new JSONObject();
        PrintWriter printWriter = httpServletResponse.getWriter();
        printWriter.write(ResultUtils.getResult(jsonObject,"userWrong").toString());
        printWriter.flush();
        printWriter.close();
    }
}
