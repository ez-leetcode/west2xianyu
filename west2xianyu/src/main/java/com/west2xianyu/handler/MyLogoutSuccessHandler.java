package com.west2xianyu.handler;

import com.alibaba.fastjson.JSONObject;
import com.west2xianyu.utils.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

//注销成功后被调用
@Slf4j
@Component
public class MyLogoutSuccessHandler implements LogoutSuccessHandler {



    @Override
    public void onLogoutSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        //删除redis缓存
        httpServletResponse.setHeader("Content-Type","application/json;charset=utf-8");
        JSONObject jsonObject = new JSONObject();
        PrintWriter printWriter = httpServletResponse.getWriter();
        printWriter.write(ResultUtils.getResult(jsonObject,"logoutSuccess").toString());
        printWriter.flush();
        printWriter.close();
    }
}
