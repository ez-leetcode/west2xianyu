package com.west2xianyu.handler;


import com.alibaba.fastjson.JSONObject;
import com.west2xianyu.mapper.UserMapper;
import com.west2xianyu.utils.JwtUtils;
import com.west2xianyu.utils.RedisUtils;
import com.west2xianyu.utils.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;


//登录成功后被调用
@Slf4j
@Component
public class MyAuthenticationSuccessHandler implements AuthenticationSuccessHandler {


    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private UserMapper userMapper;

    //登录成功
    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        httpServletResponse.setHeader("Content-Type", "application/json;charset=utf-8");
        PrintWriter printWriter = httpServletResponse.getWriter();
        JSONObject jsonObject = new JSONObject();
        //注意，这里不是pojo的user
        User user = (User) authentication.getPrincipal();
        com.west2xianyu.pojo.User user1 = userMapper.selectUser(user.getUsername());
        if(user1.getDeleted() == 1){
            if(user1.getReopenDate().before(new Date())){
                //重开日期在之前，说明账号已解封
                //解封账号
                userMapper.reopenId(user1.getId());
            }else{
                //账号未解封情况，告诉前台frozenWrong
                jsonObject.put("reopenDate",user1.getReopenDate());
                printWriter.write(ResultUtils.getResult(jsonObject,"frozenWrong").toString());
                printWriter.flush();
                printWriter.close();
                return ;
            }
        }
        //生成新的token
        String token = JwtUtils.createToken(user.getUsername(),user.getPassword());
        log.info("新生成token：" + token);
        //保存token，一小时可用
        redisUtils.saveByTime(user.getUsername(),token,1);
        jsonObject.put("token",token);
        //输出信息
        printWriter.write(ResultUtils.getResult(jsonObject, "loginSuccess").toString());
        printWriter.flush();
        printWriter.close();
    }

}
