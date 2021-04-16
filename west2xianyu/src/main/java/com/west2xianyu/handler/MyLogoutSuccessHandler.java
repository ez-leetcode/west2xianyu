package com.west2xianyu.handler;

import com.alibaba.fastjson.JSONObject;
import com.west2xianyu.pojo.User;
import com.west2xianyu.utils.RedisUtils;
import com.west2xianyu.utils.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public void onLogoutSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        httpServletResponse.setHeader("Content-Type","application/json;charset=utf-8");
        //获取用户实例
        User user = (User) authentication.getPrincipal();
        log.info("正在注销用户：" + user.getId());
        //销毁redis中的token
        redisUtils.delete(user.getId());
        JSONObject jsonObject = new JSONObject();
        PrintWriter printWriter = httpServletResponse.getWriter();
        printWriter.write(ResultUtils.getResult(jsonObject,"logoutSuccess").toString());
        printWriter.flush();
        printWriter.close();
    }

}
